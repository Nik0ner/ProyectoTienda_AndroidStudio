package com.example.proyectotienda.product_update

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.product_update.viewmodel.ProductUpdateViewModel
import com.example.proyectotienda.product_update.viewmodel.ProductUpdateViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductUpdateScreen(
    navController: NavController,
    // ⬅️ Recibimos el ID desde la navegación
    productId: String
) {
    // 1. INYECTAR VIEWMODEL CON FACTORY: Usamos el Factory para pasar el ID
    val viewModel: ProductUpdateViewModel = viewModel(
        factory = ProductUpdateViewModelFactory(productId)
    )
    val estado by viewModel.state.collectAsState()

    // 2. EFECTO DE NAVEGACIÓN: Volver a Home cuando la actualización sea exitosa
    LaunchedEffect(estado.actualizacionExitosa) {
        if (estado.actualizacionExitosa) {
            navController.popBackStack()
            viewModel.resetActualizacionExitosa()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modificar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        ProductUpdateContent(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            estado = estado,
            viewModel = viewModel
        )
    }
}

@Composable
fun ProductUpdateContent(
    modifier: Modifier = Modifier,
    estado: com.example.proyectotienda.product_update.viewmodel.ProductUpdateUiState,
    viewModel: ProductUpdateViewModel
) {
    // Muestra la carga mientras el ViewModel busca los datos del producto
    if (estado.cargando) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Nombre
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

        // Campo Descripción
        OutlinedTextField(
            value = estado.descripcion,
            onValueChange = { viewModel.onDescripcionChange(it) },
            label = { Text("Descripción") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Precio
        OutlinedTextField(
            value = estado.precio,
            onValueChange = { viewModel.onPrecioChange(it) },
            label = { Text("Precio (€ o $)") },
            isError = estado.errorPrecio,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (estado.errorPrecio) {
            Text("Ingrese un precio válido.", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Guardar Cambios (UPDATE)
        Button(
            onClick = { viewModel.onGuardarCambiosClick() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("GUARDAR CAMBIOS")
        }
    }
}