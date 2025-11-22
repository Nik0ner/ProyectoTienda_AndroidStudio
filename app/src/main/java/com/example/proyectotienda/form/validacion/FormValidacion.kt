package com.example.proyectotienda.validacion



data class FormValidationResult(
    val usuarioError: Boolean = false,
    val passError: Boolean = false,
    val correoError: Boolean = false,
    val passErrorMessage: String = "",
    val correoErrorMessage: String = ""
)

fun validateForm(usuario: String, pass: String, correo: String): FormValidationResult {
    // Validación del correo
    val correoEsValido = correo.contains("@") && correo.contains(".")
    val correoErrorMessage = if (!correoEsValido) "Correo inválido" else ""

    // Validación de la contraseña (debe tener al menos una mayúscula)
    val passEsValida = pass.any { it.isUpperCase() }
    val passErrorMessage = if (!passEsValida) "La contraseña debe tener al menos una mayúscula" else ""

    return FormValidationResult(
        usuarioError = usuario.isBlank(),
        passError = !passEsValida,
        correoError = !correoEsValido,
        passErrorMessage = passErrorMessage,
        correoErrorMessage = correoErrorMessage
    )
}