package com.example.proyectotienda.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.proyectotienda.navigation.AppNavigation
import com.example.proyectotienda.navigation.Screens

@Composable
fun LoginScreen(navController: NavController) {
    Scaffold { paddingValues ->
        BodyContent(
            Modifier.padding(paddingValues),
            navController = navController
        )
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Text("Estas en el Login")
            Button(onClick = { navController.navigate(route = Screens.HomeScreen.route) }) {
                Text("Navega")
        }
    }
}
