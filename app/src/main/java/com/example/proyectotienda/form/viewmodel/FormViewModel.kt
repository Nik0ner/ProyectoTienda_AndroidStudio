package com.example.proyectotienda.form.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectotienda.validacion.validateForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormViewModel : ViewModel() {
    private val _state = MutableStateFlow(FormUiState())
    val state = _state.asStateFlow()

    fun onUsuarioChange(texto: String) {
        _state.update { it.copy(usuario = texto, usuarioError = false) }
    }

    fun onPassChange(texto: String) {
        _state.update { it.copy(pass = texto, passError = false, passErrorMsg = "") }
    }

    fun onCorreoChange(texto: String) {
        _state.update { it.copy(correo = texto, correoError = false, correoErrorMsg = "") }
    }

    fun onRegistrarClick() {
        val s = _state.value

        // VALIDACION CON EL FORMULARIO DE FormValidacion
        val validationResult = validateForm(s.usuario, s.pass, s.correo)

        // ACTUALIZAMOS CON LOS RESULTADOS DE LA VALIDACION
        _state.update {
            it.copy(
                usuarioError = validationResult.usuarioError,
                passError = validationResult.passError,
                correoError = validationResult.correoError,
                passErrorMsg = validationResult.passErrorMessage,
                correoErrorMsg = validationResult.correoErrorMessage
            )
        }

        // Si NO hay ningún error, activamos la bandera de éxito para navegar
        if (!validationResult.usuarioError && !validationResult.passError && !validationResult.correoError) {
            _state.update { it.copy(isRegistroExitoso = true) }
        }
    }

    // Función auxiliar para que no navegue infinitamente al volver atrás
    fun resetRegistroExitoso() {
        _state.update { it.copy(isRegistroExitoso = false) }
    }
}