package com.bookintok.bookintokfront.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bookintok.bookintokfront.ui.responses.ChatItem
import com.bookintok.bookintokfront.ui.responses.ChatListResponse
import com.bookintok.bookintokfront.ui.responses.WebSocketRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

@Preview(showBackground = true)
@Composable
fun ChatsScreenPreview() {
    ChatsScreen(navController = rememberNavController())
}

@Composable
fun ChatsScreen(navController: NavController) {
    var userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val chats by remember { derivedStateOf { ChatWebSocketManager.chatList } }

    LaunchedEffect(Unit) {
        ChatWebSocketManager.connect(userUid)
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
                            ChatItemUi(chatItem = chatItem)
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
                MenuInferior(navController = navController, 1, userUid)
            }

        }

    }
}

@Composable
fun ChatItemUi(chatItem: ChatItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = {
                //TODO: Implementar la navegación a la pantalla de chat
            })
    ) {
        AsyncImage(
            model = chatItem.imagenLibroUrl,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
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
                    text = chatItem.timestampUltimoMensaje.toString(),
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

object ChatWebSocketManager {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val _chatListState = mutableStateListOf<ChatItem>()
    val chatList: List<ChatItem> get() = _chatListState

    fun connect(token: String) {
        val request = Request.Builder()
            .url("ws://10.0.2.2:8080/ws/chats")
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

                    "chat_updated" -> {
                        response.chat?.let { updated ->
                            val index = _chatListState.indexOfFirst { it.chatId == updated.chatId }
                            if (index != -1) {
                                _chatListState[index] = updated
                            }
                        }
                    }

                    "chat_new" -> {
                        response.chat?.let {
                            _chatListState.add(0, it)
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
