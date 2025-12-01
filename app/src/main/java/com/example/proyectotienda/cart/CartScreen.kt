package com.example.proyectotienda.cart.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.proyectotienda.cart.CartViewModel
import com.example.proyectotienda.navigation.Screens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    navController: NavController
) {
    // Observa el estado reactivamente
    val items by cartViewModel.items.collectAsState()
    val total by cartViewModel.total.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            // USAMOS LazyColumn para un renderizado eficiente de la lista
            if (items.isEmpty()) {
                Text(
                    text = "Tu carrito está vacío.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f), // Ocupa el espacio restante
                    contentPadding = PaddingValues(top = 16.dp)
                ) {
                    items(items) { item -> // Itera sobre la lista del ViewModel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.product.nombre} (x${item.cantidad})",
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$${"%.2f".format(item.product.precio * item.cantidad)}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Divider()
                    }
                }
            }


            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TOTAL:",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "$${"%.2f".format(total)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }


            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    navController.navigate(Screens.Development.route)
                    cartViewModel.clearCart()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar compra")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}