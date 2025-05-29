package com.bookintok.bookintokfront.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    var sliderValue by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffccdfd3)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "BOOKINTOK",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xff006025),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Intercambia libros,\nconecta historias\n",
            modifier = Modifier.padding(top = 48.dp),
            textAlign = TextAlign.Center,
            color = Color(0xff525954)
        )

        VerticalSlider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                if (sliderValue == 1f) {
                    if (firebaseAuth.currentUser != null) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            checkLocationFromUID(navController)
                        }
                    }
                }
            },
            modifier = Modifier.padding(72.dp),
            inactiveTrackColor = Color(0xffaebdb4),
            activeTrackColor = Color(0xff7aa289)
        )

    }
}

@Composable
fun VerticalSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    activeTrackColor: Color = Color(0xFF4CAF50),
    inactiveTrackColor: Color = Color(0xFFE0E0E0),
    enabled: Boolean = true
) {
    val trackWidthDp = 72.dp

    val density = LocalDensity.current
    val trackWidthPx = with(density) { trackWidthDp.toPx() }
    val thumbRadiusPx = trackWidthPx / 2f

    Box(
        modifier = modifier
            .width(trackWidthDp + 20.dp)
            .height(250.dp)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectDragGestures { change, _ ->
                    val y = change.position.y
                    val height = size.height.toFloat()
                    val newValue = (1f - y / height).coerceIn(0f, 1f)
                    onValueChange(newValue)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasHeight = size.height
            val trackX = size.width / 2
            val thumbY = (1f - value) * canvasHeight

            val alpha = if (enabled) 1f else 0.6f

            drawLine(
                color = inactiveTrackColor.copy(alpha = alpha),
                start = Offset(trackX, 0f),
                end = Offset(trackX, canvasHeight),
                strokeWidth = trackWidthPx,
                cap = StrokeCap.Round
            )

            drawLine(
                color = activeTrackColor.copy(alpha = alpha),
                start = Offset(trackX, canvasHeight),
                end = Offset(trackX, thumbY),
                strokeWidth = trackWidthPx,
                cap = StrokeCap.Round
            )

            drawCircle(
                color = activeTrackColor,
                center = Offset(trackX, thumbY),
                radius = thumbRadiusPx
            )

            val arrowSizeY = thumbRadiusPx * 0.25f
            val arrowSizeX = thumbRadiusPx * 0.45f
            val arrowColor = Color(0xFF314137)

            drawLine(
                color = arrowColor.copy(alpha = alpha),
                start = Offset(trackX - arrowSizeX, thumbY + arrowSizeY),
                end = Offset(trackX, thumbY - arrowSizeY),
                strokeWidth = 10f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = arrowColor.copy(alpha = alpha),
                start = Offset(trackX, thumbY - arrowSizeY),
                end = Offset(trackX + arrowSizeX, thumbY + arrowSizeY),
                strokeWidth = 10f,
                cap = StrokeCap.Round
            )
        }
    }
}


@Preview
@Composable
fun VerticalSliderPreview() {
    HomeScreen(navController = rememberNavController());
}