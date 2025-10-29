package com.example.proyectotienda.form

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectotienda.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navController: NavController) {

    val darkPurple = MaterialTheme.colorScheme.secondary
    val onDarkPurple = MaterialTheme.colorScheme.onSecondary

    // Estados de los campos
    var usuario by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    // Estados de error
    var usuarioError by remember { mutableStateOf(false) }
    var passError by remember { mutableStateOf(false) }
    var correoError by remember { mutableStateOf(false) }
    var passErrorMsg by remember { mutableStateOf("") }
    var correoErrorMsg by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("LF SNKRS") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screens.Login.route) }) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "Perfil")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = darkPurple,
                    titleContentColor = onDarkPurple,
                    actionIconContentColor = onDarkPurple,
                    navigationIconContentColor = onDarkPurple
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Usuario
            OutlinedTextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    usuarioError = false
                },
                label = { Text("Usuario") },
                isError = usuarioError,
                modifier = Modifier.fillMaxWidth()
            )
            if (usuarioError) {
                Text(
                    text = "El usuario es obligatorio",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Contraseña
            OutlinedTextField(
                value = pass,
                onValueChange = {
                    pass = it
                    passError = false
                    passErrorMsg = ""
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = passError,
                modifier = Modifier.fillMaxWidth()
            )
            if (passError) {
                Text(
                    text = passErrorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Correo
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    correoError = false
                    correoErrorMsg = ""
                },
                label = { Text("Correo") },
                isError = correoError,
                modifier = Modifier.fillMaxWidth()
            )
            if (correoError) {
                Text(
                    text = correoErrorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botón de registro
            Button(
                onClick = {
                    // Validación simple y básica
                    usuarioError = usuario.isBlank()
                    passError = pass.isBlank() || !pass.any { it.isUpperCase() }
                    correoError = correo.isBlank() || !correo.contains("@") || !correo.contains(".")

                    passErrorMsg = when {
                        pass.isBlank() -> "La contraseña es obligatoria"
                        !pass.any { it.isUpperCase() } -> "Debe contener al menos una mayúscula"
                        else -> ""
                    }

                    correoErrorMsg = when {
                        correo.isBlank() -> "El correo es obligatorio"
                        !correo.contains("@") || !correo.contains(".") -> "Correo inválido"
                        else -> ""
                    }

                    if (!usuarioError && !passError && !correoError) {
                        navController.navigate(Screens.HomeScreen.route)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}
