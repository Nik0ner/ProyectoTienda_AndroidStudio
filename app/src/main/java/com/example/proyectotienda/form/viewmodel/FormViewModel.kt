package com.example.proyectotienda.form.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotienda.validacion.validateForm // Asumo que esto contiene tu validación
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FormViewModel : ViewModel() {

    // 💡 Inicialización de Firebase Auth
    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(FormUiState())
    val state = _state.asStateFlow()

    // --- MANEJADORES DE INPUT ---
    fun onUsuarioChange(texto: String) {
        _state.update { it.copy(usuario = texto, usuarioError = false) }
    }

    // Renombrado para consistencia
    fun onPasswordChange(texto: String) {
        _state.update { it.copy(pass = texto, passError = false, passErrorMsg = "") }
    }

    // Renombrado para consistencia
    fun onEmailChange(texto: String) {
        _state.update { it.copy(correo = texto, correoError = false, correoErrorMsg = "") }
    }

    // --- LÓGICA DE REGISTRO CON FIREBASE ---

    fun onRegistrarClick() {
        val s = _state.value

        // 1. VALIDACION LOCAL con tu función validateForm
        val validationResult = validateForm(s.usuario, s.pass, s.correo)

        // 2. ACTUALIZAMOS EL ESTADO CON LOS RESULTADOS DE LA VALIDACION
        _state.update {
            it.copy(
                usuarioError = validationResult.usuarioError,
                passError = validationResult.passError,
                correoError = validationResult.correoError,
                passErrorMsg = validationResult.passErrorMessage,
                correoErrorMsg = validationResult.correoErrorMessage,
                // Limpiamos el error general antes del intento de Firebase
                generalErrorMessage = null
            )
        }

        // 3. Si la validación local falla, terminamos aquí
        if (validationResult.usuarioError || validationResult.passError || validationResult.correoError) {
            return
        }

        // 4. Si es válido, iniciamos la operación de Firebase en una coroutine
        viewModelScope.launch {
            try {
                // 5. ¡LLAMADA A FIREBASE PARA CREAR EL USUARIO!
                // Usamos el email (correo) y la contraseña (pass) del estado actual
                auth.createUserWithEmailAndPassword(s.correo, s.pass).await()

                // 6. Éxito: Se completa la operación de Firebase
                _state.update {
                    it.copy(
                        isRegistroExitoso = true, // Activamos la bandera de éxito
                        generalErrorMessage = null
                    )
                }

            } catch (e: Exception) {
                // 7. Fallo: Capturamos y traducimos el error de Firebase/Red
                val translatedMessage = getTranslatedErrorMessage(e)

                _state.update {
                    it.copy(
                        isRegistroExitoso = false, // Aseguramos que la bandera esté en falso
                        generalErrorMessage = translatedMessage
                    )
                }
            }
        }
    }

    // Función auxiliar para que no navegue infinitamente al volver atrás
    fun resetRegistroExitoso() {
        _state.update { it.copy(isRegistroExitoso = false) }
    }

    // --- MANEJO DE ERRORES DE FIREBASE ---

    private fun getTranslatedErrorMessage(exception: Exception): String {
        return if (exception is FirebaseAuthException) {
            val errorCode = exception.errorCode.lowercase()

            when (errorCode) {
                "auth/email-already-in-use" -> "Ya existe una cuenta con este correo electrónico."
                "auth/invalid-email" -> "El formato del correo electrónico no es válido."
                "auth/weak-password" -> "La contraseña debe tener al menos 6 caracteres."
                "auth/operation-not-allowed" -> "La autenticación por email y contraseña no está habilitada."
                "auth/network-request-failed" -> "Error de red. Revisa tu conexión a internet." // Añadido para mejor diagnóstico

                else -> "Error de registro desconocido (Código: ${exception.errorCode})."
            }
        } else {
            "Ocurrió un error de conexión o interno. Revisa tu red."
        }
    }
}