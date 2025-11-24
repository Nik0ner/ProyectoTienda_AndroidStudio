package com.example.proyectotienda.product_update.viewmodel

data class ProductUpdateUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",

    val cargando: Boolean = false,
    val errorNombre: Boolean = false,
    val errorPrecio: Boolean = false,
    val actualizacionExitosa: Boolean = false,
    val errorMessage: String? = null
)