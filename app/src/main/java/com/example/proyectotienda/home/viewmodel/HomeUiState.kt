package com.example.proyectotienda.home.viewmodel

import com.example.proyectotienda.product.Producto

data class HomeUiState(

    val productos: List<Producto> = emptyList(),

    val isLoading: Boolean = false,

    val errorMessage: String? = null

)