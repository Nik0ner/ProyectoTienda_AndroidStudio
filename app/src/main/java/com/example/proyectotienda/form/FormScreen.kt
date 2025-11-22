package com.example.proyectotienda.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// ⬇️ IMPORTS CLAVE PARA MVVM
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.form.viewmodel.FormViewModel
import com.example.proyectotienda.R
import com.example.proyectotienda.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    viewModel: FormViewModel = viewModel() // 1. Inyectamos el ViewModel aquí
) {
    // 2. Observamos el Estado (La fuente de verdad única)
    val state by viewModel.state.collectAsState()

    val appBarcolor = MaterialTheme.colorScheme.primary
    val appBarContent = MaterialTheme.colorScheme.onPrimary

    // 3. Efecto de Navegación: Si el ViewModel dice "Éxito", navegamos.
    LaunchedEffect(state.isRegistroExitoso) {
        if (state.isRegistroExitoso) {
            navController.navigate(Screens.HomeScreen.route) {
                // Opcional: Evita volver al registro con el botón atrás
                popUpTo(Screens.Login.route) { inclusive = false }
            }
            viewModel.resetRegistroExitoso()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.trafalgar),
                        contentDescription = "Logo",
                        modifier = Modifier.height(70.dp).fillMaxWidth(0.5f)
                    )
                },
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
                    containerColor = appBarcolor,
                    actionIconContentColor = appBarContent,
                    navigationIconContentColor = appBarContent,
                    titleContentColor = appBarContent
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
            // ---------------- USUARIO ----------------
            OutlinedTextField(
                value = state.usuario, // LEEMOS del estado
                onValueChange = { viewModel.onUsuarioChange(it) }, // AVISAMOS al ViewModel
                label = { Text("Usuario") },
                isError = state.usuarioError, // LEEMOS el error
                modifier = Modifier.fillMaxWidth()
            )
            if (state.usuarioError) {
                Text(
                    text = "El usuario es obligatorio",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- CONTRASEÑA ----------------
            OutlinedTextField(
                value = state.pass,
                onValueChange = { viewModel.onPassChange(it) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = state.passError,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.passError) {
                Text(
                    text = state.passErrorMsg, // Mensaje dinámico desde el ViewModel
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- CORREO ----------------
            OutlinedTextField(
                value = state.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo") },
                isError = state.correoError,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.correoError) {
                Text(
                    text = state.correoErrorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // ---------------- BOTÓN ----------------
            Button(
                // Toda la lógica compleja se fue al ViewModel. ¡Qué limpieza!
                onClick = { viewModel.onRegistrarClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}