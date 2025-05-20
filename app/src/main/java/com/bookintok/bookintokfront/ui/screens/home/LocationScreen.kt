package com.bookintok.bookintokfront.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationScreen(navController: NavHostController, onPointSelected: (LatLng) -> Unit) {
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }

    if (!hasLocationPermission) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Se necesitan permisos de ubicación para continuar",
                color = Color.Black.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xffb3d0be),
                    contentColor = Color.Black.copy(alpha = 0.6f),
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Text("Activar Ubicación")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                modifier = Modifier.padding(30.dp),
                text = "Seleccione su ubicación, seleccione un punto o seleccione una provincia para continuar.",
                textAlign = TextAlign.Center,
                color = Color.Black.copy(alpha = 0.6f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SELECCIONAR MI UBICACIÓN",
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp),
                    fontSize = 16.sp
                )
                Button(
                    onClick = {
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                selectedPosition = LatLng(it.latitude, it.longitude)
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffb3d0be)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription = "Thumb Up Icon",
                        tint = Color.Black.copy(alpha = 0.4f)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "Prefiero seleccionar un punto",
                        color = Color.Black,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.Point.route) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xff006025)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xff006025)
                        )
                    ) {
                        Text(text = "Seleccionar punto")
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "Prefiero seleccionar una provincia",
                        color = Color.Black,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.Province.route) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xff006025)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xff006025)
                        )
                    ) {
                        Text(text = "Seleccionar provincia")
                    }
                }
            }

            if (selectedPosition != null) {
                MapaDialog(
                    selectedPosition = selectedPosition!!,
                    onDismiss = { selectedPosition = null },
                    onPointSelected = onPointSelected
                )
            }
        }
    }
}

@Composable
fun MapaDialog(
    selectedPosition: LatLng,
    onDismiss: () -> Unit,
    onPointSelected: (LatLng) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(selectedPosition, 6f)
            }

            Column(
                modifier = Modifier.fillMaxSize().background(Color.White),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            zoomGesturesEnabled = false,
                            scrollGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            mapToolbarEnabled = false
                        ),
                        properties = MapProperties(
                            isMyLocationEnabled = false,
                            mapType = MapType.NORMAL
                        )
                    ) {
                        Marker(
                            state = MarkerState(position = selectedPosition),
                            title = "Ubicación seleccionada"
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onPointSelected(selectedPosition) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xffb3d0be)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    ) {
                        Text("Continuar",
                            color = Color.Black.copy(alpha = 0.6f))
                    }

                    OutlinedButton (
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xff006025)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xff006025)
                        )
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}