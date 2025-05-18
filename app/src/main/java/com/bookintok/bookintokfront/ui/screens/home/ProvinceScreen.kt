package com.bookintok.bookintokfront.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

data class Province(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

val provinces = listOf(
    // Andalusia
    Province("Seville", 37.3886, -5.9823),
    Province("Malaga", 36.7213, -4.4213),
    Province("Cordoba", 37.8847, -4.779),
    Province("Granada", 37.1773, -3.5986),
    Province("Almeria", 36.834, -2.4637),
    Province("Jaen", 37.7796, -3.7849),
    Province("Huelva", 37.2614, -6.9447),
    Province("Cadiz", 36.5164, -6.2991),

    // Aragon
    Province("Zaragoza", 41.6488, -0.8891),
    Province("Huesca", 42.1362, -0.4087),
    Province("Teruel", 40.3448, -1.1065),

    // Asturias
    Province("Oviedo", 43.3623, -5.8474),

    // Balearic Islands
    Province("Palma", 39.5696, 2.6502),

    // Canary Islands
    Province("Las Palmas", 28.1235, -15.4363),
    Province("Santa Cruz de Tenerife", 28.4632, -16.2518),

    // Cantabria
    Province("Santander", 43.4623, -3.8099),

    // Castile and Leon
    Province("Valladolid", 41.6522, -4.7245),
    Province("Leon", 42.5987, -5.5669),
    Province("Burgos", 42.3439, -3.6969),
    Province("Salamanca", 40.9701, -5.6635),
    Province("Zamora", 41.5036, -5.7443),
    Province("Palencia", 42.0125, -4.5329),
    Province("Avila", 40.6565, -4.6818),
    Province("Segovia", 40.948, -4.1184),
    Province("Soria", 41.7636, -2.4662),

    // Castile-La Mancha
    Province("Toledo", 39.8628, -4.0273),
    Province("Albacete", 38.9943, -1.8585),
    Province("Ciudad Real", 38.9857, -3.9275),
    Province("Cuenca", 40.0718, -2.1362),
    Province("Guadalajara", 40.6324, -3.1618),

    // Catalonia
    Province("Barcelona", 41.3879, 2.16992),
    Province("Girona", 41.9794, 2.8214),
    Province("Lleida", 41.6176, 0.6200),
    Province("Tarragona", 41.1189, 1.2445),

    // Extremadura
    Province("Badajoz", 38.8794, -6.9707),
    Province("Caceres", 39.4702, -6.3724),

    // Galicia
    Province("A Coruna", 43.3623, -8.4115),
    Province("Lugo", 43.0121, -7.5558),
    Province("Ourense", 42.3356, -7.863),
    Province("Pontevedra", 42.4289, -8.6435),

    // La Rioja
    Province("Logrono", 42.2871, -2.5396),

    // Community of Madrid
    Province("Madrid", 40.4168, -3.7038),

    // Region of Murcia
    Province("Murcia", 37.9922, -1.1307),

    // Navarre
    Province("Pamplona", 42.8125, -1.6458),

    // Basque Country
    Province("Bilbao", 43.263, -2.9349),
    Province("San Sebastian", 43.3129, -1.978),
    Province("Vitoria-Gasteiz", 42.8534, -2.6725),

    // Valencian Community
    Province("Valencia", 39.4699, -0.3763),
    Province("Alicante", 38.3452, -0.481),
    Province("Castellon", 39.9864, -0.0513)
)

val localities = mapOf(
    "Andalusia" to provinces.filter {
        it.name in listOf(
            "Seville",
            "Malaga",
            "Cordoba",
            "Granada",
            "Almeria",
            "Jaen",
            "Huelva",
            "Cadiz"
        )
    },
    "Aragon" to provinces.filter { it.name in listOf("Zaragoza", "Huesca", "Teruel") },
    "Asturias" to provinces.filter { it.name == "Oviedo" },
    "Balearic Islands" to provinces.filter { it.name == "Palma" },
    "Canary Islands" to provinces.filter {
        it.name in listOf(
            "Las Palmas",
            "Santa Cruz de Tenerife"
        )
    },
    "Cantabria" to provinces.filter { it.name == "Santander" },
    "Castile and Leon" to provinces.filter {
        it.name in listOf(
            "Valladolid",
            "Leon",
            "Burgos",
            "Salamanca",
            "Zamora",
            "Palencia",
            "Avila",
            "Segovia",
            "Soria"
        )
    },
    "Castile-La Mancha" to provinces.filter {
        it.name in listOf("Toledo", "Albacete", "Ciudad Real", "Cuenca", "Guadalajara")
    },
    "Catalonia" to provinces.filter {
        it.name in listOf("Barcelona", "Girona", "Lleida", "Tarragona")
    },
    "Extremadura" to provinces.filter { it.name in listOf("Badajoz", "Caceres") },
    "Galicia" to provinces.filter {
        it.name in listOf(
            "A Coruna",
            "Lugo",
            "Ourense",
            "Pontevedra"
        )
    },
    "La Rioja" to provinces.filter { it.name == "Logrono" },
    "Community of Madrid" to provinces.filter { it.name == "Madrid" },
    "Region of Murcia" to provinces.filter { it.name == "Murcia" },
    "Navarre" to provinces.filter { it.name == "Pamplona" },
    "Basque Country" to provinces.filter {
        it.name in listOf("Bilbao", "San Sebastian", "Vitoria-Gasteiz")
    },
    "Valencian Community" to provinces.filter {
        it.name in listOf("Valencia", "Alicante", "Castellon")
    }
)

//REHACER SECCION DEL SELECTOR DE PROVINCIA

@Composable
fun ProvinceScreen(
    onPointSelected: (LatLng) -> Unit
) {
    var selectedRegion by remember { mutableStateOf<String?>(null) }
    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var showMap by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Selecciona tu ubicación",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (selectedRegion == null) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                localities.keys.forEach { region ->
                    OutlinedButton(
                        onClick = { selectedRegion = region },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(region)
                    }
                }
            }
        }
        else if (selectedProvince == null) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                localities[selectedRegion]?.forEach { province ->
                    OutlinedButton(
                        onClick = {
                            selectedProvince = province
                            showMap = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(province.name)
                    }
                }
            }

        }

        if (showMap && selectedProvince != null) {
            Dialog(onDismissRequest = { showMap = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val position = LatLng(selectedProvince!!.latitude, selectedProvince!!.longitude)
                        val cameraPositionState = rememberCameraPositionState()

                        GoogleMap(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(mapType = MapType.NORMAL)
                        ) {
                            Marker(
                                state = MarkerState(position = position),
                                title = selectedProvince!!.name
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = { showMap = false }
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    onPointSelected(position)
                                    showMap = false
                                }
                            ) {
                                Text("Confirmar ubicación")
                            }
                        }
                    }
                }
            }
        }
    }
}