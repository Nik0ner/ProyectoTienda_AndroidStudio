package com.example.proyectotienda.product

import com.google.firebase.firestore.DocumentId

data class Producto(

    @DocumentId
    var id: String = "",

    var nombre: String = "",
    var descripcion: String = "",
    var precio: Double = 0.0,
    var imagenUrl: String = ""

) {
    constructor() : this("", "", "", 0.0,"")
}
