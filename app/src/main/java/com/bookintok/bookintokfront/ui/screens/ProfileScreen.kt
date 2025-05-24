package com.bookintok.bookintokfront.ui.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.model.Libro
import com.bookintok.bookintokfront.ui.model.Usuario
import com.bookintok.bookintokfront.ui.model.Valoracion
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController(), "test")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(navController: NavController, uid: String) {

    var usuario by remember { mutableStateOf<Usuario?>(null) }

    var libros by remember { mutableStateOf<List<Libro>?>(null) }

    val valoraciones by remember { mutableStateOf<List<Valoracion>?>(null) }
    val mediaValoraciones by remember { mutableFloatStateOf(0f) }
    val cantidadValoraciones by remember { mutableIntStateOf(0) }

    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        getUserFromApi(
            onSuccess = { usuario = it },
            onError = { error = it },
            uid = uid
        )

        getValoracionesFromApi(
            onSuccess = { },
            onError = { },
            uid = uid
        )

        getLibrosFromUserApi(
            onSuccess = { libros = it },
            onError = { error = it },
            uid = uid
        )
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

                Spacer(Modifier.height(72.dp))

                usuario?.let {
                    ShowUser(it, mediaValoraciones, cantidadValoraciones)
                }

                HorizontalDivider()

                Row {

                }

            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                MenuInferior(navController = navController, 2)
            }

        }

    }

}

fun getLibrosFromUserApi(onSuccess: () -> Unit, onError: () -> Unit, uid: String) {
    //TODO
}

fun getValoracionesFromApi(onSuccess: () -> Unit, onError: () -> Unit, uid: String) {
    //TODO
}

fun getUserFromApi(onSuccess: () -> Unit, onError: () -> Unit, uid: String) {
    //TODO
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowUser(usuario: Usuario, mediaValoraciones: Float, cantidadValoraciones: Int) {

    val instant = usuario.fechaRegistro

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

    val fecha = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    val fechaFormateada = fecha.format(formatter)

    Box(Modifier.fillMaxWidth().padding(8.dp)) {


        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings icon",
            tint = Color.Black.copy(.6f)
        )

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

        Column {
            AsyncImage(
                model = usuario.imagenUrl,
                contentDescription = "Imagen del usuario: ${usuario.nombre}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.user_placeholder)
            )

            Text(usuario.nombre)

            Row {
                RatingBar(mediaValoraciones)
                Spacer(Modifier.width(8.dp))
                Text("($cantidadValoraciones)")
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
                tint = Color.Yellow,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
