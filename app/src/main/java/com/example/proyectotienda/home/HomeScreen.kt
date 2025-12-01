package com.example.proyectotienda.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.proyectotienda.R
import com.example.proyectotienda.cart.CartViewModel
import com.example.proyectotienda.cart.viewmodel.CartModal
import com.example.proyectotienda.home.viewmodel.HomeViewModel
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.product.Producto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(),
    // Instancia compartida
    cartViewModel: CartViewModel
) {
    val appBarcolor = MaterialTheme.colorScheme.primary
    val appBarContent = MaterialTheme.colorScheme.onPrimary
    var showCart by remember { mutableStateOf(false) }

    // 💡 Paso 1: Observar el conteo total de ítems desde el ViewModel
    val cartItemCount by cartViewModel.itemCount.collectAsState()

    Scaffold(
        topBar = {
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
                    IconButton(
                        onClick = { navController.navigate(Screens.ProductCreation.route) }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Crear Producto")
                    }
                },
                actions = {
                    // 💡 Paso 2: Implementar BadgedBox para mostrar el contador
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge {
                                    Text(
                                        text = cartItemCount.toString(),
                                        // Ajuste menor para que se vea bien si el número es grande
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = { navController.navigate(Screens.Cart.route) }) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                    // Fin del BadgedBox

                    IconButton(onClick = { navController.navigate(Screens.Login.route) }) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
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

        if (showCart) {
            CartModal(
                cartViewModel = cartViewModel,
                onClose = { showCart = false },
                onCheckout = { showCart = false }
            )
        }

        HomeBodyContent(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            viewModel = viewModel,
            cartViewModel = cartViewModel
        )
    }
}


// --- HomeBodyContent y ProductCard siguen siendo funcionales ---

@Composable
fun HomeBodyContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel,
    cartViewModel: CartViewModel // Recibe la instancia compartida
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            state.isLoading ->
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 64.dp)
                )

            state.errorMessage != null ->
                Text("Error al cargar productos: ${state.errorMessage}", color = Color.Red)

            else ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.productos) { producto ->
                        ProductCard(
                            onComprarClick = { cartViewModel.addToCart(it) },
                            producto = producto,
                            onDeleteClick = { viewModel.onDeleteClick(producto.id) },
                            onEditClick = {
                                navController.navigate(Screens.ProductUpdate.withId(producto.id))
                            }
                        )
                    }
                }
        }
    }
}


@Composable
fun ProductCard(
    producto: Producto,
    onComprarClick: (Producto) -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.retro),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )

                Column(Modifier.padding(horizontal = 8.dp)) {
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

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "$${"%.2f".format(producto.precio)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = {onComprarClick(producto)},
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Añadir", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.width(8.dp))

                    Surface(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Surface(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.85f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
            }
        }
    }
}