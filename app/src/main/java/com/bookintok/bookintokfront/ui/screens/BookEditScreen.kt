package com.bookintok.bookintokfront.ui.screens

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.model.EstadoLibro
import com.bookintok.bookintokfront.ui.model.Libro
import com.bookintok.bookintokfront.ui.model.TipoCubierta
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.bookintok.bookintokfront.ui.responses.BookRequest
import com.bookintok.bookintokfront.ui.responses.LibroResponse
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resumeWithException
import androidx.compose.ui.text.TextStyle as ComposeTextStyle

@Composable
fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleline: Boolean = true
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleline,
        interactionSource = interactionSource,
        textStyle = ComposeTextStyle(
            fontSize = 14.sp,
            color = Color.Black
        ),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = if (isFocused) Color(0xFF7AA289) else Color(0xFFAEBDB4),
                shape = RoundedCornerShape(6.dp)
            ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 8.dp
                        )
                    )
                }
                Box(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 12.dp
                    )
                ) {
                    innerTextField()
                }

            }
        }
    )
}

@Composable
fun CompactDropdownSelector(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(
                color = if (enabled) Color(0xFFF0F0F0) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                color = if (isFocused) Color(0xFF7AA289) else Color(0xFFAEBDB4),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(enabled = enabled) { expanded = true }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = if (value.isEmpty()) label else value,
            fontSize = 14.sp,
            color = if (value.isEmpty()) Color.Gray else Color.Black
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    enabled = enabled
                )
            }
        }
    }
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    val input = context.contentResolver.openInputStream(uri)
    return BitmapFactory.decodeStream(input!!)
}

@Composable
fun BookEditScreen(navController: NavController, bookId: String? = null) {

    var bookId = bookId?.toInt()
    var newBookId by remember { mutableStateOf<Int?>(null) }

    var userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var generoPrincipal by remember { mutableStateOf("") }
    var generoSecundario by remember { mutableStateOf("") }
    var idioma by remember { mutableStateOf("") }
    var cubierta by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf<String?>(null) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteSuccessDialog by remember { mutableStateOf(false) }

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

    var errorMessage: String = ""
    var libro: Libro? = null

    LaunchedEffect(Unit) {

        if (bookId != null) {
            libro = getLibroFromApi(bookId.toInt())

            titulo = libro?.titulo ?: ""
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
            autor = libro?.autor ?: ""
            generoPrincipal = libro?.categoriaPrincipal ?: ""
            generoSecundario = libro?.categoriaSecundaria ?: ""
            idioma = libro?.idioma ?: ""
            imagenUrl = libro?.imagenUrl

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
                    .fillMaxSize()
                    .padding()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(72.dp))

                Spacer(modifier = Modifier.height(16.dp))

                if (bookId != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                showDeleteConfirmDialog = true
                            },
                            border = BorderStroke(1.dp, Color.Black.copy(.4f)),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .size(40.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar libro",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                if (bitmap.value != null) {
                    Image(
                        bitmap = bitmap.value!!.asImageBitmap(),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .width(300.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = libro?.imagenUrl,
                        contentDescription = "Portada del libro: ${libro?.titulo}",
                        modifier = Modifier
                            .width(300.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.book_placeholder),
                        placeholder = painterResource(id = R.drawable.book_placeholder)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row {
                    Button(
                        onClick = {
                            ImagePicker.with(activity)
                                .crop(3f, 2f)
                                .compress(1024)
                                .maxResultSize(1080, 720)
                                .galleryOnly()
                                .createIntent { intent ->
                                    imagePickerLauncher.launch(intent)
                                }
                        },
                        border = BorderStroke(1.dp, Color.Black.copy(.6f))
                    ) {
                        Text("Seleccionar imagen")
                    }

                    if (bitmap.value != null || libro?.imagenUrl != null) {
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
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Título:", modifier = Modifier.width(100.dp))
                    CompactTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        modifier = Modifier.weight(1f),
                        placeholder = "Título*"
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Autor:", modifier = Modifier.width(100.dp))
                    CompactTextField(
                        value = autor,
                        onValueChange = { autor = it },
                        modifier = Modifier.weight(1f),
                        placeholder = "Autor*"

                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("Descripción:", modifier = Modifier.width(100.dp))
                    CompactTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 100.dp),
                        placeholder = "Descripción",
                        singleline = false
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Estado:", modifier = Modifier.width(100.dp))
                    CompactDropdownSelector(
                        value = estado,
                        onValueChange = {
                            estado = it
                        },
                        options = listOf("Nuevo", "Como nuevo", "Usado", "Antiguo"),
                        label = "Estado*",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Géneros:", modifier = Modifier.width(100.dp))
                    CompactDropdownSelector(
                        label = "Género principal*",
                        options = generos,
                        value = generoPrincipal,
                        onValueChange = { generoPrincipal = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                CompactDropdownSelector(
                    label = "Género secundario",
                    options = generos.filter { it != generoPrincipal },
                    value = generoSecundario,
                    onValueChange = { generoSecundario = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 100.dp),
                    enabled = generoPrincipal.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Idioma:", modifier = Modifier.width(100.dp))
                    CompactDropdownSelector(
                        label = "Idioma*",
                        options = idiomas,
                        value = idioma,
                        onValueChange = { idioma = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cubierta:", modifier = Modifier.width(100.dp))
                    CompactDropdownSelector(
                        label = "Cubierta*",
                        options = listOf("Tapa dura", "Tapa blanda"),
                        value = cubierta,
                        onValueChange = { cubierta = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.navigateUp() },
                        border = BorderStroke(1.dp, Color.Black.copy(.6f))
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                if (bitmap.value != null) {
                                    val url =
                                        subirImagen(
                                            bitmap.value!!,
                                            "a6cf3bbee48752e0d178068ae93dac11"
                                        )
                                    withContext(Dispatchers.Main) {
                                        imagenUrl = url
                                    }
                                }

                                val bookRequest = BookRequest(
                                    titulo = titulo,
                                    autor = autor,
                                    idioma = idioma,
                                    cubierta = if (cubierta == "Tapa dura") TipoCubierta.TAPA_DURA else TipoCubierta.TAPA_BLANDA,
                                    categoriaPrincipal = generoPrincipal,
                                    categoriaSecundaria = generoSecundario,
                                    estado = if (estado == "Nuevo") EstadoLibro.NUEVO else if (estado == "Como nuevo") EstadoLibro.COMO_NUEVO else if (estado == "Usado") EstadoLibro.USADO else EstadoLibro.ANTIGUO,
                                    imagenUrl = imagenUrl,
                                    descripcion = descripcion
                                )
                                updateLibro(
                                    idLibro = bookId?.toInt(),
                                    bookRequest = bookRequest,
                                    onError = {
                                        errorMessage = it
                                        showErrorDialog = true
                                    },
                                    onSuccess = {
                                        newBookId =
                                            it
                                        showSuccessDialog = true
                                    })
                            }

                        },
                        enabled = (titulo.isNotBlank() &&
                                autor.isNotBlank() &&
                                estado.isNotBlank() &&
                                generoPrincipal.isNotBlank() &&
                                idioma.isNotBlank() &&
                                cubierta.isNotBlank()
                                ),
                        border = BorderStroke(1.dp, Color.Black.copy(.6f))
                    ) {
                        Text("Confirmar")
                    }
                }
                Spacer(modifier = Modifier.height(72.dp))
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
                    MenuInferior(
                        navController = navController,
                        3,
                        userUid
                    )
                }

            }

        }

    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { navController.navigate(Screen.EditBook.createRoute(newBookId.toString())) },
            title = { Text(if (bookId != null) "Libro actualizado" else "Libro creado") },
            text = { Text(if (bookId != null) "El libro se ha actualizado correctamente." else "El libro se ha creado correctamente.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate(Screen.DetailBook.createRoute(newBookId.toString()))
                    },
                    border = BorderStroke(1.dp, Color.Black.copy(.6f))
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(if (bookId != null) "Error al actualizar" else "Error al crear") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog = false
                    },
                    border = BorderStroke(1.dp, Color.Black.copy(.6f))
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar este libro?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        if (bookId != null) {
                            eliminarLibro(
                                idLibro = bookId.toInt(),
                                onSuccess = {
                                    showDeleteSuccessDialog = true
                                },
                                onError = {
                                    errorMessage = it
                                    showErrorDialog = true
                                }
                            )
                        }
                    },
                    border = BorderStroke(1.dp, Color.Black.copy(.6f))
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmDialog = false },
                    border = BorderStroke(1.dp, Color.Black.copy(.6f))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDeleteSuccessDialog) {
        AlertDialog(
            onDismissRequest = { navController.navigate(Screen.ProfilePage.createRoute(userUid)) },
            title = { Text("Libro eliminado") },
            text = { Text("El libro se ha eliminado correctamente.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteSuccessDialog = false
                        navController.navigate(Screen.ProfilePage.createRoute(userUid))
                    },
                    border = BorderStroke(1.dp, Color.Black.copy(.6f))
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

}

private fun eliminarLibro(idLibro: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
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
                    val response: HttpResponse

                    response =
                        client.delete("http://10.0.2.2:8080/libro/$idLibro") {
                            header("Authorization", "Bearer $idToken")
                            contentType(ContentType.Application.Json)
                        }

                    if (response.status.isSuccess()) {
                        val responseText = response.bodyAsText()
                        print(responseText)

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

        }
}

suspend fun subirImagen(bitmap: Bitmap, apiKey: String): String? {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

    return try {
        val response: HttpResponse = client.submitForm(
            url = "https://api.imgbb.com/1/upload",
            formParameters = Parameters.build {
                append("key", apiKey)
                append("image", base64Image)
            }
        )

        if (response.status.isSuccess()) {
            val body = response.bodyAsText()
            val json = JSONObject(body)
            val data = json.getJSONObject("data")
            val url = data.optString("url")
            if (url.isNotEmpty()) {
                url
            } else {
                data.optJSONObject("image")?.optString("url")
            }
        } else null
    } catch (e: Exception) {
        null
    }
}


fun updateLibro(
    idLibro: Int?,
    bookRequest: BookRequest,
    onError: (String) -> Unit = {},
    onSuccess: (Int) -> Unit = {}
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
                    val response: HttpResponse

                    if (idLibro != null) {
                        response =
                            client.post("http://10.0.2.2:8080/libro/actualizarLibro/$idLibro") {
                                header("Authorization", "Bearer $idToken")
                                contentType(ContentType.Application.Json)
                                setBody(bookRequest)
                            }

                    } else {
                        response =
                            client.post("http://10.0.2.2:8080/libro/crearLibro") {
                                header("Authorization", "Bearer $idToken")
                                contentType(ContentType.Application.Json)
                                setBody(bookRequest)
                            }

                    }

                    if (response.status.isSuccess()) {
                        val responseText = response.bodyAsText()
                        println(responseText)

                        val response = response.body<LibroResponse>()
                        val libro = response.libro
                        withContext(Dispatchers.Main) {
                            onSuccess(libro.id)
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

        }

}

suspend fun getLibroFromApi(idLibro: Int): Libro? {
    val user = FirebaseAuth.getInstance().currentUser ?: return null
    val idToken = user.getIdTokenSuspend() ?: return null

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    return try {
        val response: HttpResponse = client.get("http://10.0.2.2:8080/libro/$idLibro") {
            header("Authorization", "Bearer $idToken")
        }

        if (response.status.isSuccess()) {
            response.body<LibroResponse>().libro
        } else {
            null
        }
    } catch (e: Exception) {
        null
    } finally {
        client.close()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun FirebaseUser.getIdTokenSuspend(forceRefresh: Boolean = false): String? =
    suspendCancellableCoroutine { cont ->
        getIdToken(forceRefresh)
            .addOnSuccessListener { cont.resume(it.token, null) }
            .addOnFailureListener { cont.resumeWithException(it) }
    }
