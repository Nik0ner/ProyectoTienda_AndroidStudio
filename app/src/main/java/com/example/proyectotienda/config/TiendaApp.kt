package com.example.proyectotienda.config // Cambia esto por tu paquete

import android.app.Application
import com.google.firebase.FirebaseApp

class TiendaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}