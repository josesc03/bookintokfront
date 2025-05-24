package com.bookintok.bookintokfront.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.bookintok.bookintokfront.R
import com.bookintok.bookintokfront.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
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
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun isPasswordValid(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val isLongEnough = password.length >= 8
        return hasUppercase && hasDigit && isLongEnough
    }

    fun validatePassword(password: String): String? {
        if (password.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (!password.any { it.isUpperCase() }) return "Debe contener al menos una mayúscula"
        if (!password.any { it.isDigit() }) return "Debe contener al menos un número"
        return null
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        if (password != confirmPassword) return "Las contraseñas no coinciden"
        return null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "REGISTRATE",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF006025),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
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
            onValueChange = {
                password = it
                passwordError = validatePassword(it)
                confirmPasswordError = validateConfirmPassword(password, confirmPassword)
            },
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

        if (passwordError != null) {
            Text(
                text = passwordError ?: "",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = validateConfirmPassword(password, it)
            },
            label = { Text("Repetir contraseña") },
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
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton (onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Image(
                        painter = if (confirmPasswordVisible)
                            painterResource(id = R.drawable.visible)
                        else
                            painterResource(id = R.drawable.visible_off),
                        contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password",
                        colorFilter = ColorFilter.tint(Color(0xFF7AA289).copy(alpha = .6f))
                    )
                }
            }
        )

        if (confirmPasswordError != null) {
            Text(
                text = confirmPasswordError ?: "",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                errorMessage = null
                if (isPasswordValid(password).not()) {
                    errorMessage = "Rellena todos los campos"
                    return@Button
                }
                if (password != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden"
                    return@Button
                }

                registerUser(username, email, password, navController) {
                    error -> errorMessage = error
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB3D0BE)
            ),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("REGISTRATE", color = Color.Black.copy(alpha = 0.6f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("¿Ya tienes una cuenta?", color = Color.Black)

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { navController.navigate(Screen.Login.route) },
            border = BorderStroke(1.dp, Color(0xFF006025)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color(0xFF006025)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("INICIA SESIÓN")
        }

        errorMessage?.let { Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp)) }
    }
}

fun registerUser(
    username: String,
    email: String,
    password: String,
    navController: NavController,
    onError: (String) -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                firebaseUser?.getIdToken(true)
                    ?.addOnSuccessListener { result ->
                        val idToken = result.token
                        if (idToken != null) {
                            createUserInBackend(idToken, username, email, onSuccess = {
                                navController.navigate(Screen.Login.route)
                            }, onError = {
                                onError("Error al registrar en el backend: $it")
                            })
                        } else {
                            onError("No se pudo obtener el token de Firebase")
                        }
                    }
                    ?.addOnFailureListener {
                        onError("Error al obtener el token: ${it.message}")
                    }
            } else {
                onError(task.exception?.message ?: "Error desconocido en Firebase")
            }
        }
}

fun createUserInBackend(
    idToken: String,
    username: String,
    email: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: HttpResponse = client.post("http://10.0.2.2:8080/register") {
                //val response: HttpResponse = client.post("http://192.168.1.23:8080/register") {
                    header("Authorization", "Bearer $idToken")
                    contentType(ContentType.Application.Json)
                    setBody(mapOf(
                        "username" to username,
                        "email" to email))
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