package com.example.proyectotienda.product_creation.viewmodel

import android.net.Uri

data class ProductCreationUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val imagenUri: Uri? = null,
    val showSourceDialog: Boolean = false,
    val errorNombre: Boolean = false,
    val errorPrecio: Boolean = false,
    val creacionExitosa: Boolean = false,
    // 💡 Campos para manejo de carga y errores
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)