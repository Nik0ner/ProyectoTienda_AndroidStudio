package com.example.proyectotienda.cart.viewmodel

import com.example.proyectotienda.product.Producto

data class CartItem (
    val producto: Producto,
    val cantidad: Int = 1
)