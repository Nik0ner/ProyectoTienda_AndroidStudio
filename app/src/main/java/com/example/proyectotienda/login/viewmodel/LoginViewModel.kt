package com.example.proyectotienda.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance() // Inicializa Firebase Authentication

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow() // Estado observado por la UI
    // 1. MANEJADORES DE INPUT
    // Actualiza el email en el estado
    fun onEmailChange(newEmail: String) {
        _state.update {
            it.copy(
                email = newEmail,
                showEmailError = false
            )
        }
    }

    // Actualiza la contraseña en el estado
    fun onPasswordChange(newPassword: String) {
        _state.update {
            it.copy(
                password = newPassword,
                showPasswordError = false
            )
        }
    }

    // 2. LÓGICA DE LOGIN CON FIREBASE
    // Lanza la solicitud de inicio de sesión
    fun onLoginClick(s: LoginUiState) {
        viewModelScope.launch {

            try {
                // Llama a la API de Firebase para autenticar
                auth.signInWithEmailAndPassword(s.email, s.password).await()
                _state.update {
                    it.copy(
                        isLoginSuccessful = true, // Marca el éxito
                        generalErrorMessage = null,
                    )
                }

            } catch (e: Exception) {
                // Maneja el error y traduce el mensaje
                val translatedMessage = getTranslatedErrorMessage(e)

                _state.update {
                    it.copy(
                        generalErrorMessage = translatedMessage,
                        showEmailError = false,
                        showPasswordError = false,
                    )
                }
            }
        }
    }

    // 3. FUNCIONES AUXILIARES
    // Mapea los códigos de error de Firebase a mensajes legibles
    private fun getTranslatedErrorMessage(exception: Exception): String {
        // Intenta castear a FirebaseAuthException para obtener el código
        return if (exception is FirebaseAuthException) {
            val errorCode = exception.errorCode.lowercase()

            when (errorCode) {
                // Errores de credenciales y cuenta
                "auth/invalid-credential", "error_invalid_credential" -> "Credenciales no válidas. Verifica tu correo y contraseña."
                "auth/invalid-email", "error_invalid_email" -> "El formato del correo electrónico no es válido."
                "auth/wrong-password", "error_wrong_password" -> "Contraseña incorrecta."
                "auth/user-not-found", "error_user_not_found" -> "No existe un usuario registrado con este correo."
                "auth/user-disabled" -> "Esta cuenta ha sido deshabilitada."
                "auth/too-many-requests" -> "Demasiados intentos fallidos. Inténtalo más tarde."

                // Error desconocido de autenticación
                else -> "Error de autenticación desconocido (Code: ${exception.errorCode})."
            }
        } else {
            // Error de red o conexión
            "Ocurrió un error de conexión. Revisa tu red."
        }
    }

    // Resetea el indicador de éxito para evitar re-navegación
    fun resetLoginSuccessful() {
        _state.update { it.copy(isLoginSuccessful = false) }
    }
}