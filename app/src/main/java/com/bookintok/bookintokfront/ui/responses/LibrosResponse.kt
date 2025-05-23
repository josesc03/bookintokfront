package com.bookintok.bookintokfront.ui.responses

import com.bookintok.bookintokfront.ui.model.Libro
import kotlinx.serialization.Serializable

@Serializable
data class LibrosResponse(
    val status: String,
    val libros: List<Libro> = emptyList()
)