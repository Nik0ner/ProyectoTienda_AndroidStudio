package com.example.proyectotienda.product_creation.viewmodel

import android.net.Uri // 💥 IMPORTACIÓN NECESARIA

data class ProductCreationUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",

    val errorNombre: Boolean = false,
    val errorPrecio: Boolean = false,

    val creacionExitosa: Boolean = false,

    // 💡 Propiedades para la imagen
    val imagenUri: Uri? = null,
    val showSourceDialog: Boolean = false
)