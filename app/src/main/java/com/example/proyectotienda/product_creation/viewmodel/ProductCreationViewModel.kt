package com.example.proyectotienda.product_creation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotienda.data.ProductoRepository
import com.example.proyectotienda.product.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.net.Uri
import android.util.Log

class ProductCreationViewModel : ViewModel() {

    private val repository = ProductoRepository // Acceso a la capa de datos

    private val _state = MutableStateFlow(ProductCreationUiState())
    val state = _state.asStateFlow() // Estado observable por la UI

    // ---------------------------
    // MANEJO INPUTS
    // ---------------------------
    // Actualiza el nombre y limpia error
    fun onNombreChange(newNombre: String) {
        _state.update { it.copy(nombre = newNombre, errorNombre = false) }
    }

    // Actualiza la descripción
    fun onDescripcionChange(newDescripcion: String) {
        _state.update { it.copy(descripcion = newDescripcion) }
    }

    // Actualiza el precio, filtrando solo números/decimales
    fun onPrecioChange(newPrecio: String) {
        val filteredPrecio = newPrecio.replace(Regex("[^0-9.]"), "")
        _state.update { it.copy(precio = filteredPrecio, errorPrecio = false) }
    }

    // ---------------------------
    // IMAGEN (opcional)
    // ---------------------------
    // Establece la URI de la imagen seleccionada
    fun onImageSelected(uri: Uri?) {
        _state.update {
            it.copy(
                imagenUri = uri,
                showSourceDialog = false
            )
        }
    }

    // Controla la visibilidad del diálogo de selección de fuente de imagen
    fun setShowSourceDialog(show: Boolean) {
        _state.update { it.copy(showSourceDialog = show) }
    }

    // Limpia el mensaje de error general
    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // Establece un mensaje de error general
    fun setError(message: String) {
        _state.update { it.copy(errorMessage = message, isLoading = false) }
    }

    // ---------------------------
    // GUARDAR PRODUCTO EN FIRESTORE
    // ---------------------------
    fun onGuardarProductoClick() {
        val s = _state.value

        // Intenta convertir el precio a Double para validación
        val precioParseado = s.precio.toDoubleOrNull()

        // Validación local de campos
        val nombreValido = s.nombre.isNotBlank()
        val precioValido = precioParseado != null && precioParseado > 0

        if (!nombreValido || !precioValido) {
            _state.update {
                it.copy(
                    errorNombre = !nombreValido,
                    errorPrecio = !precioValido,
                    errorMessage = null
                )
            }
            return // Detiene la ejecución si falla la validación
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val precioFinal = precioParseado!!

                // Construcción del objeto Producto (sin URL de Storage)
                val producto = Producto(
                    id = "", // Firestore asignará el ID
                    nombre = s.nombre,
                    descripcion = s.descripcion,
                    precio = precioFinal,
                    imagenUrl = null // No se utiliza Storage
                )

                // Llama al repositorio para guardar en Firestore
                repository.addProducto(producto)

                // Éxito: reinicia los campos y marca la creación como exitosa
                _state.update {
                    it.copy(
                        isLoading = false,
                        creacionExitosa = true,
                        nombre = "",
                        descripcion = "",
                        precio = "",
                        imagenUri = null
                    )
                }

            } catch (e: Exception) {
                // Manejo de error de base de datos
                Log.e("PRODUCT_VM", "Error guardando producto: ${e.message}", e)

                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error guardando producto: ${e.message ?: "Desconocido"}"
                    )
                }
            }
        }
    }

    /**
     * Resetea el indicador de creación exitosa para evitar navegación repetida.
     */
    fun resetCreacionExitosa() {
        _state.update { it.copy(creacionExitosa = false) }
    }
}