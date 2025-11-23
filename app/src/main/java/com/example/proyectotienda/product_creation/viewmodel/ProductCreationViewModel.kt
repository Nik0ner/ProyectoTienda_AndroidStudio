package com.example.proyectotienda.product_creation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotienda.data.ProductoRepository
import com.example.proyectotienda.product.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductCreationViewModel : ViewModel() {

    // Instancia del Repositorio que queremos actualizar (la DB simulada)
    private val repository = ProductoRepository

    // 1. Estado Observado: La única fuente de verdad para la vista.
    private val _state = MutableStateFlow(ProductCreationUiState())
    val state = _state.asStateFlow()


    // Manejadores de Input


    fun onNombreChange(newNombre: String) {
        // Actualiza el nombre y limpia el error.
        _state.update { it.copy(nombre = newNombre, errorNombre = false) }
    }

    fun onDescripcionChange(newDescripcion: String) {
        // Actualiza la descripción.
        _state.update { it.copy(descripcion = newDescripcion) }
    }

    fun onPrecioChange(newPrecio: String) {
        // Actualiza el precio (como String, ya que es el input de texto).
        _state.update { it.copy(precio = newPrecio, errorPrecio = false) }
    }


    //Lógica principal: CREAR

    fun onGuardarProductoClick() {
        val s = _state.value

        // Validación: Intentar convertir el precio a Double
        val precioParseado = s.precio.toDoubleOrNull()

        val nombreValido = s.nombre.isNotBlank()
        val precioValido = precioParseado != null && precioParseado > 0

        // Si la validación falla, actualiza los estados de error y detiene la función
        if (!nombreValido || !precioValido) {
            _state.update {
                it.copy(
                    errorNombre = !nombreValido,
                    errorPrecio = !precioValido
                )
            }
            return
        }

        // Construir el objeto Producto
        val nuevoProducto = Producto(
            id = "",
            nombre = s.nombre,
            descripcion = s.descripcion,
            precio = precioParseado!!,
            //imagenUrl = "url_pendiente" // Placeholder
        )

        // Delegar la creación al Repositorio
        viewModelScope.launch {
            repository.addProducto(nuevoProducto)

            // Notificar a la Vista que debe navegar
            _state.update {
                it.copy(
                    creacionExitosa = true,
                    // Opcional: limpiar los campos después del registro exitoso
                    nombre = "", descripcion = "", precio = ""
                )
            }
        }
    }
    fun resetCreacionExitosa() {
        _state.update { it.copy(creacionExitosa = false) }
    }
}