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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.MaterialTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel() // Inyección del ViewModel
) {
    val appBarcolor = MaterialTheme.colorScheme.primary
    val appBarContent = MaterialTheme.colorScheme.onPrimary

    Scaffold(
        topBar = {
            // BARRA SUPERIOR (TopAppBar)
            CenterAlignedTopAppBar(
                modifier = Modifier.height(110.dp),
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.trafalgar),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(65.dp)
                            .fillMaxWidth(0.5f)
                    )
                },
                navigationIcon = {
                    // Botón para crear nuevo producto
                    IconButton(onClick = { navController.navigate(route = Screens.ProductCreation.route)}, modifier = Modifier.fillMaxHeight()) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Crear Producto", modifier = Modifier.size(32.dp))
                    }
                },
                actions = {
                    // Botón de Perfil/Login
                    IconButton(onClick = { navController.navigate(Screens.Login.route)}, modifier = Modifier.fillMaxHeight()) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "Perfil", modifier = Modifier.size(32.dp))
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
        // Contenido principal de la pantalla
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
    val state by viewModel.state.collectAsState() // Obtiene el estado del ViewModel

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            // Indicador de Carga
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 64.dp)
                )
            }
            // Mensaje de Error
            state.errorMessage != null -> {
                Text("Error al cargar productos: ${state.errorMessage}", color = Color.Red)
            }
            // Grid de Productos
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
                            onComprarClick = { viewModel.onComprarClick(producto.id) },
                            onDeleteClick = {viewModel.onDeleteClick(producto.id) }
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
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Tarjeta individual de cada producto
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
                // Imagen
                Image(
                    painter = painterResource(id = com.example.proyectotienda.R.drawable.retro),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )

                // Nombre, Descripción y Precio
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

                // Fila de acciones (Comprar y Eliminar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Comprar
                    Button(
                        onClick = { onComprarClick(producto.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Comprar", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Ícono de Eliminar
                    Surface(
                        onClick = { onDeleteClick(producto.id) },
                        modifier = Modifier
                            .size(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.error,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Eliminar Producto",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Ícono de Editar (Superpuesto en la esquina superior derecha)
            Surface(
                onClick = {
                    navController.navigate(Screens.ProductUpdate.withId(producto.id))
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.8f),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar Producto",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}