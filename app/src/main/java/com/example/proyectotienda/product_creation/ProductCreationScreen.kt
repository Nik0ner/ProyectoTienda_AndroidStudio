package com.example.proyectotienda.product_creation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.product_creation.viewmodel.ProductCreationViewModel
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreationScreen(
    navController: NavController,
    viewModel: ProductCreationViewModel = viewModel()
) {
    val estado by viewModel.state.collectAsState()

    // Configuramos colores de la barra superior
    val appBarcolor = MaterialTheme.colorScheme.primary
    val appBarContent = MaterialTheme.colorScheme.onPrimary

    LaunchedEffect(estado.creacionExitosa) {
        if (estado.creacionExitosa) {
            navController.navigate(Screens.HomeScreen.route) { // Usamos Screens.Home si ese es el objeto correcto
                popUpTo(Screens.Login.route) { inclusive = true }
            }
            viewModel.resetCreacionExitosa()
        }
    }

    Scaffold(
        topBar = {
            // ⬅️ Usamos CenterAlignedTopAppBar
            CenterAlignedTopAppBar(
                title = {
                    Text("Crear Nuevo Producto") // Título centrado, puedes usar Image si lo prefieres
                },
                // ⬅️ Botón de Navegación: Flecha para volver
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                // ⬅️ Acciones: Puedes usar el Logo de Trafalgar si quieres
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.trafalgar),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(70.dp)
                            .fillMaxWidth(0.2f), // Más pequeño para que quepa en actions
                        contentScale = ContentScale.Fit
                    )
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
        ProductCreationContent(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            estado = estado,
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
fun ProductCreationContent(
    modifier: Modifier = Modifier,
    estado: com.example.proyectotienda.product_creation.viewmodel.ProductCreationUiState,
    viewModel: ProductCreationViewModel,
    navController: NavController
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Campo Nombre
        OutlinedTextField(
            value = estado.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = { Text("Nombre del Producto") },
            isError = estado.errorNombre,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (estado.errorNombre) {
            Text("El nombre no puede estar vacío.", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Campo Descripción
        OutlinedTextField(
            value = estado.descripcion,
            onValueChange = { viewModel.onDescripcionChange(it) },
            label = { Text("Descripción") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Campo Precio
        OutlinedTextField(
            value = estado.precio,
            onValueChange = {
                // 🛑 CORRECCIÓN CRÍTICA: Debes usar el valor 'it' que proporciona el callback
                viewModel.onPrecioChange(it)
            },
            label = { Text("Precio ($)") },
            isError = estado.errorPrecio,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (estado.errorPrecio) {
            Text("Ingrese un precio válido (número mayor a 0).", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Botón de Guardar (CREATE)
        Button(
            onClick = { viewModel.onGuardarProductoClick() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("GUARDAR PRODUCTO")
        }
    }
}