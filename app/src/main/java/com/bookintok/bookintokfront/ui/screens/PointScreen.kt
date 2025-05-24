package com.bookintok.bookintokfront.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Preview(showBackground = true)
@Composable
fun PointScreenPreview() {
    PointScreen(navController = rememberNavController())
}

@Composable
fun PointScreen(
    navController: NavController
) {
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }
    val initialPosition = LatLng(40.4168, -3.7038)
    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(initialPosition, 5f)
    }
    val context = LocalContext.current


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Seleccionar punto",
                modifier = Modifier
                    .background(Color(0xffb3d0be))
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            GoogleMap(
                modifier = Modifier.fillMaxHeight(.75f).padding(bottom = 8.dp),
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    selectedPosition = it
                }
            ) {
                selectedPosition?.let { position ->
                    Marker(
                        state = MarkerState(position = position),
                        title = "Posición seleccionada"
                    )
                }
            }

            Column (modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        updateLocation(latlng = selectedPosition, onSuccess = {
                            navController.navigate(Screen.Main.route)
                        }, onError = {
                            selectedPosition = null
                        })

                    },
                    enabled = selectedPosition != null,
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffb3d0be),
                        contentColor = Color.Black.copy(alpha = 0.6f),

                        disabledContainerColor = Color(0xffd0d0d0),
                        disabledContentColor = Color.Black.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                ) {
                    Text("Continuar")
                }
                Text(
                    text = "Quiero seleccionar otra opción",
                    modifier = Modifier.padding(top = 24.dp),
                    color = Color.Black
                )
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xff006025)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xff006025)
                    )
                ) {
                    Text("Volver")
                }
            }
        }
    }
}