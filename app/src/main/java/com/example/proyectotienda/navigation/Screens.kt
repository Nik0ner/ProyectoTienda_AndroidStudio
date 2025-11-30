package com.example.proyectotienda.navigation


sealed class Screens(val route: String) {
    object Login: Screens("Login_Screen")
    object HomeScreen: Screens("Home_Screen")
    object Form: Screens("Form_Screen")
    object ProductCreation: Screens("product_creation_screen")
    object RecoverPassword : Screens("recover_password")
    data object ProductUpdate: Screens("product_update_screen") {
        fun withId(id: String) = "product_update_screen/$id"
    }
}