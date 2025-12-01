package com.example.proyectotienda.cart.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectotienda.cart.CartViewModel


@Composable
fun CartModal(
    cartViewModel: CartViewModel,
    onClose: () -> Unit,
    onCheckout: () -> Unit
) {
    val items by cartViewModel.items.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // ---------------- HEADER ----------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Carrito de Compras", style = MaterialTheme.typography.titleLarge)

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ---------------- LISTA DE ITEMS ----------------
                if (items.isEmpty()) {
                    Text("El carrito está vacío 🛒", color = Color.Gray)
                } else {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.product.nombre} (x${item.cantidad})")
                            Text("$${item.product.precio * item.cantidad}")
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ---------------- TOTAL ----------------
                val total = items.sumOf { it.product.precio * it.cantidad }

                Text(
                    "TOTAL: $${"%.2f".format(total)}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(20.dp))

                // ---------------- BOTÓN PAGO ----------------
                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = items.isNotEmpty()
                ) {
                    Text("Finalizar Compra")
                }
            }
        }
    }
}
