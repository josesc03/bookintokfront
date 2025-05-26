package com.bookintok.bookintokfront.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.model.Libro
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.bookintok.bookintokfront.ui.responses.LibrosResponse
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(navController = rememberNavController())
}

@Composable
fun MainScreen(navController: NavController) {
    var userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var libros by remember { mutableStateOf<List<Libro>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    var filtersVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }
    var categoriaPrincipal by remember { mutableStateOf("") }
    var categoriaSecundaria by remember { mutableStateOf("") }

    var idiomaSelected by remember { mutableStateOf("") }

    var estadoSelected by remember { mutableStateOf("") }

    var cubiertaSelected by remember { mutableStateOf("") }

    var distanciaSelected by remember { mutableStateOf(0f) }

    fun getAppliedFilters(): Map<String, Any> {
        val filtersMap = mutableMapOf<String, Any>()
        if (query.isNotBlank()) filtersMap["busqueda"] = query
        if (distanciaSelected > 0f) filtersMap["distancia"] = distanciaSelected.toInt()
        if (categoriaPrincipal.isNotBlank()) filtersMap["categoriaPrincipal"] = categoriaPrincipal
        if (categoriaSecundaria.isNotBlank()) filtersMap["categoriaSecundaria"] =
            categoriaSecundaria
        if (idiomaSelected.isNotBlank()) filtersMap["idioma"] = idiomaSelected
        if (estadoSelected.isNotBlank()) filtersMap["estado"] = estadoSelected
        if (cubiertaSelected.isNotBlank()) filtersMap["cubierta"] = cubiertaSelected
        return filtersMap
    }

    LaunchedEffect(Unit) {
        getLibrosFromApi(
            onSuccess = { libros = it.shuffled() },
            onError = { error = it },
            filters = getAppliedFilters()
        )
    }

    BackHandler(filtersVisible) {
        filtersVisible = false
        focusManager.clearFocus()
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                }
        ) {

            Box(
                modifier = Modifier
                    .background(Color(0xffb3d0be))
                    .fillMaxWidth()
                    .height(72.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "BOOKINTOK",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                )

            }

            Column {

                Spacer(modifier = Modifier.height(72.dp))

                Column(modifier = Modifier.padding(16.dp)) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xfff5f5f5))
                            .border(
                                shape = RoundedCornerShape(5.dp),
                                border = BorderStroke(1.dp, Color.Black.copy(alpha = .6f))
                            ),
                        verticalArrangement = Arrangement.Top
                    ) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = {
                                Text(
                                    "Buscar...",
                                    color = Color.Black.copy(alpha = .6f)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) filtersVisible = true
                                },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon",
                                    tint = Color.Black.copy(alpha = .6f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF1A1A1A),
                                focusedContainerColor = Color(0xFFf5f5f5),
                                unfocusedContainerColor = Color(0xFFf5f5f5),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = Color.Transparent,
                                unfocusedLabelColor = Color.Transparent,
                                cursorColor = Color(0xFF7AA289),
                            ),
                            singleLine = true,

                            )

                        AnimatedVisibility(
                            visible = filtersVisible,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(5.dp))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Distancia",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (distanciaSelected == 0f) "Distancia desactivada" else "${distanciaSelected.toInt()}km",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black.copy(alpha = 0.8f)
                                )

                                Slider(
                                    value = distanciaSelected,
                                    onValueChange = { distanciaSelected = it },
                                    valueRange = 0f..500f,
                                    modifier = Modifier.weight(1f)
                                )


                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    "Generos",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                DropdownSelector(
                                    label = "Genero principal",
                                    options = generos.map { it },
                                    selectedOption = categoriaPrincipal,
                                    onOptionSelected = {
                                        categoriaPrincipal = it
                                    },
                                    enabled = true,
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                DropdownSelector(
                                    label = "Genero secundario",
                                    options = generos.filter { it != categoriaPrincipal },
                                    selectedOption = categoriaSecundaria,
                                    onOptionSelected = {
                                        categoriaSecundaria = it
                                    },
                                    enabled = categoriaPrincipal.isNotEmpty(),
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    "Idioma",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                DropdownSelector(
                                    label = "Idioma",
                                    options = idiomas.map { it },
                                    selectedOption = idiomaSelected,
                                    onOptionSelected = {
                                        idiomaSelected = it
                                    },
                                    enabled = true,
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    "Estado",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(8.dp))


                                Row {
                                    listOf("Como nuevo", "Usado", "Antiguo").forEach { estado ->
                                        FilterChip(
                                            selected = estadoSelected == estado,
                                            onClick = { estadoSelected = estado },
                                            label = { Text(estado) },
                                            modifier = Modifier.padding(end = 8.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xffb3d0be)
                                            )
                                        )
                                    }
                                }


                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    "Cubierta",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row {
                                    listOf("Tapa dura", "Tapa blanda").forEach { cubierta ->
                                        FilterChip(
                                            selected = cubiertaSelected == cubierta,
                                            onClick = { cubiertaSelected = cubierta },
                                            label = { Text(cubierta) },
                                            modifier = Modifier.padding(end = 8.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xffb3d0be)
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Button(
                                    onClick = {
                                        filtersVisible = false
                                        getLibrosFromApi(
                                            onSuccess = { libros = it.shuffled() },
                                            onError = { error = it },
                                            filters = getAppliedFilters()
                                        )
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = Color.Black.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Text("Buscar")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (getAppliedFilters().isEmpty()) "LISTADO DE LIBROS" else "LIBROS FILTRADOS",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (error != null) {
                        Text("Error: $error", color = Color.Red)
                    } else {
                        if (libros.isEmpty()) {
                            Text("No se encontraron libros.")
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 130.dp)
                            ) {
                                items(libros) {
                                    LibroCard(it)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            Spacer(Modifier.height(130.dp))
                        }
                    }
                }

            }

            if (!filtersVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xffe6f0ea))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("¿Quieres cambiar tu ubicación?")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    navController.navigate(Screen.Location.route)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xffb3d0be)
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = Color.Black.copy(alpha = 0.6f)
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.map),
                                    contentDescription = "Location Icon",
                                    tint = Color.Black.copy(alpha = 0.4f)
                                )
                            }
                        }

                        MenuInferior(navController = navController, userUid = userUid)
                    }
                }
            }

        }
    }

}

@Stable
@Composable
fun LibroCard(libro: Libro) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        AsyncImage(
            model = libro.imagenUrl,
            contentDescription = "Portada del libro: ${libro.titulo}",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .width(133.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.book_placeholder)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = libro.titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Autor: ${libro.autor}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun MenuInferior(navController: NavController, index: Int = 0, userUid: String) {

    var explorerColor = Color(0xffb3d0be)
    var chatsColor = Color(0xffb3d0be)
    var userColor = Color(0xffb3d0be)

    when (index) {
        0 -> {
            explorerColor = Color(0xffd7d7d7)
        }

        1 -> {
            chatsColor = Color(0xffd7d7d7)
        }

        2 -> {
            userColor = Color(0xffd7d7d7)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .background(explorerColor)
                .weight(1f)
                .fillMaxHeight()
                .clickable(enabled = true, onClick = {
                    navController.navigate(Screen.Main.route)
                })
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val halfStroke = strokeWidth / 2
                    val color = Color.Black.copy(alpha = 0.4f)

                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = halfStroke),
                        end = Offset(x = size.width, y = halfStroke),
                        strokeWidth = strokeWidth
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.explore),
                contentDescription = "Explore Icon",
                tint = Color.Black.copy(alpha = 0.4f)
            )
        }
        Box(
            modifier = Modifier
                .background(chatsColor)
                .weight(1f)
                .fillMaxHeight()
                .clickable(enabled = true, onClick = {
                    navController.navigate(Screen.Chats.route)
                })
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val halfStroke = strokeWidth / 2
                    val color = Color.Black.copy(alpha = 0.4f)

                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = halfStroke),
                        end = Offset(x = size.width, y = halfStroke),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = color,
                        start = Offset(halfStroke, 0f),
                        end = Offset(halfStroke, size.height),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = color,
                        start = Offset(size.width - halfStroke, 0f),
                        end = Offset(size.width - halfStroke, size.height),
                        strokeWidth = strokeWidth
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.chats),
                contentDescription = "Chats Icon",
                tint = Color.Black.copy(alpha = 0.4f)
            )
        }
        Box(
            modifier = Modifier
                .background(userColor)
                .weight(1f)
                .fillMaxHeight()
                .clickable(enabled = true, onClick = {
                    navController.navigate(Screen.ProfilePage.createRoute(userUid))
                })
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val halfStroke = strokeWidth / 2
                    val color = Color.Black.copy(alpha = 0.4f)

                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = halfStroke),
                        end = Offset(x = size.width, y = halfStroke),
                        strokeWidth = strokeWidth
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.user),
                contentDescription = "User Icon",
                tint = Color.Black.copy(alpha = 0.4f)
            )
        }
    }

}

val generos = listOf(
    "Acción y Aventura",
    "Ciencia Ficción",
    "Fantasía",
    "Misterio y Suspense",
    "Romance",
    "Terror",
    "Thriller",
    "Histórica",
    "Contemporánea",
    "Distopía",
    "Humor y Sátira",
    "Literatura Clásica",
    "Poesía",
    "Drama",
    "Biografía y Autobiografía",
    "Ensayo",
    "Autoayuda y Desarrollo Personal",
    "Viajes y Aventuras Reales",
    "Historia",
    "Ciencia y Tecnología",
    "Filosofía",
    "Religión y Espiritualidad",
    "Arte y Fotografía",
    "Cocina y Gastronomía",
    "Salud y Bienestar",
    "Deportes y Actividades al Aire Libre",
    "Negocios y Finanzas",
    "Educación y Referencia",
    "Infantil",
    "Juvenil",
    "Cómics y Novelas Gráficas",
    "Manga",
    "Realismo Mágico",
    "Western",
    "Policial",
    "Espionaje",
    "Sobrenatural y Paranormal",
    "Steampunk",
    "Cyberpunk",
    "Mitología y Folclore",
    "Cuentos de Hadas",
    "Gótico",
    "Absurdo",
    "Experimental",
    "No Ficción Creativa",
    "Memorias",
    "Crítica Literaria",
    "Guiones de Cine y Televisión",
    "Juegos y Puzzles",
    "Libros Ilustrados",
    "Actividades",
    "Libros Interactivos",
    "Música",
    "Arquitectura y Diseño",
    "Jardinería",
    "Mascotas",
    "Bricolaje y Manualidades",
    "Idiomas",
    "Programación",
    "Marketing y Publicidad",
    "Psicología",
    "Sociología",
    "Política y Gobierno",
    "Economía",
    "Medio Ambiente y Naturaleza",
    "Astronomía",
    "Matemáticas"
)

val idiomas = listOf(
    "Español",
    "Inglés",
    "Francés",
    "Alemán",
    "Italiano",
    "Portugués"
)

fun getLibrosFromApi(
    onSuccess: (List<Libro>) -> Unit,
    onError: (String) -> Unit = {},
    filters: Map<String, Any>? = null
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onError("Usuario no autenticado")
        return
    }

    user.getIdToken(true).addOnSuccessListener { result ->
        val idToken = result.token
        if (idToken == null) {
            onError("No se pudo obtener el token de Firebase")
            return@addOnSuccessListener
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                var url = "http://10.0.2.2:8080/libro/allLibros"
                filters?.let {
                    val queryParams = it.map { (key, value) -> "$key=$value" }.joinToString("&")
                    if (queryParams.isNotEmpty()) url += "?$queryParams"
                }

                val response: HttpResponse = client.get(url) {
                    header("Authorization", "Bearer $idToken")
                    println("Requesting URL: $url with token: $idToken")
                }

                if (response.status.isSuccess()) {
                    val libros = response.body<LibrosResponse>()
                    withContext(Dispatchers.Main) {
                        onSuccess(libros.libros)
                    }
                } else {
                    val errorBody = response.bodyAsText()
                    withContext(Dispatchers.Main) {
                        println("Error HTTP ${response.status.value}: $errorBody")
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
}