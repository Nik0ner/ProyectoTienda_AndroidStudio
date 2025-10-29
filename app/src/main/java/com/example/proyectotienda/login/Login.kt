package com.example.proyectotienda.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectotienda.navigation.AppNavigation
import com.example.proyectotienda.navigation.Screens

@Composable
fun LoginScreen(navController: NavController) {
    Scaffold { paddingValues ->
        BodyContent(
            Modifier.padding(paddingValues),
            navController = navController
        )
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier, navController: NavController) {
    // Estados simples para los campos del login
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "LF SNKRS",
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.primary
        )



        Spacer(modifier = Modifier.height(32.dp))

        // Campo de correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para iniciar sesión
        Button(
            onClick = {
                // 🔹 Aquí se navega al Home
                navController.navigate(route = Screens.HomeScreen.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("INICIAR SESIÓN")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de registro (comentado por ahora)
        TextButton(
            onClick = {
                // TODO: Agregar navegación al registro cuando la pantalla exista
                // navController.navigate(route = Screens.Register.route)
            }
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }

        // Enlace de recuperación (comentado por ahora)
        TextButton(
            onClick = {
                // TODO: Agregar navegación a recuperar contraseña
                // navController.navigate(route = Screens.RecoverPassword.route)
            }
        ) {
            Text("¿Olvidaste tu contraseña?")
        }
    }
}
