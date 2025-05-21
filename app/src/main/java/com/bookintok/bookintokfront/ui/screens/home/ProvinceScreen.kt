package com.bookintok.bookintokfront.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.google.android.gms.maps.model.LatLng

data class Province(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

@Preview(showBackground = true)
@Composable
fun ProvinceScreenPreview() {
    ProvinceScreen(navController = rememberNavController())
}

@Composable
fun ProvinceScreen(navController: NavController) {
    var selectedLocality by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var showMapDialog by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), bottom = paddingValues.calculateBottomPadding()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Seleccionar provincia",
                modifier = Modifier
                    .background(Color(0xffb3d0be))
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            Column (modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {

                Text("Para seleccionar una provincia primero tienes que seleccionar la comunidad de la misma",
                    textAlign = TextAlign.Center,
                    color = Color.Black.copy(alpha = 0.6f))

                Spacer(modifier = Modifier.height(16.dp))

                DropdownSelector(
                    label = "Selecciona una comunidad",
                    options = localidades.keys.toList(),
                    selectedOption = selectedLocality,
                    onOptionSelected = { locality ->
                        selectedLocality = locality
                        selectedProvince = null
                    },
                    enabled = true
                )


                val provinceOptions = if (selectedLocality.isNotEmpty()) {
                    localidades[selectedLocality] ?: emptyList()
                } else {
                    emptyList()
                }

                Spacer(modifier = Modifier.height(16.dp))

                DropdownSelector(
                    label = "Selecciona una provincia",
                    options = provinceOptions.map { it.name },
                    selectedOption = selectedProvince?.name ?: "",
                    onOptionSelected = { provinceName ->
                        selectedProvince = provinceOptions.find { it.name == provinceName }
                    },
                    enabled = selectedLocality.isNotEmpty(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = selectedProvince?.name?.let { "Provincia seleccionada: $it" } ?: "Selecciona una provincia para mostrar",
                    modifier = Modifier.padding(top = 16.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { if (selectedProvince != null) showMapDialog = true
                              selectedPosition = LatLng(selectedProvince!!.latitude, selectedProvince!!.longitude)},
                    enabled = selectedProvince != null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffb3d0be),
                        disabledContainerColor = Color(0xffe0e0e0),
                        disabledContentColor = Color(0xff808080)
                    ),
                    border = BorderStroke(1.dp, if (selectedProvince != null) Color.Black.copy(alpha = 0.6f) else Color(0xFF808080))
                ) {
                    Text(
                        "Continuar",
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xff006025)),
                    border = BorderStroke(1.dp, Color(0xff006025))
                ) {
                    Text("Volver")
                }
            }
        }

        if (showMapDialog && selectedProvince != null) {
            MapaDialog(
                selectedPosition = selectedPosition!!,
                onDismiss = { selectedPosition = null },
                onPointSelected = {
                    updateLocation(latlng = it, onSuccess = {
                        navController.navigate(Screen.Main.route)
                    }, onError = {
                        selectedPosition = null
                    })
                }
            )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF1A1A1A),
                unfocusedTextColor = Color(0xFF4A4A4A),
                focusedContainerColor = Color(0xFFf5f5f5),
                unfocusedContainerColor = Color(0xFFf5f5f5),
                disabledContainerColor = Color(0xFFe0e0e0),
                disabledTextColor = Color(0xFF808080),
                focusedBorderColor = Color(0xFF7AA289),
                unfocusedBorderColor = Color(0xFFAEBDB4),
                focusedLabelColor = Color(0xFF7AA289),
                unfocusedLabelColor = Color(0xFFAEBDB4),
                cursorColor = Color(0xFF7AA289)
            ),
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                .fillMaxWidth(),
            enabled = enabled,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


val provincias = listOf(
    // Andalucía
    Province("Sevilla", 37.3886, -5.9823),
    Province("Málaga", 36.7213, -4.4213),
    Province("Córdoba", 37.8847, -4.779),
    Province("Granada", 37.1773, -3.5986),
    Province("Almería", 36.834, -2.4637),
    Province("Jaén", 37.7796, -3.7849),
    Province("Huelva", 37.2614, -6.9447),
    Province("Cádiz", 36.5164, -6.2991),

    // Aragón
    Province("Zaragoza", 41.6488, -0.8891),
    Province("Huesca", 42.1362, -0.4087),
    Province("Teruel", 40.3448, -1.1065),

    // Asturias
    Province("Oviedo", 43.3623, -5.8474),

    // Islas Baleares
    Province("Palma", 39.5696, 2.6502),

    // Islas Canarias
    Province("Las Palmas", 28.1235, -15.4363),
    Province("Santa Cruz de Tenerife", 28.4632, -16.2518),

    // Cantabria
    Province("Santander", 43.4623, -3.8099),

    // Castilla y León
    Province("Valladolid", 41.6522, -4.7245),
    Province("León", 42.5987, -5.5669),
    Province("Burgos", 42.3439, -3.6969),
    Province("Salamanca", 40.9701, -5.6635),
    Province("Zamora", 41.5036, -5.7443),
    Province("Palencia", 42.0125, -4.5329),
    Province("Ávila", 40.6565, -4.6818),
    Province("Segovia", 40.948, -4.1184),
    Province("Soria", 41.7636, -2.4662),

    // Castilla-La Mancha
    Province("Toledo", 39.8628, -4.0273),
    Province("Albacete", 38.9943, -1.8585),
    Province("Ciudad Real", 38.9857, -3.9275),
    Province("Cuenca", 40.0718, -2.1362),
    Province("Guadalajara", 40.6324, -3.1618),

    // Cataluña
    Province("Barcelona", 41.3879, 2.16992),
    Province("Girona", 41.9794, 2.8214),
    Province("Lleida", 41.6176, 0.6200),
    Province("Tarragona", 41.1189, 1.2445),

    // Extremadura
    Province("Badajoz", 38.8794, -6.9707),
    Province("Cáceres", 39.4702, -6.3724),

    // Galicia
    Province("A Coruña", 43.3623, -8.4115),
    Province("Lugo", 43.0121, -7.5558),
    Province("Ourense", 42.3356, -7.863),
    Province("Pontevedra", 42.4289, -8.6435),

    // La Rioja
    Province("Logroño", 42.2871, -2.5396),

    // Comunidad de Madrid
    Province("Madrid", 40.4168, -3.7038),

    // Región de Murcia
    Province("Murcia", 37.9922, -1.1307),

    // Navarra
    Province("Pamplona", 42.8125, -1.6458),

    // País Vasco
    Province("Bilbao", 43.263, -2.9349),
    Province("San Sebastián", 43.3129, -1.978),
    Province("Vitoria-Gasteiz", 42.8534, -2.6725),

    // Comunidad Valenciana
    Province("Valencia", 39.4699, -0.3763),
    Province("Alicante", 38.3452, -0.481),
    Province("Castellón", 39.9864, -0.0513)
)

val localidades = mapOf(
    "Andalucía" to provincias.filter {
        it.name in listOf(
            "Sevilla",
            "Málaga",
            "Córdoba",
            "Granada",
            "Almería",
            "Jaén",
            "Huelva",
            "Cádiz"
        )
    },
    "Aragón" to provincias.filter { it.name in listOf("Zaragoza", "Huesca", "Teruel") },
    "Asturias" to provincias.filter { it.name == "Oviedo" },
    "Islas Baleares" to provincias.filter { it.name == "Palma" },
    "Islas Canarias" to provincias.filter {
        it.name in listOf(
            "Las Palmas",
            "Santa Cruz de Tenerife"
        )
    },
    "Cantabria" to provincias.filter { it.name == "Santander" },
    "Castilla y León" to provincias.filter {
        it.name in listOf(
            "Valladolid",
            "León",
            "Burgos",
            "Salamanca",
            "Zamora",
            "Palencia",
            "Ávila",
            "Segovia",
            "Soria"
        )
    },
    "Castilla-La Mancha" to provincias.filter {
        it.name in listOf("Toledo", "Albacete", "Ciudad Real", "Cuenca", "Guadalajara")
    },
    "Cataluña" to provincias.filter {
        it.name in listOf("Barcelona", "Girona", "Lleida", "Tarragona")
    },
    "Extremadura" to provincias.filter { it.name in listOf("Badajoz", "Cáceres") },
    "Galicia" to provincias.filter {
        it.name in listOf(
            "A Coruña",
            "Lugo",
            "Ourense",
            "Pontevedra"
        )
    },
    "La Rioja" to provincias.filter { it.name == "Logroño" },
    "Comunidad de Madrid" to provincias.filter { it.name == "Madrid" },
    "Región de Murcia" to provincias.filter { it.name == "Murcia" },
    "Navarra" to provincias.filter { it.name == "Pamplona" },
    "País Vasco" to provincias.filter {
        it.name in listOf("Bilbao", "San Sebastián", "Vitoria-Gasteiz")
    },
    "Comunidad Valenciana" to provincias.filter {
        it.name in listOf("Valencia", "Alicante", "Castellón")
    }
)

