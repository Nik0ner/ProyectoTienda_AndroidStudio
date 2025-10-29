package com.example.proyectotienda.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.material3.ButtonDefaults
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.product.Producto

import com.example.proyectotienda.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val darkPurple = MaterialTheme.colorScheme.secondary
    val onDarkPurple = MaterialTheme.colorScheme.onSecondary

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Reemplazamos el texto por un logo
                    Image(
                        painter = painterResource(id = R.drawable.trafalgar), // tu drawable
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(70.dp)
                            .fillMaxWidth(0.5f) // Ajusta tamaño del logo
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screens.Login.route)}) {
                        Icon(imageVector = androidx.compose.material.icons.Icons.Filled.Person, contentDescription = "Perfil")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = darkPurple,
                    titleContentColor = onDarkPurple,
                    actionIconContentColor = onDarkPurple,
                    navigationIconContentColor = onDarkPurple
                )
            )
        }
    ) { paddingValues ->
        HomeBodyContent(
            Modifier.padding(paddingValues),
            navController = navController
        )
    }
}

@Composable
fun HomeBodyContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val productosDeEjemplo = listOf(
        Producto("1", "Jordan 1 Retro Dior", "Edición limitada", 7000.00, "url_imagen_1"),
        Producto("2", "Jordan 1 Retro Dior", "Edición limitada", 7000.00, "url_imagen_2"),
        Producto("3", "Jordan 1 Retro Dior", "Edición limitada", 7000.00, "url_imagen_3"),
        Producto("4", "Jordan 1 Retro Dior", "Edición limitada", 7000.00, "url_imagen_4"),
        Producto("5", "Jordan 1 Retro Dior", "Edición limitada", 7000.00, "url_imagen_5"),
        Producto("6", "Jordan 1 Retro Dior", "Edición limitada", 7000.00, "url_imagen_6"),
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(productosDeEjemplo) { producto -> // ⬅️ Usamos la lista de ejemplo
                ProductCard(producto = producto, navController = navController)
            }
        }
    }
}

@Composable
fun ProductCard(
    producto: Producto,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.width(180.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {

            //  Imagen del Producto
            Image(
                painter = painterResource(id = com.example.proyectotienda.R.drawable.retro),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            //  Nombre y Descripción
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

            //  Precio
            Text(
                text = "$${"%.2f".format(producto.precio)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            //  Botón "Comprar"
            Button(
                onClick = { /* Lógica de añadir al carrito */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Comprar", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
