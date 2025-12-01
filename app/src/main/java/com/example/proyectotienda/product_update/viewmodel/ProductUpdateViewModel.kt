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
    // ID del producto a modificar (inyectado)
    private val productId: String
) : ViewModel() {

    private val repository = ProductoRepository // Repositorio de datos
    private val _state = MutableStateFlow(ProductUpdateUiState())
    val state = _state.asStateFlow() // Estado observable por la UI

    init {
        // Lógica de carga inicial: buscar el producto por ID
        viewModelScope.launch {
            _state.update { it.copy(cargando = true) }

            val producto = repository.getProductoById(productId)

            producto?.let { p ->
                // Rellena el estado con los datos del producto existente
                _state.update {
                    it.copy(
                        nombre = p.nombre,
                        descripcion = p.descripcion,
                        precio = p.precio.toString(),
                        cargando = false
                    )
                }
            } ?: _state.update {
                it.copy(cargando = false, errorMessage = "Producto no encontrado")
            }
        }
    }


    // 2. MANEJADORES DE INPUT
    // Actualiza el nombre en el estado
    fun onNombreChange(newNombre: String) {
        _state.update { it.copy(nombre = newNombre, errorNombre = false) }
    }

    // Actualiza la descripción en el estado
    fun onDescripcionChange(newDescripcion: String) {
        _state.update { it.copy(descripcion = newDescripcion) }
    }

    // Actualiza el precio, permitiendo solo formato numérico
    fun onPrecioChange(newPrecio: String) {
        if (newPrecio.isEmpty() || newPrecio.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _state.update { it.copy(precio = newPrecio, errorPrecio = false) }
        }
    }

    // 3. LÓGICA PRINCIPAL: GUARDAR CAMBIOS (UPDATE)
    fun onGuardarCambiosClick() {
        val s = _state.value

        // Validación de datos
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

        // Construye el objeto Producto con los nuevos datos, manteniendo el ID
        val productoActualizado = Producto(
            id = productId,
            nombre = s.nombre,
            descripcion = s.descripcion,
            precio = precioParseado!!
        )

        // Llama al repositorio para actualizar el producto
        viewModelScope.launch {
            repository.updateProducto(productoActualizado)

            // Notifica la actualización exitosa a la UI
            _state.update { it.copy(actualizacionExitosa = true) }
        }
    }

    fun resetActualizacionExitosa() {
        _state.update { it.copy(actualizacionExitosa = false) }
    }
}