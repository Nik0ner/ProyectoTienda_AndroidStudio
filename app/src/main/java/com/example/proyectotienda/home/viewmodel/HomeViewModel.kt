package com.example.proyectotienda.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotienda.data.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {

    // ----------------------------------------------------
    // 1. Capa de Datos (Data Layer)
    // ----------------------------------------------------
    // Instanciamos el Repositorio. En apps grandes, esto se inyectaría
    // usando Hilt/Koin (Inyección de Dependencias) para facilitar el testing.
    private val repository = ProductoRepository

    // ----------------------------------------------------
    // 2. Estado (State Layer)
    // ----------------------------------------------------
    // El estado observado por la vista (UI). Contiene la lista de productos.
    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        // En lugar de cargar datos fijos, comenzamos a escuchar el Repositorio.
        observeProductos()
    }

    // ----------------------------------------------------
    // 3. Lógica (Business Logic - READ)
    // ----------------------------------------------------

    /**
     * Función que escucha los cambios en el Repositorio (la base de datos simulada).
     * Se ejecuta automáticamente cada vez que se añade o elimina un producto.
     */
    private fun observeProductos() {
        // Usamos viewModelScope para ejecutar la observación en el ciclo de vida del ViewModel.
        viewModelScope.launch {
            // Recolectamos el Flow que emite el Repositorio.
            repository.getProductos().collect { productos ->
                // Cuando el Flow emite una nueva lista, actualizamos el estado.
                _state.update { currentState ->
                    currentState.copy(
                        productos = productos,
                        isLoading = false // Desactivamos el indicador de carga una vez que tenemos datos
                    )
                }
            }
        }
    }

    // ----------------------------------------------------
    // 4. Manejo de Eventos (Event Handling)
    // ----------------------------------------------------

    /**
     * Evento al presionar el botón "Comprar".
     */
    fun onComprarClick(productoId: String) {
        // FUTURO:
        // 1. Llamar a repository.addProductoToCart(productoId)
        // 2. Navegar a Screens.Detail(productoId)
        println("FUTURO: Producto ${productoId} añadido al carrito.")
    }

    /**
     * Evento para borrar un producto (simulando una acción de administrador).
     */
    fun onDeleteClick(productoId: String) {
        // DELETE: Delegamos la acción de borrado al Repositorio.
        viewModelScope.launch {
            repository.deleteProducto(productoId)
            // FUTURO: Añadir manejo de errores aquí (try/catch)
        }
    }
}