package com.bookintok.bookintokfront.ui.model

import com.bookintok.bookintokfront.ui.utils.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import kotlin.time.ExperimentalTime

enum class EstadoIntercambio {
    PENDIENTE,
    ACEPTADO,
    COMPLETADO,
    CANCELADO
}

@Serializable
data class Intercambio @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val idChat: Int,
    val estado: EstadoIntercambio,
    val confirmadoOfertante: Boolean,
    val confirmadoInteresado: Boolean,
    @Serializable(with = InstantSerializer::class)
    val fechaCreacion: Instant,
)