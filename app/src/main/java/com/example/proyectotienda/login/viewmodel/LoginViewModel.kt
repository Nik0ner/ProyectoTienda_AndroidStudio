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

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()
    // ------------------------------------
    // 1. MANEJADORES DE INPUT
    // ------------------------------------
    fun onEmailChange(newEmail: String) {
        _state.update {
            it.copy(
                email = newEmail,
                showEmailError = false
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _state.update {
            it.copy(
                password = newPassword,
                showPasswordError = false
            )
        }
    }
    // ------------------------------------
    // 2. LÓGICA DE LOGIN CON FIREBASE
    // ------------------------------------
    fun onLoginClick(s: LoginUiState) {
        viewModelScope.launch {

            try {
                auth.signInWithEmailAndPassword(s.email, s.password).await()
                _state.update {
                    it.copy(
                        isLoginSuccessful = true,
                        generalErrorMessage = null,
                    )
                }

            } catch (e: Exception) {
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

    // ------------------------------------
    // 3. FUNCIONES AUXILIARES
    // ------------------------------------
    private fun getTranslatedErrorMessage(exception: Exception): String {
        // 💡 Intenta primero castear la excepción a FirebaseAuthException
        return if (exception is FirebaseAuthException) {
            // Usa el errorCode y normalízalo a minúsculas para un chequeo más seguro
            val errorCode = exception.errorCode.lowercase()

            when (errorCode) {
                // Códigos comunes con prefijo:
                "auth/invalid-credential", "error_invalid_credential" -> "Credenciales no válidas. Verifica tu correo y contraseña."
                "auth/invalid-email" -> "El formato del correo electrónico no es válido."
                "auth/wrong-password" -> "Contraseña incorrecta."
                "auth/user-not-found" -> "No existe un usuario registrado con este correo."
                "auth/user-disabled" -> "Esta cuenta ha sido deshabilitada."
                "auth/too-many-requests" -> "Demasiados intentos fallidos. Inténtalo más tarde."
                // Códigos comunes sin prefijo (legacy):
                "error_invalid_email" -> "El formato del correo electrónico no es válido."
                "error_wrong_password" -> "Contraseña incorrecta."
                "error_user_not_found" -> "No existe un usuario registrado con este correo."

                // Caso donde el error existe, pero no está en el mapeo:
                else -> "Error de autenticación desconocido (Code: ${exception.errorCode})."
            }
        } else {
            // Si no es un FirebaseAuthException, asumimos un problema de red o interno.
            "Ocurrió un error de conexión. Revisa tu red."
        }
    }

    fun resetLoginSuccessful() {
        _state.update { it.copy(isLoginSuccessful = false) }
    }
}