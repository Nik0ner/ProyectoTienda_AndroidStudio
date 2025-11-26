package com.example.proyectotienda.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val LightColorScheme = lightColorScheme(
    primary = YellowPrimary,          // Botones y AppBar
    onPrimary = BlackPrimary,         // Texto en AppBar y botones

    secondary = BlackPrimary,         // Fondos oscuros
    onSecondary = White,              // Texto en fondo negro

    background = White,               // Fondo general
    onBackground = BlackPrimary,      // Texto principal

    error = IntensoRed,
    onError = Color.White,
    outline = Color.White,

    surface = White,                  // Cards, contenedores
    onSurface = BlackPrimary          // Texto en cards
)

private val DarkColorScheme = darkColorScheme(
    primary = YellowPrimary,
    onPrimary = BlackPrimary,

    secondary = BlackPrimary,
    onSecondary = YellowPrimary,

    background = BlackPrimary,
    onBackground = White,
    outline = Color.Black,

    error = IntensoRed,
    onError = Color.White,

    surface = BlackPrimary,
    onSurface = YellowPrimary
)


@Composable
fun ProyectoTiendaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}