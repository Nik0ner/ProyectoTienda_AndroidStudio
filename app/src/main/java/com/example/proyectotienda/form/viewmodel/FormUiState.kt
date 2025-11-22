package com.example.proyectotienda.form.viewmodel

data class FormUiState(
    val usuario: String = "",
    val pass: String = "",
    val correo: String = "",

    // ERRORES
    val usuarioError: Boolean = false,
    val passError: Boolean = false,
    val correoError: Boolean = false,

    // MENSAJES DE ERROR
    val passErrorMsg: String = "",
    val correoErrorMsg: String = "",

    // ESTADO DE EXITO
    val isRegistroExitoso: Boolean = false
)