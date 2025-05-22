package com.bookintok.bookintokfront.ui.responses

import com.bookintok.bookintokfront.ui.model.Libro
import kotlinx.serialization.Serializable

@Serializable
data class LibroResponse(
    val status: String,
    val usuario: Libro
)
