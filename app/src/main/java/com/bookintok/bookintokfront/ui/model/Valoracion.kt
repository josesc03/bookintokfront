package com.bookintok.bookintokfront.ui.model

import com.bookintok.bookintokfront.ui.utils.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

data class Valoracion (
    val id: Int,
    val uidUsuarioValorado: String,
    val uidUsuarioQueValora: String,
    val puntuacion: Int,
    val comentario: String?,
    @Serializable(with = InstantSerializer::class)
    val fechaValoracion: Instant
)