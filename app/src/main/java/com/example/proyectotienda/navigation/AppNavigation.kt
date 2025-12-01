package com.example.proyectotienda.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectotienda.cart.CartViewModel
import com.example.proyectotienda.cart.ui.CartScreen
import com.example.proyectotienda.development.ui.DevelopmentScreen
import com.example.proyectotienda.form.FormScreen
import com.example.proyectotienda.home.HomeScreen
import com.example.proyectotienda.login.LoginScreen
import com.example.proyectotienda.product_creation.ProductCreationScreen
import com.example.proyectotienda.product_update.ProductUpdateScreen
import com.example.proyectotienda.recover.RecoverPasswordScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 💡 PASO 1: CREAMOS LA INSTANCIA ÚNICA DE CartViewModel AQUÍ
    val cartViewModel: CartViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route
    ){

        composable(route = Screens.Login.route){
            LoginScreen(navController)
        }

        composable(route = Screens.HomeScreen.route){
            HomeScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        composable(route = Screens.Form.route){
            FormScreen(navController)
        }

        composable(route = Screens.ProductCreation.route) {
            ProductCreationScreen(navController)
        }

        composable(route = Screens.RecoverPassword.route) {
            RecoverPasswordScreen(navController)
        }

        composable(route = Screens.Cart.route) {
            CartScreen(
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable (route = Screens.Development.route){
            DevelopmentScreen(navController)
        }

        composable(
            route = Screens.ProductUpdate.route + "/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductUpdateScreen(
                navController = navController,
                productId = productId
            )
        }
    }
}