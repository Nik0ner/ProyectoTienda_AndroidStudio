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

    // Asumimos que ProductoRepository es un Singleton o un Object
    private val repository = ProductoRepository

    private val _state = MutableStateFlow(ProductCreationUiState())
    val state = _state.asStateFlow()

    // ---------------------------
    // MANEJO INPUTS
    // ---------------------------
    fun onNombreChange(newNombre: String) {
        _state.update { it.copy(nombre = newNombre, errorNombre = false) }
    }

    fun onDescripcionChange(newDescripcion: String) {
        _state.update { it.copy(descripcion = newDescripcion) }
    }

    fun onPrecioChange(newPrecio: String) {
        // Permite solo números (decimales opcionales)
        val filteredPrecio = newPrecio.replace(Regex("[^0-9.]"), "")
        _state.update { it.copy(precio = filteredPrecio, errorPrecio = false) }
    }

    // ---------------------------
    // IMAGEN (opcional)
    // ---------------------------
    fun onImageSelected(uri: Uri?) {
        _state.update {
            it.copy(
                imagenUri = uri,
                showSourceDialog = false
            )
        }
    }

    fun setShowSourceDialog(show: Boolean) {
        _state.update { it.copy(showSourceDialog = show) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun setError(message: String) {
        _state.update { it.copy(errorMessage = message, isLoading = false) }
    }

    // ---------------------------
    // GUARDAR PRODUCTO SIN STORAGE (Lógica Principal)
    // ---------------------------
    fun onGuardarProductoClick() {
        val s = _state.value

        // Intenta parsear el precio, si falla será null
        val precioParseado = s.precio.toDoubleOrNull()

        // Validación
        val nombreValido = s.nombre.isNotBlank()
        val precioValido = precioParseado != null && precioParseado > 0

        if (!nombreValido || !precioValido) {
            _state.update {
                it.copy(
                    errorNombre = !nombreValido,
                    errorPrecio = !precioValido,
                    errorMessage = null // Limpia cualquier error previo si falla la validación local
                )
            }
            // Sale si la validación falla
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // El precio ya se validó como no nulo y > 0
                val precioFinal = precioParseado!!

                // ------------------------
                // CREACIÓN DEL PRODUCTO
                // ------------------------
                val producto = Producto(
                    id = "", // Firebase/Firestore asignará el ID
                    nombre = s.nombre,
                    descripcion = s.descripcion,
                    precio = precioFinal,
                    imagenUrl = null // 🔥 SIN STORAGE → siempre null
                )

                // Llama al repositorio para guardar en Firestore
                repository.addProducto(producto)

                // Éxito: reinicia el estado y marca como exitoso
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
                // Manejo de error de base de datos o cualquier excepción
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