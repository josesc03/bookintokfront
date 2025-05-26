package com.bookintok.bookintokfront.ui.responses

import com.bookintok.bookintokfront.ui.model.Valoracion
import kotlinx.serialization.Serializable

@Serializable
data class ValoracionesResponse(
    val status: String,
    val valoraciones: List<Valoracion>
)
