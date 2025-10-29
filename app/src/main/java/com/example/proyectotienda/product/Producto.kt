package com.example.proyectotienda.product

data class Producto(
    val id: String,
    val nombre: String,
    val descripcion: String, // Podría ser el tipo de zapatilla
    val precio: Double,
    val imageUrl: String
){

}