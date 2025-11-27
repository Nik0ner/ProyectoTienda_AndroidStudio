package com.example.proyectotienda.form.viewmodel

data class FormUiState(
    // --- PROPIEDADES DE INPUT ---
    val usuario: String = "",
    val pass: String = "",
    val correo: String = "",

    // --- ERRORES DE CAMPO ---
    val usuarioError: Boolean = false,
    val passError: Boolean = false,
    val correoError: Boolean = false,

    // --- MENSAJES DE ERROR DE CAMPO ---
    val passErrorMsg: String = "",
    val correoErrorMsg: String = "",

    // --- ESTADO DE EXITO DE FIREBASE ---
    val isRegistroExitoso: Boolean = false,

    // 💥 ¡LA PROPIEDAD FALTANTE! 💥
    /**
     * Mensaje de error general para errores que no son de un campo específico
     * (ej. Error de red, Email ya en uso, o error de Firebase traducido).
     */
    val generalErrorMessage: String? = null
)