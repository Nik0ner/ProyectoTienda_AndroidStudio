package com.example.proyectotienda.form.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Define la estructura de los datos recibidos de la API
data class PositiveItem(
    val id: Int,
    val text: String,
    val lang: String,
    val category_id: Int,
    val author_id: Int?
)

// Define la interfaz del servicio API (petición)
interface PositiveApiService {
    @GET("phrases/esp")
    suspend fun getPositiveItems(): List<PositiveItem> // Obtiene la lista de frases
}

// Cliente Retrofit para configurar la conexión a la API
object PositiveApiClient {

    private const val BASE_URL = "https://www.positive-api.online/"

    val api: PositiveApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PositiveApiService::class.java)
    }
}

// ViewModel para gestionar el estado y la lógica de la API
class PositiveApiViewModel : ViewModel() {

    // Estado observable de la frase actual
    private val _phrase = MutableStateFlow("Cargando frase positiva…")
    val phrase: StateFlow<String> = _phrase

    init {
        startAutoRefresh() // Inicia la carga automática al crearse el ViewModel
    }

    // Ejecuta la carga de frases en un bucle con retraso
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                loadPhrase()
                delay(30_000) // Espera 30 segundos
            }
        }
    }

    // Lógica para llamar a la API y actualizar el estado
    fun loadPhrase() {
        viewModelScope.launch {
            try {
                // Realiza la llamada de red
                val response = PositiveApiClient.api.getPositiveItems()

                // Selecciona una frase aleatoria
                _phrase.value =
                    response.randomOrNull()?.text ?: "No se encontraron frases positivas."

            } catch (e: Exception) {
                // Manejo de errores de la conexión
                _phrase.value = "Error al cargar frase positiva."
            }
        }
    }
}