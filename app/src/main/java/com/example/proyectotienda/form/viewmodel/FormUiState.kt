package com.example.proyectotienda.form.viewmodel

data class FormUiState(

    val usuario: String = "",
    val pass: String = "",
    val correo: String = "",

    val usuarioError: Boolean = false,
    val passError: Boolean = false,
    val correoError: Boolean = false,

    val passErrorMsg: String = "",
    val correoErrorMsg: String = "",

    val isRegistroExitoso: Boolean = false,
    val generalErrorMessage: String? = null
)