package com.bookintok.bookintokfront.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun PointScreen(
    onPointSelected: (LatLng) -> Unit,
    onBack: () -> Unit
) {
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }
    val initialPosition = LatLng(40.4168, -3.7038)
    val cameraPositionState = rememberCameraPositionState {
        position =
            com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(initialPosition, 5f)
    }
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            checkLocationPermission(context)
        )
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            requestLocationPermissions(context as ComponentActivity)
        }
    }

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
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            GoogleMap(
                modifier = Modifier.fillMaxHeight(.75f).padding(bottom = 8.dp),
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    selectedPosition = it
                }
            ) {
                selectedPosition?.let { position ->
                    com.google.maps.android.compose.Marker(
                        state = com.google.maps.android.compose.MarkerState(position = position),
                        title = "Posición seleccionada"
                    )
                }
            }

            Column (modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        selectedPosition?.let { onPointSelected(it) }
                    },
                    enabled = selectedPosition != null,
                    modifier = Modifier.padding(top = 8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xffb3d0be),
                        contentColor = Color.Black.copy(alpha = 0.6f),

                        disabledContainerColor = Color(0xffd0d0d0),
                        disabledContentColor = Color.Black.copy(alpha = 0.3f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
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
                    onClick = { onBack() },
                    modifier = Modifier.padding(8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
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

private fun checkLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

private fun requestLocationPermissions(activity: ComponentActivity) {
    activity.requestPermissions(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        LOCATION_PERMISSION_REQUEST_CODE
    )
}

private const val LOCATION_PERMISSION_REQUEST_CODE = 123

@Preview(showBackground = true)
@Composable
fun PointScreenPreview() {
    PointScreen(
        onPointSelected = { /* Mock or do nothing */ },
        onBack = { /* Mock or do nothing */ }
    )
}