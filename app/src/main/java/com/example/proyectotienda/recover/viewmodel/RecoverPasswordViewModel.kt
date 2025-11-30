package com.example.proyectotienda.recover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecoverPasswordViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<String?>(null)
    val uiState: StateFlow<String?> = _uiState

    fun sendRecoveryEmail(email: String) {
        viewModelScope.launch {
            if (email.isEmpty()) {
                _uiState.value = "Debes ingresar un correo."
                return@launch
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    _uiState.value = "Correo de recuperación enviado."
                }
                .addOnFailureListener { e ->
                    _uiState.value = "Error: ${e.message}"
                }
        }
    }

    fun clearMessage() {
        _uiState.value = null
    }
}
