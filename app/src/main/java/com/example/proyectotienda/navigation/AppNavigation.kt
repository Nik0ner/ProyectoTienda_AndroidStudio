package com.example.proyectotienda.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectotienda.login.LoginScreen

import com.example.proyectotienda.login.HomeScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController , startDestination = Screens.Login.route ){
        composable(route = Screens.Login.route){
            LoginScreen(navController)
        }
        composable(route = Screens.HomeScreen.route){
            HomeScreen(navController)
        }

    }
}