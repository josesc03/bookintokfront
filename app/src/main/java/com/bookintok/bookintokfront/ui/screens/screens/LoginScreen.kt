package com.bookintok.bookintokfront.ui.screens.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.bookintok.bookintokfront.R
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.bookintok.bookintokfront.ui.responses.UsuarioResponse
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
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

        Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "INICIA SESIÓN",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF006025),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
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
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
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
            ),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton (onClick = { passwordVisible = !passwordVisible }) {
                    Image(
                        painter = if (passwordVisible)
                            painterResource(id = R.drawable.visible)
                        else
                            painterResource(id = R.drawable.visible_off),
                        contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                        colorFilter = ColorFilter.tint(Color(0xFF7AA289).copy(alpha = .6f))
                    )
                }
            }
        )

        Text(
            text = "¿Has olvidado tu contraseña?",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFD54941),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { loginUser(email, password, navController) { error ->
                errorMessage = error
            } },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB3D0BE),
            ),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("INICIA SESIÓN", color = Color.Black.copy(alpha = 0.6f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("¿Todavía no tienes una cuenta?", color = Color.Black)

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { navController.navigate(Screen.Register.route) },
            border = BorderStroke(1.dp, Color(0xFF006025)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color(0xFF006025)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("REGISTRATE")
        }

            errorMessage?.let { Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp)) }

        }
}

fun loginUser(
    email: String,
    password: String,
    navController: NavController,
    onError: (String) -> Unit = {}
) {
    if (email.isBlank() || password.isBlank()) {
        onError("Por favor completa todos los campos")
        return
    }

    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnSuccessListener { authResult ->
            authResult.user?.getIdToken(true)?.addOnSuccessListener { result ->
                val idToken = result.token
                if (idToken != null) {
                    checkLocationFromUID(navController, onError)
                } else {
                    onError("Error al obtener el token de autenticación")
                }
            }
        }
        .addOnFailureListener { e ->
            onError("Error al iniciar sesión: ${e.localizedMessage}")
        }
}

fun checkLocationFromUID(
    navController: NavController,
    onError: (String) -> Unit = {}
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
                val response: HttpResponse = client.get("http://10.0.2.2:8080/me") {
                    header("Authorization", "Bearer $idToken")
                }

                if (response.status.isSuccess()) {
                    val response = response.body<UsuarioResponse>()
                    val usuario = response.usuario
                    withContext(Dispatchers.Main) {
                        if (usuario.hasCoordinates()){
                            navController.navigate(Screen.Main.route)
                        } else {
                            navController.navigate(Screen.Location.route)
                        }
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