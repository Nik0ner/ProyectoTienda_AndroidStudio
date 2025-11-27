

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proyectotienda"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.proyectotienda"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Dependencias básicas
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // COMPOSE
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

// SOLO UNA librería Material3
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

// Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")

// ----------------------------------------------------------------------
//                      🔥 FIREBASE (Limpio)
// ----------------------------------------------------------------------

// Siempre primero la BoM (Bill of Materials) para gestionar versiones de Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

// Dependencia de Firebase Auth (usa BoM para la versión)
    implementation("com.google.firebase:firebase-auth-ktx")

// Dependencia de Firebase Firestore (usa BoM para la versión)
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

// Dependencia para usar .await() en las tareas de Firebase (corutinas)
// Usamos una versión específica y eliminamos la duplicada.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

// Otros
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.compose.material:material-icons-extended-android")
    implementation(libs.firebase.auth.ktx)

// Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

