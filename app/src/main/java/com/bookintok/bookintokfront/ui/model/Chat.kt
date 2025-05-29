package com.bookintok.bookintokfront.ui.model

import com.bookintok.bookintokfront.ui.utils.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Chat(
    val id: Int,
    val uidUsuarioOfertante: String,
    val uidUsuarioInteresado: String,
    val idLibro: String,
    @Serializable(with = InstantSerializer::class)
    val fechaCreacion: Instant
)