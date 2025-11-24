package com.example.proyectotienda.product_update.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotienda.data.ProductoRepository
import com.example.proyectotienda.product.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductUpdateViewModel(
    // ⬅️ 1. Recibe el ID del producto a modificar desde la navegación
    private val productId: String
) : ViewModel() {

    private val repository = ProductoRepository // Usamos el Singleton
    private val _state = MutableStateFlow(ProductUpdateUiState())
    val state = _state.asStateFlow()

    init {
        // LÓGICA CLAVE: CARGAR EL PRODUCTO EXISTENTE
        viewModelScope.launch {
            _state.update { it.copy(cargando = true) }

            // Busca el producto en el Repositorio
            val producto = repository.getProductoById(productId)

            producto?.let { p ->
                // ⬅️ Rellenar el estado con los datos actuales del producto
                _state.update {
                    it.copy(
                        nombre = p.nombre,
                        descripcion = p.descripcion,
                        precio = p.precio.toString(), // Convertir Double a String para el TextField
                        cargando = false
                    )
                }
            } ?: _state.update {
                it.copy(cargando = false, errorMessage = "Producto no encontrado")
            }
        }
    }

    // ------------------------------------
    // 2. MANEJADORES DE INPUT (Actualizan el estado con cada tecla)
    // ------------------------------------

    fun onNombreChange(newNombre: String) {
        _state.update { it.copy(nombre = newNombre, errorNombre = false) }
    }

    fun onDescripcionChange(newDescripcion: String) {
        _state.update { it.copy(descripcion = newDescripcion) }
    }

    fun onPrecioChange(newPrecio: String) {
        // Opcional: solo permitir entrada de números y puntos
        if (newPrecio.isEmpty() || newPrecio.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _state.update { it.copy(precio = newPrecio, errorPrecio = false) }
        }
    }

    // ------------------------------------
    // 3. LÓGICA PRINCIPAL: GUARDAR CAMBIOS (UPDATE)
    // ------------------------------------

    fun onGuardarCambiosClick() {
        val s = _state.value

        // Validación
        val precioParseado = s.precio.toDoubleOrNull()
        val nombreValido = s.nombre.isNotBlank()
        val precioValido = precioParseado != null && precioParseado > 0

        if (!nombreValido || !precioValido) {
            _state.update {
                it.copy(
                    errorNombre = !nombreValido,
                    errorPrecio = !precioValido
                )
            }
            return
        }

        // Construir el objeto Producto Actualizado
        val productoActualizado = Producto(
            id = productId, // ⬅️ ¡CRÍTICO! Mantenemos el ID original
            nombre = s.nombre,
            descripcion = s.descripcion,
            precio = precioParseado!!
        )

        // Llamar a la función de Actualización del Repositorio
        viewModelScope.launch {
            repository.updateProducto(productoActualizado)

            // Notificar a la Vista para volver al Home
            _state.update { it.copy(actualizacionExitosa = true) }
        }
    }

    /**
     * Llamado por la Vista DESPUÉS de navegar, para resetear la bandera.
     */
    fun resetActualizacionExitosa() {
        _state.update { it.copy(actualizacionExitosa = false) }
    }
}
