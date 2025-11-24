package com.example.proyectotienda.product_update.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProductUpdateViewModelFactory(private val productId: String) : ViewModelProvider.Factory {

    // Esta función le dice a Compose cómo crear el ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 1. Verifica la clase y construye el ViewModel pasándole el ID
        if (modelClass.isAssignableFrom(ProductUpdateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductUpdateViewModel(productId) as T
        }
        // Si se solicita otra clase, lanza un error
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}