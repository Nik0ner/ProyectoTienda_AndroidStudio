package com.example.proyectotienda.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.proyectotienda.R
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.form.viewmodel.FormViewModel
import com.example.proyectotienda.form.viewmodel.PositiveApiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    viewModel: FormViewModel = viewModel()
) {
    val positiveVm: PositiveApiViewModel = viewModel()
    val frasePositiva by positiveVm.phrase.collectAsState()
    val state by viewModel.state.collectAsState()

    val YellowMain = Color(0xFFFFFF33)
    val YellowStrong = Color(0xFFFFFF33)
    val DarkCard = Color(0xFF1A1A1A)

    // Navegación tras registro completo
    LaunchedEffect(state.isRegistroExitoso) {
        if (state.isRegistroExitoso) {
            navController.navigate(Screens.HomeScreen.route) {
                popUpTo(Screens.Login.route) { inclusive = false }
            }
            viewModel.resetRegistroExitoso()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(110.dp),
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.trafalgar),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(240.dp)
                            .fillMaxWidth(0.5f)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screens.Login.route) }) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = YellowMain
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------- FRASE POSITIVA ----------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = frasePositiva,
                    modifier = Modifier.padding(16.dp),
                    color = YellowMain,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // ---------- CARD DEL FORMULARIO ----------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // USUARIO
                    StyledInput(
                        value = state.usuario,
                        onChange = { viewModel.onUsuarioChange(it) },
                        label = "Usuario",
                        isError = state.usuarioError,
                        errorMessage = "El usuario es obligatorio",
                        YellowMain = YellowMain
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // PASSWORD
                    StyledInput(
                        value = state.pass,
                        onChange = { viewModel.onPasswordChange(it) },
                        label = "Contraseña",
                        isError = state.passError,
                        errorMessage = state.passErrorMsg,
                        YellowMain = YellowMain,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // CORREO
                    StyledInput(
                        value = state.correo,
                        onChange = { viewModel.onEmailChange(it) },
                        label = "Correo",
                        isError = state.correoError,
                        errorMessage = state.correoErrorMsg,
                        YellowMain = YellowMain
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // BOTÓN
                    Button(
                        onClick = { viewModel.onRegistrarClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YellowStrong)
                    ) {
                        Text("Registrarse", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun StyledInput(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String,
    YellowMain: Color,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = { Text(label, color = YellowMain) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            visualTransformation =
                if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = YellowMain,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = YellowMain,
                cursorColor = YellowMain,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
