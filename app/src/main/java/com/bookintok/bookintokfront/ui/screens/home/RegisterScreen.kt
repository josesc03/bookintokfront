package com.bookintok.bookintokfront.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bookintok.bookintokfront.ui.navigation.Screen

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
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
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* Enviar al LoginScreen si el registro es exitoso */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB3D0BE)
            ),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("REGISTRATE", color = Color.Black.copy(alpha = 0.6f))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* Enviar al LoginScreen si el registro es exitoso */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB3D0BE)
            ),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("REGISTRATE CON GOOGLE", color = Color.Black.copy(alpha = 0.6f))
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
    }
}