package com.example.proyectotienda.navigation


sealed class Screens(val route: String) {
    object Login: Screens("Login_Screen")
    object HomeScreen: Screens("Home_Screen")
}