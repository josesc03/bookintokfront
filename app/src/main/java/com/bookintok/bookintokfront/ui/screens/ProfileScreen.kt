package com.bookintok.bookintokfront.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.model.Usuario
import com.bookintok.bookintokfront.ui.model.Valoracion
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.bookintok.bookintokfront.ui.responses.LibroInfo
import com.bookintok.bookintokfront.ui.responses.LibroInfoResponse
import com.bookintok.bookintokfront.ui.responses.UsuarioResponse
import com.bookintok.bookintokfront.ui.responses.ValoracionesResponse
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController(), "test")
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(navController: NavController, uid: String) {
    var userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var isCurrentUserProfile by remember { mutableStateOf(false) }

    var usuario by remember { mutableStateOf<Usuario?>(null) }

    var librosInfo by remember { mutableStateOf<List<LibroInfo>?>(null) }

    var hasUserRated by remember { mutableStateOf(false) }
    var hasCompletedExchange by remember { mutableStateOf(false) }

    var valoraciones by remember { mutableStateOf<List<Valoracion>?>(null) }
    var mediaValoraciones by remember { mutableFloatStateOf(0f) }
    var cantidadValoraciones by remember { mutableIntStateOf(0) }

    var valorarUsuario = remember { mutableStateOf(false) }

    var errorUsuario by remember { mutableStateOf<String?>(null) }
    var errorValoraciones by remember { mutableStateOf<String?>(null) }
    var errorLibros by remember { mutableStateOf<String?>(null) }

    var mostrarLibros by remember { mutableStateOf(true) }

    var imagenUrl by remember { mutableStateOf<String?>(null) }
    var nombre by remember { mutableStateOf("") }
    var showUserConfig = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val activity = LocalActivity.current as ComponentActivity
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                val bmp = uriToBitmap(context, uri)
                bitmap.value = bmp
            }
        }
    }

    LaunchedEffect(Unit) {
        if (userUid == uid) isCurrentUserProfile = true

        getUserFromApi(
            onSuccess = {
                usuario = it
                nombre = it.nombre
                imagenUrl = it.imagenUrl
            },
            onError = { errorUsuario = it },
            uid = uid
        )

        getValoracionesFromApi(
            onSuccess = {
                valoraciones = it
                mediaValoraciones = valoraciones?.map { it.puntuacion }?.average()?.toFloat() ?: 0f
                cantidadValoraciones = valoraciones?.size ?: 0
            },
            onError = { errorValoraciones = it },
            uid = uid
        )

        getLibrosFromUserApi(
            onSuccess = {
                librosInfo = it
            },
            onError = { errorLibros = it },
            uid = uid
        )

        if (!isCurrentUserProfile) {
            hasCompletedExchange = hasCompletedExchange(uid)

            hasUserRated = hasUserRated(uid)

            println("$hasCompletedExchange - $hasUserRated")
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            HeaderBookintok()

            Column {

                Spacer(Modifier.height(72.dp))


                if (usuario == null && errorUsuario == null) {
                    CircularProgressIndicator()
                }

                usuario?.let {
                    ShowUserInfo(
                        it,
                        imagenUrl,
                        mediaValoraciones,
                        cantidadValoraciones,
                        currentUserUid = uid,
                        userInfoUid = userUid,
                        hasCompletedExchange = hasCompletedExchange,
                        hasUserRated = hasUserRated,
                        valorarUsuario = valorarUsuario,
                        showUserConfig = showUserConfig
                    )
                }

                HorizontalDivider()

                Row(Modifier.fillMaxWidth()) {
                    val librosTextColor =
                        if (mostrarLibros) Color.Black else Color.Black.copy(alpha = 0.6f)
                    val valoracionesTextColor =
                        if (!mostrarLibros) Color.Black else Color.Black.copy(alpha = 0.6f)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(Color.Transparent)
                            .drawBehind {
                                val strokeWidth = 2.dp.toPx()
                                val y = size.height - strokeWidth / 2
                                if (mostrarLibros) {
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = strokeWidth
                                    )
                                }
                            }
                            .clickable { mostrarLibros = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Libros", color = librosTextColor)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(Color.Transparent)
                            .drawBehind {
                                val strokeWidth = 2.dp.toPx()
                                val y = size.height - strokeWidth / 2
                                if (!mostrarLibros) {
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = strokeWidth
                                    )
                                }
                            }
                            .clickable { mostrarLibros = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Valoraciones",
                            color = valoracionesTextColor
                        )
                    }
                }

                HorizontalDivider()

                if (mostrarLibros) {
                    if (librosInfo == null && errorLibros == null) {
                        CircularProgressIndicator()
                    } else {
                        ShowLibros(librosInfo, navController)
                    }
                } else {
                    if (valoraciones == null && errorValoraciones == null) {
                        CircularProgressIndicator()
                    } else {
                        Spacer(Modifier.height(16.dp))
                        ShowValoraciones(valoraciones)
                    }
                }


            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (uid == userUid) {
                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 48.dp)
                                .clip(
                                    RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.Black.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    navController.navigate(Screen.EditBook.createRoute(null))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Icon",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    MenuInferior(
                        navController = navController,
                        if (uid == userUid) 2 else 3,
                        userUid
                    )
                }

            }

            if (valorarUsuario.value) {
                RatingDialog(
                    username = usuario?.nombre ?: "",
                    onCancel = { valorarUsuario.value = false },
                    onAccept = {
                        valorarUsuarioFromApi(
                            uid,
                            comentario = it["comentario"],
                            rating = it["rating"]?.toInt() ?: 0,
                            onSuccess = {
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Se ha valorado al usuario correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    getValoracionesFromApi(
                                        onSuccess = {
                                            valoraciones = it
                                            mediaValoraciones =
                                                valoraciones?.map { it.puntuacion }?.average()
                                                    ?.toFloat() ?: 0f
                                            cantidadValoraciones = valoraciones?.size ?: 0
                                        },
                                        onError = { errorValoraciones = it },
                                        uid = uid
                                    )

                                    hasCompletedExchange = hasCompletedExchange(uid)
                                    hasUserRated = hasUserRated(uid)

                                    valorarUsuario.value = false
                                }
                            },
                            onError = {
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "No se ha podido valorar al usuario",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                valorarUsuario.value = false
                            }
                        )
                    }
                )
            }

            if (showUserConfig.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .zIndex(100f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(24.dp)
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Configuración del usuario")
                        Spacer(modifier = Modifier.height(16.dp))

                        if (bitmap.value != null) {
                            Image(
                                bitmap = bitmap.value!!.asImageBitmap(),
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = imagenUrl,
                                contentDescription = "Imagen de perfil de: ${usuario?.nombre}",
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.user_placeholder),
                                placeholder = painterResource(id = R.drawable.user)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = {
                                    ImagePicker.with(activity)
                                        .crop(1f, 1f)
                                        .compress(1024)
                                        .maxResultSize(1080, 720)
                                        .galleryOnly()
                                        .createIntent { intent ->
                                            imagePickerLauncher.launch(intent)
                                        }
                                },
                                border = BorderStroke(1.dp, Color.Black.copy(.6f))
                            ) {
                                Text("Cambiar imagen")
                            }
                            if (bitmap.value != null || imagenUrl != null) {
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        bitmap.value = null
                                        imagenUrl = null
                                    },
                                    border = BorderStroke(1.dp, Color.Black.copy(.6f))
                                ) { Text("Borrar") }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre de usuario") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF4A4A4A),
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFEEEEEE),
                                focusedBorderColor = Color(0xFF7AA289),
                                unfocusedBorderColor = Color(0xFFAEBDB4),
                                focusedLabelColor = Color(0xFF7AA289),
                                unfocusedLabelColor = Color(0xFFAEBDB4),
                                cursorColor = Color(0xFF7AA289)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    showUserConfig.value = false
                                    imagenUrl = null
                                    nombre = usuario?.nombre ?: ""
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xff006025)
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = Color(0xff006025)
                                ),
                                enabled = (nombre != usuario?.nombre || bitmap.value != null)
                            ) {
                                Text("Cancelar")
                            }
                            Button(
                                onClick = {
                                    val datosActualizados = mutableMapOf<String, String>()

                                    if (bitmap.value != null) {
                                        subirImagen(
                                            bitmap.value!!,
                                            "a6cf3bbee48752e0d178068ae93dac11",
                                            callback = { success, response ->
                                                if (success) {
                                                    val json = JSONObject(response.toString())
                                                    imagenUrl =
                                                        json.getJSONObject("data").getString("url")
                                                } else {
                                                    println("Error al subir imagen: $response")
                                                }

                                                imagenUrl?.let {
                                                    datosActualizados["imageUrl"] = it
                                                }
                                                if (nombre != usuario?.nombre) {
                                                    datosActualizados["nombre"] = nombre
                                                }

                                                updateUser(
                                                    datosActualizados,
                                                    onSuccess = {
                                                        getUserFromApi(
                                                            onSuccess = {
                                                                usuario = it
                                                                nombre = it.nombre
                                                            },
                                                            onError = { errorUsuario = it },
                                                            uid = uid
                                                        )
                                                        Toast.makeText(
                                                            context,
                                                            "Usuario actualizado",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    },
                                                    onError = {
                                                        Toast.makeText(
                                                            context,
                                                            "Error al actualizar",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                )
                                            }
                                        )
                                    } else {
                                        if (nombre != usuario?.nombre || imagenUrl != usuario?.imagenUrl) {
                                            datosActualizados["nombre"] = nombre
                                        }

                                        if (datosActualizados.isNotEmpty()) {
                                            updateUser(
                                                datosActualizados,
                                                onSuccess = {
                                                    getUserFromApi(
                                                        onSuccess = {
                                                            usuario = it
                                                            nombre = it.nombre
                                                        },
                                                        onError = { errorUsuario = it },
                                                        uid = uid
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "Usuario actualizado",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                onError = {
                                                    Toast.makeText(
                                                        context,
                                                        "Error al actualizar",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                    }

                                    showUserConfig.value = false
                                    imagenUrl = null
                                },
                                border = BorderStroke(1.dp, Color.Black.copy(.6f))
                            ) {
                                Text("Confirmar")
                            }

                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Login.route) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            border = BorderStroke(1.dp, Color.Black.copy(.6f))
                        ) {
                            Text(text = "Cerrar sesión")
                        }

                    }
                }
            }

        }

    }

}

fun updateUser(
    data: Map<String, String>,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
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
                val response: HttpResponse = client.post("http://192.168.1.135:8080/usuario/update") {
                    header("Authorization", "Bearer $idToken")
                    contentType(ContentType.Application.Json)
                    setBody(data)
                }

                if (response.status.isSuccess()) {
                    withContext(Dispatchers.Main) {
                        onSuccess()
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
                    println("Excepción: ${e.localizedMessage}")
                    onError("Excepción: ${e.localizedMessage}")
                }
            } finally {
                client.close()
            }
        }
    }.addOnFailureListener { exception ->
        onError("Error al obtener token: ${exception.localizedMessage}")
    }

}

fun valorarUsuarioFromApi(

    userUid: String,
    rating: Int,
    comentario: String? = null,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onError()
        return
    }

    user.getIdToken(true)
        .addOnSuccessListener { result ->
            val idToken = result.token
            if (idToken == null) {
                return@addOnSuccessListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: HttpResponse =
                        client.post("http://192.168.1.135:8080/usuario/valorar/$userUid") {
                            header("Authorization", "Bearer $idToken")
                            contentType(ContentType.Application.Json)
                            setBody(
                                mapOf(
                                    "puntuacion" to rating.toString(),
                                    "comentario" to comentario
                                )
                            )
                        }
                    if (response.status.isSuccess()) {
                        onSuccess()
                    } else {
                        println("Error HTTP ${response.status.value}")
                        onError()
                    }
                } catch (e: Exception) {
                    println("Excepción: ${e.localizedMessage}")
                    onError()
                }
            }
        }
}

@Composable
fun ShowValoraciones(valoraciones: List<Valoracion>?) {
    if (valoraciones.isNullOrEmpty()) {
        Text(
            "No se encontraron valoraciones",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 50.dp)
        ) {
            items(valoraciones) { valoracion ->
                var nombreUsuario by remember { mutableStateOf("") }

                LaunchedEffect(valoracion.uidUsuarioQueValora) {
                    getNombreFromApi(
                        valoracion.uidUsuarioQueValora,
                        onSuccess = {
                            nombreUsuario = it
                        }
                    )
                }

                ValoracionItem(valoracion, nombreUsuario)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ValoracionItem(valoracion: Valoracion, nombreUsuario: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = nombreUsuario,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                RatingBar(valoracion.puntuacion.toFloat())
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valoracion.comentario ?: "El usuario no ha dejado ningun comentario",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 100.dp),
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun getNombreFromApi(uid: String, onSuccess: (String) -> Unit) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    val user = FirebaseAuth.getInstance().currentUser

    user?.getIdToken(true)
        ?.addOnSuccessListener { result ->
            val idToken = result.token
            if (idToken == null) {
                return@addOnSuccessListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: HttpResponse =
                        client.get("http://192.168.1.135:8080/usuario/$uid/nombre") {
                            header("Authorization", "Bearer $idToken")
                        }

                    if (response.status.isSuccess()) {
                        val response = response.body<String>()
                        onSuccess(response)
                    } else {
                        val errorBody = response.bodyAsText()
                        withContext(Dispatchers.Main) {
                            System.err.println("Error HTTP ${response.status.value}: $errorBody")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        System.err.println("Excepción: ${e.localizedMessage}")
                    }
                } finally {
                    client.close()
                }
            }

        }
}

@Composable
fun ShowLibros(libros: List<LibroInfo>?, navController: NavController) {
    if (libros.isNullOrEmpty()) {
        Text(
            "No se encontraron libros",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 50.dp)
        ) {
            items(libros.size) { index ->
                if (index == 0) {
                    Spacer(Modifier.height(16.dp))
                }
                LibroProfileCard(libros[index], navController)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Stable
@Composable
fun LibroProfileCard(libro: LibroInfo, navController: NavController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(16.dp)
            .clickable(onClick = {
                if (!libro.isCompleted) {
                    navController.navigate(Screen.DetailBook.createRoute(libro.id.toString()))
                } else {
                    Toast.makeText(
                        context,
                        "Este libro ya ha sido intercambiado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    ) {
        Box {
            if (libro.isCompleted) {
                Image(
                    painter = painterResource(id = R.drawable.book_intercambiado),
                    contentDescription = "Marco de intercambiado: ${libro.titulo}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 2f)
                        .clip(RoundedCornerShape(8.dp))
                        .zIndex(100f)
                        .alpha(.9f),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            AsyncImage(
                model = libro.url,
                contentDescription = "Portada del libro: ${libro.titulo}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 2f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.book_placeholder)
            )
        }
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


fun getUserFromApi(onSuccess: (Usuario) -> Unit, onError: (String) -> Unit, uid: String) {
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
                        client.get("http://192.168.1.135:8080/usuario/$uid") {
                            header("Authorization", "Bearer $idToken")
                        }

                    if (response.status.isSuccess()) {
                        val response = response.body<UsuarioResponse>()
                        val usuario = response.usuario
                        onSuccess(usuario)
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

fun getValoracionesFromApi(
    onSuccess: (List<Valoracion>) -> Unit,
    onError: (String) -> Unit,
    uid: String
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
                        client.get("http://192.168.1.135:8080/usuario/valoraciones/$uid") {
                            header("Authorization", "Bearer $idToken")
                        }

                    if (response.status.isSuccess()) {
                        val response = response.body<ValoracionesResponse>()
                        val valoraciones = response.valoraciones
                        onSuccess(valoraciones)
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

fun getLibrosFromUserApi(
    onSuccess: (List<LibroInfo>) -> Unit,
    onError: (String) -> Unit,
    uid: String
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
                        client.get("http://192.168.1.135:8080/libros/$uid") {
                            header("Authorization", "Bearer $idToken")
                        }

                    if (response.status.isSuccess()) {
                        val libroInfoResponse = response.body<LibroInfoResponse>()
                        println(libroInfoResponse)
                        val libros = libroInfoResponse.libros
                        withContext(Dispatchers.Main) {
                            onSuccess(libros)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowUserInfo(
    usuario: Usuario,
    imagenUrl: String?,
    mediaValoraciones: Float,
    cantidadValoraciones: Int,
    valorarUsuario: MutableState<Boolean>,
    currentUserUid: String,
    userInfoUid: String,
    hasCompletedExchange: Boolean = false,
    hasUserRated: Boolean = false,
    showUserConfig: MutableState<Boolean>
) {
    val context = LocalContext.current

    val instant = usuario.fechaRegistro

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

    val fecha = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    val fechaFormateada = fecha.format(formatter)

    Box(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {


        if (userInfoUid == currentUserUid) {
            IconButton(onClick = { showUserConfig.value = true }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings icon",
                    tint = Color.Black.copy(.6f)
                )
            }
        }

        Row(
            Modifier.align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("desde $fechaFormateada", color = Color.Black.copy(.6f))
            Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Calendar icon",
                tint = Color.Black.copy(.6f)
            )
        }

        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            AsyncImage(
                model = imagenUrl ?: usuario.imagenUrl,
                contentDescription = "Imagen del usuario: ${usuario.nombre}",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black.copy(.6f), CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.user_placeholder)
            )

            Spacer(Modifier.height(8.dp))

            Text(usuario.nombre)

            Spacer(Modifier.height(8.dp))

            Row {
                RatingBar(mediaValoraciones)
                Spacer(Modifier.width(8.dp))
                Text("($cantidadValoraciones)")
            }

            if (userInfoUid != currentUserUid) {
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (hasCompletedExchange && !hasUserRated)
                            valorarUsuario.value = true
                        else
                            Toast.makeText(
                                context,
                                "No puedes valorar a este usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                    },
                    border = BorderStroke(1.dp, Color.Black.copy(.4f)),
                    enabled = hasCompletedExchange && !hasUserRated
                ) {
                    Text("Valorar")
                }
            }
        }

    }

}

suspend fun hasCompletedExchange(userUid: String): Boolean {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser ?: return false

    val idToken = user.getIdToken(true).await().token ?: return false

    return try {
        val response: HttpResponse =
            client.get("http://192.168.1.135:8080/has-completed-exchange/$userUid") {
                header("Authorization", "Bearer $idToken")
            }

        if (response.status.isSuccess()) {
            response.body<Boolean>()
        } else {
            false
        }
    } catch (e: Exception) {
        false
    } finally {
        client.close()
    }
}


suspend fun hasUserRated(userUid: String): Boolean {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser ?: return false

    val idToken = user.getIdToken(true).await().token ?: return false

    return try {
        val response: HttpResponse =
            client.get("http://192.168.1.135:8080/has-rated/$userUid") {
                header("Authorization", "Bearer $idToken")
            }

        if (response.status.isSuccess()) {
            response.body<Boolean>()
        } else {
            false
        }
    } catch (e: Exception) {
        false
    } finally {
        client.close()
    }
}


@Composable
fun RatingDialog(
    username: String,
    onCancel: () -> Unit,
    onAccept: (Map<String, String>) -> Unit
) {
    var rating by remember { mutableStateOf(1) } // mínimo 1

    var comentario by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .zIndex(100f),
        contentAlignment = Alignment.Center

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(24.dp)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Estás valorando a $username")

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Estrella $i",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                rating = i
                            }
                    )
                }
            }

            TextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Comentario (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp),
                maxLines = 5,
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1A1A1A),
                    unfocusedTextColor = Color(0xFF4A4A4A),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFEEEEEE),
                    focusedBorderColor = Color(0xFF7AA289),
                    unfocusedBorderColor = Color(0xFFAEBDB4),
                    focusedLabelColor = Color(0xFF7AA289),
                    unfocusedLabelColor = Color(0xFFAEBDB4),
                    cursorColor = Color(0xFF7AA289)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onCancel, border = BorderStroke(1.dp, Color.Black.copy(.4f)),
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        onAccept(
                            mapOf(
                                "rating" to rating.toString(),
                                "comentario" to comentario
                            )
                        )
                    },
                    border = BorderStroke(1.dp, Color.Black.copy(.4f)),
                ) {
                    Text("Aceptar")
                }
            }
        }
    }
}

@Composable
fun RatingBar(rating: Float, maxRating: Int = 5) {
    Row {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = Color.Black.copy(.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
