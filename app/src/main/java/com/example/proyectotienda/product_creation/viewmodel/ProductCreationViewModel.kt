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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductCreationViewModel : ViewModel() {

    private val repository = ProductoRepository

    // 💡 INICIALIZACIÓN DE FIREBASE STORAGE
    private val storage = Firebase.storage

    private val _state = MutableStateFlow(ProductCreationUiState())
    val state = _state.asStateFlow()


    // --- MANEJADORES DE INPUT ---

    fun onNombreChange(newNombre: String) {
        _state.update { it.copy(nombre = newNombre, errorNombre = false) }
    }

    fun onDescripcionChange(newDescripcion: String) {
        _state.update { it.copy(descripcion = newDescripcion) }
    }

    fun onPrecioChange(newPrecio: String) {
        _state.update { it.copy(precio = newPrecio, errorPrecio = false) }
    }

    // --- LÓGICA DE SELECCIÓN DE IMAGEN ---

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


    // --- LÓGICA PRINCIPAL: CREAR Y SUBIR IMAGEN ---

    fun onGuardarProductoClick() {
        val s = _state.value

        val precioParseado = s.precio.toDoubleOrNull()

        val nombreValido = s.nombre.isNotBlank()
        val precioValido = precioParseado != null && precioParseado > 0
        val imagenSeleccionada = s.imagenUri != null

        // 1. Validación de campos
        if (!nombreValido || !precioValido || !imagenSeleccionada) {
            _state.update {
                it.copy(
                    errorNombre = !nombreValido,
                    errorPrecio = !precioValido,
                )
            }
            return
        }


        viewModelScope.launch {
            try {
                // 2. ⬆️ SUBIR LA IMAGEN A FIREBASE STORAGE
                val imageUri = s.imagenUri!!

                // Crea un nombre único para la imagen
                val uniqueId = UUID.randomUUID().toString()
                val imageName = "productos/${s.nombre}_${uniqueId}.jpg"
                val storageRef = storage.reference.child(imageName)

                // Sube el archivo y espera la finalización
                val uploadTask = storageRef.putFile(imageUri).await()

                // Obtiene el URL de descarga PERMANENTE
                val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

                // 3. 📝 CREAR Y GUARDAR PRODUCTO EN REPOSITORY
                val nuevoProducto = Producto(
                    id = "",
                    nombre = s.nombre,
                    descripcion = s.descripcion,
                    precio = precioParseado!!,
                    imagenUrl = downloadUrl // URL permanente
                )

                repository.addProducto(nuevoProducto)

                // 4. Notificar a la Vista
                _state.update {
                    it.copy(
                        creacionExitosa = true,
                        nombre = "",
                        descripcion = "",
                        precio = "",
                        imagenUri = null
                    )
                }

            } catch (e: Exception) {
                println("Error al guardar producto o subir imagen: ${e.message}")
                // Aquí podrías actualizar el estado con un error visible para el usuario
            }
        }
    }

    fun resetCreacionExitosa() {
        _state.update { it.copy(creacionExitosa = false) }
    }
}