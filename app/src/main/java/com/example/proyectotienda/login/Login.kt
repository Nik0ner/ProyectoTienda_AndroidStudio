package com.example.proyectotienda.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.R
import com.example.proyectotienda.login.viewmodel.LoginViewModel
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.ui.theme.AmarilloClaro
import com.example.proyectotienda.ui.theme.YellowNeonBright
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Lógica de Navegación tras inicio de sesión exitoso
    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            navController.navigate(Screens.HomeScreen.route) {
                popUpTo(Screens.Login.route) { inclusive = true }
            }
            viewModel.resetLoginSuccessful()
        }
    }

    Scaffold { paddingValues ->
        // Contenido Principal de la pantalla (Cuerpo)
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

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // CARD PRINCIPAL con diseño de degradado
        Surface(
            color = Color.Transparent,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AmarilloClaro, YellowNeonBright)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo de la aplicación
                Image(
                    painter = painterResource(id = R.drawable.trafalgar),
                    contentDescription = "Logo",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // CAMPO DE CORREO
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo electrónico", color = MaterialTheme.colorScheme.surface) },
                    isError = state.showEmailError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        errorIndicatorColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                if (state.showEmailError) {
                    Text("Ingrese un correo válido.", color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO DE CONTRASEÑA con visibilidad
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    isError = state.showPasswordError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),

                    // Control de visibilidad de contraseña
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Mostrar/Ocultar contraseña",
                                tint = Color.Black
                            )
                        }
                    },

                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        errorIndicatorColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error
                    ),

                    shape = RoundedCornerShape(12.dp)
                )

                if (state.showPasswordError) {
                    Text("La contraseña es obligatoria.", color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mensaje de error general de Firebase/Conexión
                state.generalErrorMessage?.let { msg ->
                    Text(msg, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTÓN DE LOGIN
                Button(
                    onClick = { viewModel.onLoginClick(state) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = YellowNeonBright
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("INICIAR SESIÓN")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Enlace a Registro
                TextButton(
                    onClick = { navController.navigate(Screens.Form.route) }
                ) {
                    Text(
                        "¿No tienes cuenta? Regístrate",
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Enlace a Recuperar Contraseña
                TextButton(onClick = { navController.navigate(Screens.RecoverPassword.route)}) {
                    Text(
                        "¿Olvidaste tu contraseña?",
                        color = Color.Black
                    )
                }
            }
        }
    }
}