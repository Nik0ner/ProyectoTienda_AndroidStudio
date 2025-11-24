package com.example.proyectotienda.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.home.viewmodel.HomeViewModel
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.product.Producto
import com.example.proyectotienda.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    // 1. INYECTAMOS EL VIEWMODEL
    viewModel: HomeViewModel = viewModel()
) {
    // ... (El Scaffold y TopBar se mantienen igual) ...

    val appBarcolor = MaterialTheme.colorScheme.primary
    val appBarContent = MaterialTheme.colorScheme.onPrimary

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.trafalgar),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(70.dp)
                            .fillMaxWidth(0.5f)
                    )
                },

                // ⬅️ Botón FAB (Add) en la barra superior (navigationIcon)
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(route = Screens.ProductCreation.route)}) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Crear Producto")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screens.Login.route)}) {
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
        HomeBodyContent(
            Modifier.padding(paddingValues),
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun HomeBodyContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 64.dp)
                )
            }
            state.errorMessage != null -> {
                Text("Error al cargar productos: ${state.errorMessage}", color = Color.Red)
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.productos) { producto ->
                        ProductCard(
                            producto = producto,
                            navController = navController,
                            onComprarClick = { viewModel.onComprarClick(producto.id) }
                            // No necesitamos un onEditClick aquí, la navegación es directa.
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    producto: Producto,
    navController: NavController,
    onComprarClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // ... (El resto del contenido de la columna, Imagen, Texto, etc., se mantiene igual) ...

                // Imagen del Producto
                Image(
                    painter = painterResource(id = com.example.proyectotienda.R.drawable.retro),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )

                // Nombre y Descripción
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = producto.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = producto.descripcion,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$${"%.2f".format(producto.precio)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Botón
                Button(
                    onClick = { onComprarClick(producto.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Comprar", fontWeight = FontWeight.SemiBold)
                }
            }

            // ⬅️ ÍCONO DE EDICIÓN DENTRO DE UN CÍRCULO CON FONDO
            Surface(
                onClick = {
                    // NAVEGAR a ProductUpdateScreen con el ID del producto
                    navController.navigate(Screens.ProductUpdate.withId(producto.id))
                },
                modifier = Modifier
                    .align(Alignment.TopEnd) // Posicionado en la esquina superior izquierda
                    .padding(8.dp) // Pequeño margen desde el borde de la tarjeta
                    .size(32.dp), // Tamaño del círculo
                shape = CircleShape, // ⬅️ Forma circular
                color = Color.White.copy(alpha = 0.8f), // Fondo blanco semitransparente
                shadowElevation = 4.dp // Pequeña sombra
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar Producto",
                        tint = MaterialTheme.colorScheme.primary, // Color primario para el lápiz
                        modifier = Modifier.size(18.dp) // Tamaño del icono dentro del círculo
                    )
                }
            }
        }
    }
}