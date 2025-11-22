package com.example.proyectotienda.login.viewmodel

data class LoginUiState(

    val email: String = "",
    val password: String = "",

    val isLoginSuccessful: Boolean = false,
    val showEmailError: Boolean = false,
    val showPasswordError: Boolean = false,
    val generalErrorMessage: String? = null
)