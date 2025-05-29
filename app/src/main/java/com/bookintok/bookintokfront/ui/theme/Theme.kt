package com.bookintok.bookintokfront.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFb3d0be),
    secondary = Color(0xFF006025),
    tertiary = Color(0xffe6f0ea),

    onPrimary = Color.Black.copy(.4f),

    background = Color.White,
    onBackground = Color.Black

)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}