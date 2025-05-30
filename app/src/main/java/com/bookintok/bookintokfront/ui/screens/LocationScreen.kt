package com.bookintok.bookintokfront.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    LocationScreen(navController = rememberNavController())
}

@Composable
fun LocationScreen(navController: NavHostController) {
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

    var showMapDialog by remember { mutableStateOf(false) }

    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }

    if (!hasLocationPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
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
                        val fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(context)

                        val locationRequest = LocationRequest.Builder(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            1000L
                        ).setMaxUpdates(1).build()

                        val locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                val location = locationResult.lastLocation
                                if (location != null) {
                                    selectedPosition = LatLng(location.latitude, location.longitude)
                                    showMapDialog = true
                                }
                                fusedLocationClient.removeLocationUpdates(this)
                            }
                        }

                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
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
                    ),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = "Location Icon",
                        tint = Color.Black.copy(alpha = 0.4f)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
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
                ) {
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

            if (showMapDialog) {
                MapaDialog(
                    selectedPosition = selectedPosition!!,
                    onDismiss = { showMapDialog = false },
                    onPointSelected = {
                        updateLocation(latlng = it, onSuccess = {
                            navController.navigate(Screen.Main.route)
                        }, onError = {
                            showMapDialog = false
                        })
                    }
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
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
                        Text(
                            "Continuar",
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }

                    OutlinedButton(
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

fun updateLocation(
    latlng: LatLng?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val latitud = latlng?.latitude
    val longitud = latlng?.longitude

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onError("Usuario no autenticado")
        return
    }

    user.getIdToken(true)
        .addOnSuccessListener { result ->
            val idToken = result.token
            if (idToken == null) {
                onError("No se pudo obtener el token de Firebase")
                return@addOnSuccessListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: HttpResponse =
                        client.post("http://10.0.2.2:8080/update-location") {
                            //val response: HttpResponse = client.post("http://192.168.1.23:8080/update-location") {
                            header("Authorization", "Bearer $idToken")
                            contentType(ContentType.Application.Json)
                            setBody(
                                mapOf(
                                    "latitud" to latitud,
                                    "longitud" to longitud
                                )
                            )
                        }


                    if (response.status.isSuccess()) {
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    } else {
                        val errorBody = response.bodyAsText()
                        withContext(Dispatchers.Main) {
                            onError("Error HTTP ${response.status.value}: $errorBody")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onError("Excepción: ${e.localizedMessage}")
                    }
                } finally {
                    client.close()
                }
            }
        }
        .addOnFailureListener { exception ->
            onError("Error al obtener token: ${exception.localizedMessage}")
        }
}