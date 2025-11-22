package com.example.proyectotienda.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = YellowPrimary,          // Botones y AppBar
    onPrimary = BlackPrimary,         // Texto en AppBar y botones

    secondary = BlackPrimary,         // Fondos oscuros
    onSecondary = White,              // Texto en fondo negro

    background = White,               // Fondo general
    onBackground = BlackPrimary,      // Texto principal

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