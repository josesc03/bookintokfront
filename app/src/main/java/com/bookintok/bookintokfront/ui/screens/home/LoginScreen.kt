package com.bookintok.bookintokfront.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bookintok.bookintokfront.ui.navigation.Screen

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
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
            onClick = { /* Condicionar
            si el usuario ya tiene unas coordenadas envia a MainScreen
             si no tiene unas coordenadas envia a LocationScreen*/
                navController.navigate(Screen.Location.route)},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB3D0BE),
            ),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("INICIA SESIÓN", color = Color.Black.copy(alpha = 0.6f))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* Handle Google login */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB3D0BE)
            ),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("INICIA SESIÓN CON GOOGLE", color = Color.Black.copy(alpha = 0.6f))
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
    }
}