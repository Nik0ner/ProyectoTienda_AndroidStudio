package com.example.proyectotienda.recover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecoverPasswordViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance() // Inicialización de Firebase Auth

    // Estado observable para mostrar mensajes a la UI (éxito o error)
    private val _uiState = MutableStateFlow<String?>(null)
    val uiState: StateFlow<String?> = _uiState

    /**
     * Envía un correo electrónico de restablecimiento de contraseña al email proporcionado.
     */
    fun sendRecoveryEmail(email: String) {
        viewModelScope.launch {
            // 1. Validación de entrada
            if (email.isEmpty()) {
                _uiState.value = "Debes ingresar un correo."
                return@launch
            }

            // 2. Llamada a la API de Firebase
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    // Éxito: notifica que el correo fue enviado
                    _uiState.value = "Correo de recuperación enviado."
                }
                .addOnFailureListener { e ->
                    // Fallo: notifica el error
                    _uiState.value = "Error: ${e.message}"
                }
        }
    }

    /**
     * Limpia el mensaje de estado después de ser mostrado en la UI.
     */
    fun clearMessage() {
        _uiState.value = null
    }
}