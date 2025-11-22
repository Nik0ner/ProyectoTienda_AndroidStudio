package com.example.proyectotienda.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotienda.product.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay // Necesario para la simulación

class HomeViewModel : ViewModel() {

    // 1. Declaración del Estado: Lo que la Vista observará.
    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        // Al crearse el ViewModel, automáticamente inicia la carga de datos.
        cargarProductos()
    }

    // 2. Lógica para obtener los productos (simulación de datos).
    fun cargarProductos() {
        // Usamos viewModelScope para ejecutar una corrutina de forma segura.
        viewModelScope.launch {
            // **SIMULACIÓN:** En una app real, aquí llamarías a un Repositorio (base de datos/API)

            // Simular un retraso de carga (1 segundo) para ver el CircularProgressIndicator
            delay(1000)

            val productosDeEjemplo = listOf(
                Producto("1", "Jordan 1 Retro Dior", "Edición limitada", 7000.00, "url_imagen_1"),
                Producto("2", "Air Force 1 Snake", "Púrpura y Escamas", 1200.00, "url_imagen_2"),
                Producto("3", "Nike Dunk Panda", "Clásico B/N", 950.00, "url_imagen_3"),
                Producto("4", "Yeezy Foam Runner", "Diseño futurista", 5000.00, "url_imagen_4"),
                Producto("5", "Adidas Ultraboost", "Running Performance", 800.00, "url_imagen_5"),
                Producto("6", "New Balance 990v5", "Casual retro", 1300.00, "url_imagen_6"),
            )

            // 3. Actualización del Estado: Notifica a la Vista de los nuevos datos.
            _state.update {
                it.copy(
                    productos = productosDeEjemplo,
                    isLoading = false // Desactivamos el indicador de carga
                )
            }
        }
    }

    // 4. Manejador de Eventos: La Vista lo llamará cuando se presione el botón.
    fun onComprarClick(productoId: String) {
        println("Producto ${productoId} añadido al carrito.")
        // **ACCIÓN FUTURA:** Aquí iría la lógica real para añadir el producto a una lista de carrito
        // o navegar a una pantalla de detalle.
    }
}