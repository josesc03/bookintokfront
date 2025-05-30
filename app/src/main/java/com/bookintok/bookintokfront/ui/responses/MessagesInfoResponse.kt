package com.bookintok.bookintokfront.ui.responses

import com.bookintok.bookintokfront.ui.model.EstadoIntercambio
import com.bookintok.bookintokfront.ui.utils.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class MessageInfoResponse(
    val type: String,
    val messages: List<MessageInfo>? = null,
    val hasUserConfirmedExchange: Boolean,
    val estadoIntercambio: EstadoIntercambio,
    val message: MessageInfo? = null
)

@Serializable
data class MessageInfo(
    val contenido: String,
    val id_usuario_emisor: String,
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
)