package com.example.proyectotienda.form.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotienda.validacion.validateForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FormViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(FormUiState())
    val state = _state.asStateFlow()

    fun onUsuarioChange(texto: String) {
        _state.update { it.copy(usuario = texto, usuarioError = false) }
    }

    fun onPasswordChange(texto: String) {
        _state.update { it.copy(pass = texto, passError = false, passErrorMsg = "") }
    }

    fun onEmailChange(texto: String) {
        _state.update { it.copy(correo = texto, correoError = false, correoErrorMsg = "") }
    }

    fun onRegistrarClick() {
        val s = _state.value

        val validationResult = validateForm(s.usuario, s.pass, s.correo)

        _state.update {
            it.copy(
                usuarioError = validationResult.usuarioError,
                passError = validationResult.passError,
                correoError = validationResult.correoError,
                passErrorMsg = validationResult.passErrorMessage,
                correoErrorMsg = validationResult.correoErrorMessage,
                generalErrorMessage = null
            )
        }

        if (validationResult.usuarioError || validationResult.passError || validationResult.correoError) {
            return
        }

        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(s.correo, s.pass).await()

                _state.update {
                    it.copy(
                        isRegistroExitoso = true,
                        generalErrorMessage = null
                    )
                }

            } catch (e: Exception) {
                val translatedMessage = getTranslatedErrorMessage(e)

                _state.update {
                    it.copy(
                        isRegistroExitoso = false,
                        generalErrorMessage = translatedMessage
                    )
                }
            }
        }
    }

    fun resetRegistroExitoso() {
        _state.update { it.copy(isRegistroExitoso = false) }
    }

    private fun getTranslatedErrorMessage(exception: Exception): String {
        return if (exception is FirebaseAuthException) {
            val errorCode = exception.errorCode.lowercase()

            when (errorCode) {
                "auth/email-already-in-use" -> "Ya existe una cuenta con este correo electrónico."
                "auth/invalid-email" -> "El formato del correo electrónico no es válido."
                "auth/weak-password" -> "La contraseña debe tener al menos 6 caracteres."
                "auth/operation-not-allowed" -> "La autenticación por email y contraseña no está habilitada."
                "auth/network-request-failed" -> "Error de red. Revisa tu conexión a internet."

                else -> "Error de registro desconocido (Código: ${exception.errorCode})."
            }
        } else {
            "Ocurrió un error de conexión o interno. Revisa tu red."
        }
    }
}