package com.bookintok.bookintokfront.ui.responses

import kotlinx.serialization.Serializable

@Serializable
data class WebSocketRequest(val action: String)