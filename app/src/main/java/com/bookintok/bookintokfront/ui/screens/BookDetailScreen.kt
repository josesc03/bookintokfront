package com.bookintok.bookintokfront.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.model.EstadoLibro
import com.bookintok.bookintokfront.ui.model.Libro
import com.bookintok.bookintokfront.ui.model.TipoCubierta
import com.bookintok.bookintokfront.ui.model.Usuario
import com.bookintok.bookintokfront.ui.model.Valoracion
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun BookDetailScreenPreview() {
    Button(
        onClick = {},
        border = BorderStroke(1.dp, Color.Black.copy(.4f)),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .size(50.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.chats),
            contentDescription = "Chat Icon",
            tint = Color.Black.copy(.4f)
        )
    }
}

@Composable
fun BookDetailScreen(navController: NavController, bookId: String) {

    var userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var isBookOwner by remember { mutableStateOf(false) }

    var titulo by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var generoPrincipal by remember { mutableStateOf("") }
    var generoSecundario by remember { mutableStateOf("") }
    var idioma by remember { mutableStateOf("") }
    var cubierta by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf<String?>(null) }

    var errorMessage: String = ""

    var usuario: Usuario? = null
    var libro: Libro? = null

    var valoraciones by remember { mutableStateOf<List<Valoracion>?>(null) }
    var mediaValoraciones by remember { mutableFloatStateOf(0f) }
    var cantidadValoraciones by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {

        CoroutineScope(Dispatchers.IO).launch {

            libro = getLibroFromApi(bookId.toInt())

            getUserFromApi(uid = libro?.uidUsuario.toString(), onSuccess = {
                usuario = it
            }, onError = {
                errorMessage = it
            })

            getValoracionesFromApi(
                onSuccess = {
                    valoraciones = it
                    mediaValoraciones =
                        valoraciones?.map { it.puntuacion }?.average()?.toFloat() ?: 0f
                    cantidadValoraciones = valoraciones?.size ?: 0
                },
                onError = { errorMessage = it },
                uid = libro?.uidUsuario.toString()
            )

            if (libro != null) {
                titulo = libro?.titulo.toString()
                estado = when (libro?.estado) {
                    EstadoLibro.NUEVO -> "Nuevo"
                    EstadoLibro.COMO_NUEVO -> "Como nuevo"
                    EstadoLibro.USADO -> "Usado"
                    EstadoLibro.ANTIGUO -> "Antiguo"
                    else -> ""
                }
                cubierta = when (libro?.cubierta) {
                    TipoCubierta.TAPA_DURA -> "Tapa dura"
                    TipoCubierta.TAPA_BLANDA -> "Tapa blanda"
                    else -> ""
                }
                descripcion = libro?.descripcion ?: ""
                autor = libro?.autor.toString()
                generoPrincipal = libro?.categoriaPrincipal.toString()
                generoSecundario = libro?.categoriaSecundaria ?: ""
                idioma = libro?.idioma.toString()
                imagenUrl = libro?.imagenUrl

                if (libro?.uidUsuario == userUid) {
                    isBookOwner = true
                }

            }


        }

    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            HeaderBookintok()


            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(72.dp))

                AsyncImage(
                    model = libro?.imagenUrl,
                    contentDescription = "Portada del libro: ${libro?.titulo}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 2f),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.book_placeholder),
                    placeholder = painterResource(id = R.drawable.book_placeholder)
                )

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = titulo,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = estado,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Autor:", modifier = Modifier.width(100.dp))
                        Text(
                            text = autor,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Géneros:", modifier = Modifier.width(100.dp))
                        Text(
                            text = if (generoSecundario.isNotEmpty()) "$generoPrincipal, $generoSecundario" else generoPrincipal,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Idioma:", modifier = Modifier.width(100.dp))
                        Text(
                            text = idioma,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cubierta:", modifier = Modifier.width(100.dp))
                        Text(
                            text = cubierta,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (descripcion == "") "Sin descripción" else descripcion,
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isBookOwner) {
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = usuario?.imagenUrl,
                                contentDescription = "Foto de perfil del usuario",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(50))
                                    .border(
                                        BorderStroke(1.dp, Color.Black.copy(.4f)),
                                        RoundedCornerShape(50)
                                    ),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.user_placeholder),
                                placeholder = painterResource(id = R.drawable.user_placeholder)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(onClick = {
                                        navController.navigate(
                                            Screen.ProfilePage.createRoute(
                                                usuario?.uid.toString()
                                            )
                                        )
                                    }),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = usuario?.nombre ?: "Usuario",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RatingBar(
                                        rating = mediaValoraciones,
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "($cantidadValoraciones)")
                                }
                            }

                            Button(
                                onClick = {
                                    //TODO: Implementar la navegación a la pantalla de chat
                                },
                                border = BorderStroke(1.dp, Color.Black.copy(.4f)),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .size(50.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.chats),
                                    contentDescription = "Chat Icon",
                                    tint = Color.Black.copy(.4f)
                                )
                            }
                        }

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

                    if (isBookOwner) {
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
                                    navController.navigate(Screen.EditBook.createRoute(bookId))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Icon",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    MenuInferior(
                        navController = navController,
                        3,
                        userUid
                    )
                }

            }

        }

    }
}