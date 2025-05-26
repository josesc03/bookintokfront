package com.bookintok.bookintokfront.ui.model

import com.bookintok.bookintokfront.ui.utils.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import kotlin.time.ExperimentalTime

@Serializable
data class Valoracion @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val uidUsuarioValorado: String,
    val uidUsuarioQueValora: String,
    val puntuacion: Int,
    val comentario: String?,
    @Serializable(with = InstantSerializer::class)
    val fechaValoracion: Instant
)