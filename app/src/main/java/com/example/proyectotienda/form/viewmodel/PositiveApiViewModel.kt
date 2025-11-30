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

// -------------------------------------------------------
//                     DATA CLASS
// -------------------------------------------------------
data class PositiveItem(
    val id: Int,
    val text: String,
    val lang: String,
    val category_id: Int,
    val author_id: Int?
)

// -------------------------------------------------------
//                     API SERVICE
// -------------------------------------------------------
interface PositiveApiService {
    @GET("phrases/esp")   // ⬅️ Ruta correcta
    suspend fun getPositiveItems(): List<PositiveItem>
}

// -------------------------------------------------------
//                  RETROFIT CLIENT
// -------------------------------------------------------
object PositiveApiClient {

    private const val BASE_URL = "https://www.positive-api.online/"  // ⬅️ Base URL correcta

    val api: PositiveApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // ⬅️ Import correcto
            .build()
            .create(PositiveApiService::class.java)
    }
}

// -------------------------------------------------------
//                       VIEWMODEL
// -------------------------------------------------------
class PositiveApiViewModel : ViewModel() {

    private val _phrase = MutableStateFlow("Cargando frase positiva…")
    val phrase: StateFlow<String> = _phrase

    init {
        startAutoRefresh()
    }

    // 🔄 Se ejecuta en bucle y actualiza la frase cada 10 segundos
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                loadPhrase()
                delay(30_000) // 10 segundos
            }
        }
    }

    fun loadPhrase() {
        viewModelScope.launch {
            try {
                val response = PositiveApiClient.api.getPositiveItems()

                _phrase.value =
                    response.randomOrNull()?.text ?: "No se encontraron frases positivas."

            } catch (e: Exception) {
                _phrase.value = "Error al cargar frase positiva."
            }
        }
    }
}

