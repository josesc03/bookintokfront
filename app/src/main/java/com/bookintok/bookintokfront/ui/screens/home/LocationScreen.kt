package com.bookintok.bookintokfront.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
import com.google.maps.android.compose.currentCameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationScreen(navController: NavHostController, onPointSelected: (LatLng) -> Unit) {
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }

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
            text = "Para comenzar debe de activar los permisos de ubicación, seleccionar una provincia o seleccionar un punto en el mapa",
            textAlign = TextAlign.Center,
            color = Color.Black.copy(alpha = 0.6f)
        )
        Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = "ACTIVAR UBICACIÓN",
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 16.sp
            )
            Button(
                onClick = @androidx.annotation.RequiresPermission(anyOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
                    val fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(navController.context)
                    if (ActivityCompat.checkSelfPermission(
                            navController.context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            navController.context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                selectedPosition = LatLng(it.latitude, it.longitude)
                            }
                        }
                    } else {
                        ActivityCompat.requestPermissions(
                            navController.context as Activity,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            1
                        )
                    }
                    } ,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(72.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xffb3d0be)
                ),
                border = androidx.compose.foundation.BorderStroke(
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
        Column  (modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                Text(
                    text = "Prefiero seleccionar un punto",
                    color = Color.Black,
                    modifier = Modifier.padding(top = 24.dp),
                )
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Point.route) },
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xff006025)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xff006025)
                    )
                ) {
                    Text(text = "Seleccionar punto")
                }
            }
            Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                Text(
                    text = "Prefiero seleccionar una provincia",
                    color = Color.Black,
                    modifier = Modifier.padding(top = 24.dp)
                )
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Province.route) },
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xff006025)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
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
                onPointSelected = onPointSelected)
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
                position = CameraPosition.fromLatLngZoom(selectedPosition, 5f)
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
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xffb3d0be)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    ) {
                        Text("Continuar")
                    }

                    OutlinedButton (
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDismiss,
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xff006025)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
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