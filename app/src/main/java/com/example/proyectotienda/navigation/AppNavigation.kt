package com.example.proyectotienda.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectotienda.form.FormScreen

import com.example.proyectotienda.login.LoginScreen

import com.example.proyectotienda.home.HomeScreen
import com.example.proyectotienda.product_creation.ProductCreationScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = Screens.HomeScreen.route
    ){

        composable(route = Screens.Login.route){
            LoginScreen(navController)
        }

        composable(route = Screens.HomeScreen.route){
            HomeScreen(navController)
        }

        composable(route = Screens.Form.route){
            FormScreen(navController)

        }

        composable(route = Screens.ProductCreation.route) {
            ProductCreationScreen(navController)
        }
    }
}