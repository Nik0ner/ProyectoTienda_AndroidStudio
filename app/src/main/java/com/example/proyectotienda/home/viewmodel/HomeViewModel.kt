package com.example.proyectotienda.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotienda.data.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {

    // 1. Inicializa el Repositorio (Capa de Datos)
    private val repository = ProductoRepository

    // 2. Estado observable (UI State)
    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        // Inicia la observación de productos al crear el ViewModel
        observeProductos()
    }

    // Lógica para suscribirse al flujo de datos del Repositorio (READ)
    private fun observeProductos() {
        viewModelScope.launch {
            // Recolecta el Flow que emite los productos en tiempo real
            repository.getProductos().collect { productos ->
                _state.update { currentState ->
                    currentState.copy(
                        productos = productos,
                        isLoading = false // Desactiva la carga al recibir datos
                    )
                }
            }
        }
    }

    // Evento de click para la acción "Comprar"
    fun onComprarClick(productoId: String) {
        // Aquí iría la lógica para agregar al carrito o navegar al detalle
        println("FUTURO: Producto ${productoId} añadido al carrito.")
    }

    // Evento para borrar un producto (DELETE)
    fun onDeleteClick(productoId: String) {
        viewModelScope.launch {
            repository.deleteProducto(productoId) // Llama a la función de borrado del Repositorio
        }
    }
}