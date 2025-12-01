package com.example.proyectotienda.cart

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.proyectotienda.product.Producto

data class CartItem(
    val product: Producto,
    var cantidad: Int = 1
)

class CartViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items = _items.asStateFlow()

    // Total del carrito
    private val _total = MutableStateFlow(0.0)
    val total = _total.asStateFlow()

    // Conteo de ítems para el contador (Badge)
    private val _itemCount = MutableStateFlow(0)
    val itemCount = _itemCount.asStateFlow()


    // AGREGAR PRODUCTO
    fun addToCart(product: Producto) {
        _items.update { current ->
            val mutable = current.toMutableList()
            val existing = mutable.find { it.product.id == product.id }

            if (existing != null) {
                existing.cantidad++
            } else {
                mutable.add(CartItem(product))
            }
            recalculateTotal(mutable)
            updateItemCount(mutable)
            mutable
        }
    }

    fun addOne(product: Producto) = addToCart(product)


    // QUITAR UNA UNIDAD
    fun removeOne(product: Producto) {
        _items.update { current ->
            val mutable = current.toMutableList()
            val existing = mutable.find { it.product.id == product.id }

            if (existing != null) {
                existing.cantidad--
                if (existing.cantidad <= 0) mutable.remove(existing)
            }
            recalculateTotal(mutable)
            updateItemCount(mutable)
            mutable
        }
    }


    // RE-CALCULAR TOTAL
    private fun recalculateTotal(list: List<CartItem>) {
        _total.value = list.sumOf { it.product.precio * it.cantidad }
    }


    // ACTUALIZAR CONTEO DE ÍTEMS
    private fun updateItemCount(list: List<CartItem>) {
        _itemCount.value = list.sumOf { it.cantidad }
    }

    // LIMPIAR CARRITO
    fun clearCart() {
        _items.value = emptyList()
        _total.value = 0.0
        _itemCount.value = 0 // Reiniciar el contador
    }
}