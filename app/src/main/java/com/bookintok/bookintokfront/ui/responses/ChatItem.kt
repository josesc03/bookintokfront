package com.bookintok.bookintokfront.ui.responses

import com.bookintok.bookintokfront.ui.utils.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ChatItem(
    val chatId: Int,
    val tituloLibro: String,
    val imagenLibroUrl: String?,
    val nombreUsuario: String,
    val esMio: Boolean,
    val ultimoMensaje: String,
    @Serializable(with = InstantSerializer::class)
    val timestampUltimoMensaje: Instant
)