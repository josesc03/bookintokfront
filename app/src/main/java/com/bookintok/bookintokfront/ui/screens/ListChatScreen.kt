package com.bookintok.bookintokfront.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.bookintok.bookintokfront.ui.responses.ChatItem
import com.bookintok.bookintokfront.ui.responses.ChatListResponse
import com.bookintok.bookintokfront.ui.responses.WebSocketRequest
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListChatScreen(navController: NavController) {
    var user = FirebaseAuth.getInstance().currentUser

    var userUid = user?.uid

    val context = LocalContext.current
    val chats by remember { derivedStateOf { ListChatWebSocketManager.chatList } }

    LaunchedEffect(Unit) {

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

        ListChatWebSocketManager.connect(idToken)
    }

    DisposableEffect(Unit) {
        onDispose {
            ListChatWebSocketManager.disconnect()
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

                Text(
                    text = "Intercambios activos",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(16.dp)
                )

                if (!chats.isEmpty()) {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(chats) { chatItem ->
                            ChatItemUi(chatItem = chatItem, navController)
                            HorizontalDivider()
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 66.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Aquí aparecerán los intercambios que tengas en marcha. ¡Es hora de que empieces una nueva aventura literaria!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                MenuInferior(navController = navController, 1, userUid.toString())
            }

        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatItemUi(chatItem: ChatItem, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = {
                navController.navigate(Screen.Chat.createRoute(chatItem.chatId.toString()))
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = chatItem.imagenLibroUrl,
            contentDescription = "Imagen del chat",
            placeholder = painterResource(id = R.drawable.book_placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .border(
                    2.dp,
                    Color.Black.copy(alpha = 0.6f),
                    androidx.compose.foundation.shape.CircleShape
                ),
            error = painterResource(id = R.drawable.book_placeholder)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${chatItem.nombreUsuario} - ${chatItem.tituloLibro}",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatTimestamp(chatItem.timestampUltimoMensaje),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (chatItem.esMio) "Tú: ${chatItem.ultimoMensaje}" else chatItem.ultimoMensaje,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

object ListChatWebSocketManager {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val _chatListState = mutableStateListOf<ChatItem>()
    val chatList: List<ChatItem> get() = _chatListState

    fun connect(token: String) {
        val request = Request.Builder()
            .url("ws://192.168.1.135:8080/ws/chats")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                val request = WebSocketRequest("get_chats")
                ws.send(json.encodeToString(request))
            }

            override fun onMessage(ws: WebSocket, text: String) {
                val response = json.decodeFromString<ChatListResponse>(text)

                when (response.type) {
                    "chat_list" -> {
                        response.chats?.let {
                            _chatListState.clear()
                            _chatListState.addAll(it.sortedByDescending { it.timestampUltimoMensaje })
                        }
                    }
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
