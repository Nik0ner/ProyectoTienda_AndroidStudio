package com.example.proyectotienda.data

import com.example.proyectotienda.product.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductoRepository {
    private val productosDB = MutableStateFlow(
        mutableListOf(
            Producto("1", "Jordan 1 Retro Dior", "Edición limitada", 7000.00),
            Producto("2", "Air Force 1 Snake", "Púrpura y Escamas", 1200.00),
            Producto("3", "Nike Dunk Panda", "Clásico B/N", 950.00),
        )
    )

    private fun <T> MutableStateFlow<T>.updateValue(newValue: T) {
        this.value = newValue
    }

    // READ (Leer): El ViewModel se suscribe a este Flow.
    fun getProductos() = productosDB.asStateFlow()

    // CREATE (Crear): Añade un producto y notifica a los observadores.
    fun addProducto(producto: Producto) {
        // Aseguramos un ID único simple para la simulación
        val newId = (productosDB.value.size + 1).toString()
        val newProducto = producto.copy(id = newId)

        productosDB.value.add(newProducto)
        productosDB.updateValue(productosDB.value)
    }

    // DELETE (Borrar): (Lo usaremos más adelante en la Home Screen)
    fun deleteProducto(productoId: String) {
        val originalSize = productosDB.value.size
        productosDB.value.removeIf { it.id == productoId }
        if (productosDB.value.size < originalSize) {
            productosDB.updateValue(productosDB.value)
        }
    }
}