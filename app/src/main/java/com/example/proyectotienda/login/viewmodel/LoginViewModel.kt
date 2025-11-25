package com.example.proyectotienda.login.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    // Manejadores de Input
    fun onEmailChange(newEmail: String) {
        _state.update {
            it.copy(
                email = newEmail,
                showEmailError = false // Limpia el error al empezar a escribir
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _state.update {
            it.copy(
                password = newPassword,
                showPasswordError = false // Limpia el error al empezar a escribir
            )
        }
    }

    // Lógica principal: Intento de Login
    fun onLoginClick() {
        val s = _state.value

        // 1. Validación simple (Campos no vacíos)
        val emailValid = s.email.isNotBlank() && s.email.contains("@")
        val passwordValid = s.password.isNotBlank()

        if (!emailValid || !passwordValid) {
            _state.update {
                it.copy(
                    showEmailError = !emailValid,
                    showPasswordError = !passwordValid,
                    generalErrorMessage = "Por favor, complete todos los campos correctamente."
                )
            }
            return
        }

        // 2. Simulación de Autenticación
        // En una app real, aquí llamarías a un servicio de autenticación (Firebase, API, etc.)
        if (s.email == "@.com" && s.password == "Pass123") {
            // Éxito: Activamos la bandera para la navegación
            _state.update { it.copy(isLoginSuccessful = true) }
        } else {
            // Fallo: Mostramos un error general
            _state.update {
                it.copy(
                    generalErrorMessage = "Credenciales incorrectas. Intente de nuevo.",
                    showEmailError = false,
                    showPasswordError = false
                )
            }
        }
    }

    // Función para limpiar la bandera de éxito después de navegar
    fun resetLoginSuccessful() {
        _state.update { it.copy(isLoginSuccessful = false) }
    }
}