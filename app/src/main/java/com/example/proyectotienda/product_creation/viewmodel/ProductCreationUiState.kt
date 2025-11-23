package com.example.proyectotienda.product_creation.viewmodel

data class ProductCreationUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "", // Usamos String para la entrada de texto

    val errorNombre: Boolean = false,
    val errorPrecio: Boolean = false,

    // Bandera para notificar a la Vista cuando la creación fue exitosa y debe navegar
    val creacionExitosa: Boolean = false
)

