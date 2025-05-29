package com.bookintok.bookintokfront.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.model.Libro
import com.bookintok.bookintokfront.ui.model.Usuario
import com.bookintok.bookintokfront.ui.model.Valoracion
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.bookintok.bookintokfront.ui.responses.LibrosResponse
import com.bookintok.bookintokfront.ui.responses.UsuarioResponse
import com.bookintok.bookintokfront.ui.responses.ValoracionesResponse
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

    var libros by remember { mutableStateOf<List<Libro>?>(null) }

    var valoraciones by remember { mutableStateOf<List<Valoracion>?>(null) }
    var mediaValoraciones by remember { mutableFloatStateOf(0f) }
    var cantidadValoraciones by remember { mutableIntStateOf(0) }

    var errorUsuario by remember { mutableStateOf<String?>(null) }
    var errorValoraciones by remember { mutableStateOf<String?>(null) }
    var errorLibros by remember { mutableStateOf<String?>(null) }

    var mostrarLibros by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (userUid == uid) isCurrentUserProfile = true

        getUserFromApi(
            onSuccess = {
                usuario = it
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
                libros = it
            },
            onError = { errorLibros = it },
            uid = uid
        )
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
                        mediaValoraciones,
                        cantidadValoraciones,
                        currentUserUid = uid,
                        userInfoUid = userUid
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
                    if (libros == null && errorLibros == null) {
                        CircularProgressIndicator()
                    } else {
                        ShowLibros(libros, navController)
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

        }

    }

}

@Composable
fun ShowValoraciones(valoraciones: List<Valoracion>?) {
    if (valoraciones?.isEmpty() == true || valoraciones == null) {
        Text(
            "No se encontraron valoraciones",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 55.dp, top = 16.dp)
    ) {
        items(valoraciones) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var nombreValorador = "Usuario"
                getNombreFromApi(it.uidUsuarioQueValora) {
                    nombreValorador = it
                }
                Column {
                    Row {
                        Text(nombreValorador)
                        Spacer(Modifier.weight(1f))
                        RatingBar(it.puntuacion.toFloat())
                    }
                    Text(it.comentario.toString())
                }
            }
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
                        client.get("http://10.0.2.2:8080/usuario/$uid/nombre") {
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
fun ShowLibros(libros: List<Libro>?, navController: NavController) {
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
                LibroCard(libros[index], navController)
                Spacer(Modifier.height(8.dp))
            }
        }
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
                    val response: HttpResponse = client.get("http://10.0.2.2:8080/usuario/$uid") {
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
                        client.get("http://10.0.2.2:8080/valoraciones/$uid") {
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

fun getLibrosFromUserApi(onSuccess: (List<Libro>) -> Unit, onError: (String) -> Unit, uid: String) {
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
                    val response: HttpResponse = client.get("http://10.0.2.2:8080/libros/$uid") {
                        header("Authorization", "Bearer $idToken")
                    }

                    if (response.status.isSuccess()) {
                        val response = response.body<LibrosResponse>()
                        val libros = response.libros
                        onSuccess(libros)
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
    mediaValoraciones: Float,
    cantidadValoraciones: Int,
    currentUserUid: String,
    userInfoUid: String
) {
    val context = LocalContext.current

    var valorarUsuario by remember { mutableStateOf(false) }

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
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings icon",
                tint = Color.Black.copy(.6f)
            )
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
                model = usuario.imagenUrl,
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
                        if (hasCompletedExchange(userInfoUid) && !hasUserRated(userInfoUid))
                            valorarUsuario = true
                        else
                            Toast.makeText(
                                context,
                                "No puedes valorar a este usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                    },
                    border = BorderStroke(1.dp, Color.Black.copy(.4f)),
                ) {
                    Text("Valorar")
                }
            }
        }

        if (valorarUsuario) {
            RatingDialog(
                username = usuario.nombre,
                onCancel = { valorarUsuario = false },
                onAccept = { valorarUsuario = false }
            )
        }

    }

}

private fun hasCompletedExchange(userUid: String): Boolean {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        return false
    }

    var valueToReturn = true

    user.getIdToken(true)
        .addOnSuccessListener { result ->
            val idToken = result.token
            if (idToken == null) {
                return@addOnSuccessListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: HttpResponse =
                        client.get("http://10.0.2.2:8080/has-completed-exchange/$userUid") {
                            header("Authorization", "Bearer $idToken")
                        }

                    if (response.status.isSuccess()) {
                        valueToReturn = response.body<Boolean>()
                    } else {
                        withContext(Dispatchers.Main) {
                            return@withContext
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        return@withContext
                    }
                } finally {
                    client.close()
                }
            }

        }

    return valueToReturn
}

private fun hasUserRated(userUid: String): Boolean {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        return false
    }

    var valueToReturn = true

    user.getIdToken(true)
        .addOnSuccessListener { result ->
            val idToken = result.token
            if (idToken == null) {
                return@addOnSuccessListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: HttpResponse =
                        client.get("http://10.0.2.2:8080/has-rated/$userUid") {
                            header("Authorization", "Bearer $idToken")
                        }

                    if (response.status.isSuccess()) {
                        valueToReturn = response.body<Boolean>()
                    } else {
                        withContext(Dispatchers.Main) {
                            return@withContext
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        return@withContext
                    }
                } finally {
                    client.close()
                }
            }

        }

    return valueToReturn

}

@Composable
fun RatingDialog(
    username: String,
    onCancel: () -> Unit,
    onAccept: (Int) -> Unit
) {
    var rating by remember { mutableStateOf(1) } // mínimo 1

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
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
                    tint = Color(0xFFFFC107), // color dorado
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            rating = i
                        }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }

            Button(onClick = { onAccept(rating) }) {
                Text("Aceptar")
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
