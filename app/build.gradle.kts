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
    // Dependencias básicas de Kotlin y AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --------------------------------------------------------------------------
    //             ✅ ARQUITECTURA LIMPIA DE COMPOSE (¡La clave!)
    // --------------------------------------------------------------------------

    // 1. COMPOSE BOM: Gestiona todas las versiones UI/Material/Runtime
    implementation(platform(libs.androidx.compose.bom))

    // 2. Dependencias de UI/Material sin versión (el BOM las define)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3) // Material 3 (CLAVE para TextFieldDefaults)

    // Dependencias adicionales de Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // 3. NAVIGATION (Usar solo la versión más reciente)
    implementation("androidx.navigation:navigation-compose:2.8.3") // o la versión estable más alta

    // --------------------------------------------------------------------------
    //             ✅ DEPENDENCIAS DE FIREBASE
    // --------------------------------------------------------------------------

    // Dependencia de la Plataforma Firebase (Importante para que todo sea compatible)
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Dependencias de prueba (Mantener las que ya tenías)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("androidx.compose.material:material-icons-extended-android")


}