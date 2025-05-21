package com.bookintok.bookintokfront.ui.responses

import com.bookintok.bookintokfront.ui.model.Usuario
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioResponse(
    val status: String,
    val usuario: Usuario
)