package com.bookintok.bookintokfront.ui.model

import com.bookintok.bookintokfront.ui.utils.BigDecimalSerializer
import com.bookintok.bookintokfront.ui.utils.InstantSerializer
import java.math.BigDecimal
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class Usuario @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val firebaseUid: String,
    val nickname: String,
    val nombre: String,
    val email: String,
    var imagenUrl: String?,
    @Serializable(with = BigDecimalSerializer::class)
    val ultimaLatitud: BigDecimal?,
    @Serializable(with = BigDecimalSerializer::class)
    val ultimaLongitud: BigDecimal?,
    @Serializable(with = BigDecimalSerializer::class)
    val valoracionPromedio: BigDecimal?,
    @Serializable(with = InstantSerializer::class)
    val fechaRegistro: java.time.Instant
) {
    fun hasCoordinates(): Boolean {
        return ultimaLatitud != null && ultimaLongitud != null
    }
}