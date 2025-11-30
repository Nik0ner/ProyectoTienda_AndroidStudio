package com.example.proyectotienda.recover

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.navigation.Screens

@Composable
fun RecoverPasswordScreen(
    navController: NavController,
    viewModel: RecoverPasswordViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    val message by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Colores del proyecto
    val Yellow = Color(0xFFFFFF33)
    val DarkCard = Color(0xFF1A1A1A)

    // Mostrar snackbar + volver al login automáticamente
    LaunchedEffect(message) {
        message?.let { msg ->

            // Muestra Snackbar
            snackbarHostState.showSnackbar(msg)

            // Navega al login si el correo fue enviado
            if (msg == "Correo de recuperación enviado.") {
                navController.navigate(Screens.Login.route) {
                    popUpTo(Screens.Login.route) { inclusive = true }
                }
            }

            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {

            // ------------------ CARD PRINCIPAL ------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(10.dp)
            ) {

                Column(
                    modifier = Modifier
                        .padding(26.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Recuperar contraseña",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Yellow
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo asociado a la cuenta", color = Yellow) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Yellow,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Yellow,
                            focusedLabelColor = Yellow,
                            unfocusedLabelColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = { viewModel.sendRecoveryEmail(email) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Yellow,
                            contentColor = Color.Black
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Enviar correo de recuperación")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Volver", color = Yellow)
                    }
                }
            }
        }
    }
}
