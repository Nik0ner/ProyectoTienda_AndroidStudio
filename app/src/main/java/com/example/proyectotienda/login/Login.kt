package com.example.proyectotienda.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// ⬇️ Imports del ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.R
import com.example.proyectotienda.login.viewmodel.LoginViewModel
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.ui.theme.ProyectoTiendaTheme

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // 3. Efecto de Navegación
    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            navController.navigate(Screens.HomeScreen.route) {
                popUpTo(Screens.Login.route) { inclusive = true }
            }
            viewModel.resetLoginSuccessful()
        }
    }

    Scaffold { paddingValues ->
        BodyContent(
            Modifier.padding(paddingValues),
            navController = navController,
            viewModel = viewModel,
            state = state
        )
    }
}

@Composable
fun BodyContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: LoginViewModel,
    state: com.example.proyectotienda.login.viewmodel.LoginUiState
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.trafalgar),
                    contentDescription = "Logo",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo de correo
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo electrónico") },
                    isError = state.showEmailError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.showEmailError) {
                    Text("Ingrese un correo válido.", color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de contraseña
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    isError = state.showPasswordError,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.showPasswordError) {
                    Text("La contraseña es obligatoria.", color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mensaje de error general (credenciales inválidas)
                state.generalErrorMessage?.let { msg ->
                    Text(msg, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón de login
                Button(
                    onClick = { viewModel.onLoginClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("INICIAR SESIÓN")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón de registro
                TextButton(
                    onClick = { navController.navigate(Screens.Form.route) }
                ) {
                    Text("¿No tienes cuenta? Regístrate")
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Recuperar contraseña
                TextButton(onClick = { /* TODO: Recuperar contraseña */ }) {
                    Text("¿Olvidaste tu contraseña?")
                }
            }
        }
    }
}