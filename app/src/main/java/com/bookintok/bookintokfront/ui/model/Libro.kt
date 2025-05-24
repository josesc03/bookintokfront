package com.bookintok.bookintokfront.ui.model

import com.bookintok.bookintokfront.ui.utils.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import kotlin.time.ExperimentalTime

enum class EstadoLibro {
    DISPONIBLE,
    RESERVADO,
    INTERCAMBIADO
}

enum class TipoCubierta {
    TAPA_BLANDA,
    TAPA_DURA
}

@Serializable
data class Libro (
    val id: Int,
    val idUsuario: Int,
    val titulo: String,
    val autor: String,
    val idioma: String,
    val cubierta: TipoCubierta,
    val categoriaPrincipal: String,
    val categoriaSecundaria: String? = null,
    val estado: EstadoLibro,
    val imagenUrl: String?,
    @Serializable(with = InstantSerializer::class)
    val fechaPublicacion: Instant
)

