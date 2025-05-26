package com.bookintok.bookintokfront.ui.responses

import com.bookintok.bookintokfront.ui.model.EstadoLibro
import com.bookintok.bookintokfront.ui.model.TipoCubierta
import kotlinx.serialization.Serializable

@Serializable
data class BookRequest(
    val titulo: String,
    val autor: String,
    val idioma: String,
    val cubierta: TipoCubierta,
    val categoriaPrincipal: String,
    val categoriaSecundaria: String? = null,
    val estado: EstadoLibro,
    val imagenUrl: String? = null,
    val descripcion: String? = null
)