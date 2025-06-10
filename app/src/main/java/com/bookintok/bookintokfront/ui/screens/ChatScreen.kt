package com.bookintok.bookintokfront.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.model.EstadoIntercambio
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.bookintok.bookintokfront.ui.responses.MessageInfo
import com.bookintok.bookintokfront.ui.responses.MessageInfoResponse
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import dto.ChatInfo
import dto.ChatInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(navController: NavController, idChat: String) {
    var user = FirebaseAuth.getInstance().currentUser

    var userUid = user?.uid

    val idChat = idChat.toInt()

    var messageText by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    var chatInfo: ChatInfo? by remember { mutableStateOf<ChatInfo?>(null) }

    val listState = rememberLazyListState()

    val messages = ChatWebSocketManager.messageList

    val hasUserConfirmedExchange = ChatWebSocketManager.hasUserConfirmedExchange
    val estadoIntercambio = ChatWebSocketManager.estadoIntercambio

    var confirmarIntercambioDialog by remember { mutableStateOf(false) }


    if (estadoIntercambio.value == EstadoIntercambio.COMPLETADO
        || estadoIntercambio.value == EstadoIntercambio.CANCELADO
    ) {
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Main.route) {
                inclusive = true
            }
        }
    }

    BackHandler(isFocused) {
        focusManager.clearFocus()
    }

    LaunchedEffect(Unit) {
        getChatInfoFromApi(
            onSuccess = { chatInfo = it },
            chatId = idChat,
            onError = {
                Toast.makeText(
                    context,
                    "Error al cargar la información del chat",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        if (user == null) {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return@LaunchedEffect
        }

        val idToken = withContext(Dispatchers.IO) {
            try {
                Tasks.await(user.getIdToken(true)).token
            } catch (e: Exception) {
                null
            }
        }

        if (idToken == null) {
            Toast.makeText(context, "No se pudo obtener token", Toast.LENGTH_SHORT).show()
            return@LaunchedEffect
        }

        ChatWebSocketManager.connect(idToken, idChat)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }

        println(messages.toString())
        println(hasUserConfirmedExchange)
        println(estadoIntercambio)

    }

    DisposableEffect(Unit) {
        onDispose {
            ChatWebSocketManager.disconnect()
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HeaderBookintok()

            Column {

                Spacer(Modifier.height(72.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var menuExpanded by remember { mutableStateOf(false) }

                    AsyncImage(
                        model = chatInfo?.imagenLibroUrl,
                        contentDescription = "Imagen del chat",
                        placeholder = painterResource(id = R.drawable.book_placeholder),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(
                                2.dp,
                                Color.Black.copy(alpha = 0.6f),
                                CircleShape
                            ),
                        error = painterResource(id = R.drawable.book_placeholder)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = (chatInfo?.nombreUsuario?.take(15) ?: ""),
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = (chatInfo?.tituloLibro?.take(20) ?: ""),
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Spacer(Modifier.weight(1f))

                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Opciones del chat",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                )
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Ver perfil") },
                                    onClick = {
                                        navController.navigate(
                                            Screen.ProfilePage.createRoute(
                                                chatInfo?.uidUsuario
                                            )
                                        )
                                    })
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Borrar chat") },
                                    onClick = {
                                        cancelarIntercambioFromApi(
                                            idChat = idChat,
                                            onSuccess = {
                                                Toast.makeText(
                                                    context,
                                                    "Intercambio cancelado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.navigate(Screen.Main.route) {
                                                    popUpTo(Screen.Main.route) {
                                                        inclusive = true
                                                    }
                                                }
                                            },
                                            onError = { error ->
                                                Toast.makeText(
                                                    context,
                                                    "Error al cancelar: $error",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    })
                            }
                        }
                    }


                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    items(messages) { messageInfo ->
                        ChatMessageUi(
                            messageInfo = messageInfo,
                            isCurrentUser = messageInfo.id_usuario_emisor == userUid,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                        .heightIn(min = 60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Escribe aquí...") },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = 150.dp)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) isFocused = true
                            },
                        singleLine = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1A1A1A),
                            unfocusedTextColor = Color(0xFF4A4A4A),
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color(0xFF7AA289),
                            unfocusedBorderColor = Color(0xFFAEBDB4),
                            focusedLabelColor = Color(0xFF7AA289),
                            unfocusedLabelColor = Color(0xFFAEBDB4),
                            cursorColor = Color(0xFF7AA289)
                        )
                    )
                    IconButton(onClick = {
                        if (messageText.isNotBlank()) {
                            sendMessageFromApi(idChat, messageText)
                            messageText = ""
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Enviar",
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxSize()
                        )
                    }
                    Box {
                        IconButton(
                            onClick = {
                                confirmarIntercambioDialog = true
                            },
                            enabled = !hasUserConfirmedExchange.value
                        ) {
                            Icon(
                                Icons.Default.Autorenew,
                                tint = if (hasUserConfirmedExchange.value) MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.2f
                                ) else MaterialTheme.colorScheme.onPrimary,
                                contentDescription = "Rehacer",
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxSize()
                            )
                        }
                        if (!hasUserConfirmedExchange.value && estadoIntercambio.value == EstadoIntercambio.ACEPTADO) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                                    .clip(CircleShape)
                            ) {
                                Text("!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                }

                Spacer(Modifier.height(50.dp))
            }



            if (confirmarIntercambioDialog) {
                AlertDialog(
                    onDismissRequest = { confirmarIntercambioDialog = false },
                    title = { Text("Confirmar Intercambio") },
                    text = {
                        if (estadoIntercambio.value == EstadoIntercambio.ACEPTADO && !hasUserConfirmedExchange.value) {
                            Text(
                                "El otro usuario ya ha confirmado el intercambio. Esta acción es irreversible y no se puede cancelar. ¿Estás seguro de que deseas continuar?"
                            )
                        } else {
                            Text(
                                "Esta acción es irreversible a menos que canceles el intercambio cerrando el chat. ¿Estás seguro de que deseas continuar?"
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                confirmarIntercambioFromApi(
                                    idChat,
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Intercambio confirmado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onError = {
                                        Toast.makeText(
                                            context,
                                            "Error al confirmar: $it",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                                confirmarIntercambioDialog = false
                            },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                confirmarIntercambioDialog = false
                            },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Text("Cancelar")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {

                Spacer(Modifier.height(50.dp))
                MenuInferior(navController = navController, 3, userUid.toString())
            }

        }

    }
}

fun confirmarIntercambioFromApi(
    chatId: Int,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onError("Usuario no autenticado")
        return
    }

    user.getIdToken(true).addOnSuccessListener { result ->
        val idToken = result.token
        if (idToken == null) {
            onError("No se pudo obtener el token de Firebase")
            return@addOnSuccessListener
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "http://192.168.1.135:8080/chat/confirmar/$chatId"
                val response: HttpResponse = client.post(url) {
                    println("Requesting URL: $url with token: $idToken")
                    header("Authorization", "Bearer $idToken")
                }

                if (response.status.isSuccess()) {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    val errorBody = response.bodyAsText()
                    withContext(Dispatchers.Main) {
                        println("Error HTTP ${response.status.value}: $errorBody")
                        onError("Error HTTP ${response.status.value}: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Excepción: ${e.localizedMessage}")
                }
            } finally {
                client.close()
            }
        }
    }.addOnFailureListener {
        onError("Error al obtener token: ${it.localizedMessage}")
    }
}

fun sendMessageFromApi(
    idChat: Int,
    content: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
) {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onError("Usuario no autenticado")
        return
    }

    user.getIdToken(true).addOnSuccessListener { result ->
        val idToken = result.token
        if (idToken == null) {
            onError("No se pudo obtener el token de Firebase")
            return@addOnSuccessListener
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "http://192.168.1.135:8080/chat/send/$idChat"
                val response: HttpResponse = client.post(url) {
                    println("Requesting URL: $url with token: $idToken")
                    setBody(content)
                    header("Authorization", "Bearer $idToken")
                }

                if (response.status.isSuccess()) {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    val errorBody = response.bodyAsText()
                    withContext(Dispatchers.Main) {
                        println("Error HTTP ${response.status.value}: $errorBody")
                        onError("Error HTTP ${response.status.value}: $errorBody")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Excepción: ${e.localizedMessage}")
                }
            } finally {
                client.close()
            }
        }

    }.addOnFailureListener {
        onError("Error al obtener token: ${it.localizedMessage}")
    }

}

private fun cancelarIntercambioFromApi(
    idChat: Int,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onError("Usuario no autenticado")
        return
    }

    user.getIdToken(true).addOnSuccessListener { result ->
        val idToken = result.token
        if (idToken == null) {
            onError("No se pudo obtener el token de Firebase")
            return@addOnSuccessListener
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "http://192.168.1.135:8080/chat/cancel/$idChat"
                val response: HttpResponse = client.post(url) {
                    println("Requesting URL: $url with token: $idToken")
                    header("Authorization", "Bearer $idToken")
                }

                if (response.status.isSuccess()) {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    val errorBody = response.bodyAsText()
                    withContext(Dispatchers.Main) {
                        println("Error HTTP ${response.status.value}: $errorBody")
                        onError("Error HTTP ${response.status.value}: $errorBody")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Excepción: ${e.localizedMessage}")
                }
            } finally {
                client.close()
            }
        }

    }.addOnFailureListener {
        onError("Error al obtener token: ${it.localizedMessage}")
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMessageUi(messageInfo: MessageInfo, isCurrentUser: Boolean) {
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor =
        if (isCurrentUser) MaterialTheme.colorScheme.tertiary else Color(0xffdbdbdb)
    val textAlign = if (isCurrentUser) TextAlign.End else TextAlign.Start

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor, MaterialTheme.shapes.medium)
                .padding(
                    top = 8.dp,
                    bottom = 8.dp,
                    start = if (isCurrentUser) 8.dp else 16.dp,
                    end = if (isCurrentUser) 16.dp else 8.dp
                )
                .widthIn(max = LocalContext.current.resources.displayMetrics.widthPixels.dp * 0.25f),
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            Text(
                text = messageInfo.contenido,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = textAlign
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimestamp(messageInfo.timestamp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Light),
                textAlign = textAlign,
                modifier = Modifier.align(if (isCurrentUser) Alignment.Start else Alignment.End)
            )
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTimestamp(timestamp: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")
        .withZone(ZoneId.systemDefault())

    return formatter.format(timestamp)
}

fun getChatInfoFromApi(
    chatId: Int,
    onSuccess: (ChatInfo?) -> Unit,
    onError: (String) -> Unit = {}
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onError("Usuario no autenticado")
        return
    }

    user.getIdToken(true).addOnSuccessListener { result ->
        val idToken = result.token
        if (idToken == null) {
            onError("No se pudo obtener el token de Firebase")
            return@addOnSuccessListener
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "http://192.168.1.135:8080/chats/$chatId"
                val response: HttpResponse = client.get(url) {
                    println("Requesting URL: $url with token: $idToken")
                    header("Authorization", "Bearer $idToken")
                }

                if (response.status.isSuccess()) {
                    val chatResponse = response.body<ChatInfoResponse>()
                    withContext(Dispatchers.Main) {
                        onSuccess(chatResponse.chats)
                    }
                } else {
                    val errorBody = response.bodyAsText()
                    withContext(Dispatchers.Main) {
                        println("getChatFromApi: Error HTTP ${response.status.value}: $errorBody")
                        onError("Error HTTP ${response.status.value}: $errorBody")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Excepción: ${e.localizedMessage}")
                }
            } finally {
                client.close()
            }
        }

    }.addOnFailureListener {
        onError("Error al obtener token: ${it.localizedMessage}")
    }
}

object ChatWebSocketManager {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val _messageListState = mutableStateListOf<MessageInfo>()
    val messageList: SnapshotStateList<MessageInfo> get() = _messageListState

    private val _hasUserConfirmedExchange = mutableStateOf(false)
    val hasUserConfirmedExchange: MutableState<Boolean> get() = _hasUserConfirmedExchange

    private val _estadoIntercambio = mutableStateOf(EstadoIntercambio.PENDIENTE)
    val estadoIntercambio: MutableState<EstadoIntercambio> get() = _estadoIntercambio

    fun connect(token: String, idChat: Int) {
        val request = Request.Builder()
            .url("ws://192.168.1.135:8080/ws/messages/$idChat")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(ws: WebSocket, response: Response) {
                println("WebSocket conectado correctamente")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                try {
                    val response = json.decodeFromString<MessageInfoResponse>(text)
                    when (response.type) {
                        "message_list" -> {
                            _hasUserConfirmedExchange.value = response.hasUserConfirmedExchange
                            _estadoIntercambio.value = response.estadoIntercambio
                            response.messages?.let { messages ->
                                _messageListState.clear()
                                _messageListState.addAll(messages.sortedBy { it.timestamp })
                            }
                        }

                        else -> println("Tipo de mensaje desconocido: ${response.type}")
                    }
                } catch (e: Exception) {
                    println("Error al procesar el mensaje: ${e.localizedMessage}")
                }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                println("WebSocket error: ${t.message}")
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, null)
        webSocket = null
    }
}