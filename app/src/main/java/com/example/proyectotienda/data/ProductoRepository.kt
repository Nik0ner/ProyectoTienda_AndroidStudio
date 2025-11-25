package com.example.proyectotienda.data

import com.example.proyectotienda.product.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object ProductoRepository {

    // ⬅️ 1. Referencia a la base de datos Firestore
    private val db = FirebaseFirestore.getInstance()
    private val productosCollection = db.collection("productos")

    // ----------------------------------------------------
    // READ (LEER): Usamos un Flow para escuchar cambios en tiempo real
    // ----------------------------------------------------

    fun getProductos() = callbackFlow {
        // Usa un Listener para escuchar la colección
        val subscription = productosCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Cierra el Flow si hay un error
                    return@addSnapshotListener
                }

                // Mapea los documentos de Firebase a objetos Producto
                val productos = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Producto::class.java)
                } ?: emptyList()

                // Envía la nueva lista al Flow
                trySend(productos)
            }

        // ⬅️ Cuando el colector deja de escuchar, se cierra el listener
        awaitClose { subscription.remove() }
    }

    // ----------------------------------------------------
    // READ BY ID (LEER POR ID): Función suspendida para la edición
    // ----------------------------------------------------

    suspend fun getProductoById(id: String): Producto? {
        return try {
            val document = productosCollection.document(id).get().await()
            document.toObject(Producto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // ----------------------------------------------------
    // CREATE (CREAR): Función suspendida para subir datos
    // ----------------------------------------------------

    suspend fun addProducto(producto: Producto) {
        // Firestore crea automáticamente el ID del documento
        productosCollection.add(producto).await()
    }

    // ----------------------------------------------------
    // UPDATE (ACTUALIZAR): Usa el ID del producto para actualizar
    // ----------------------------------------------------

    suspend fun updateProducto(updatedProducto: Producto) {
        // Usamos set(updatedProducto) para reemplazar el documento completo
        productosCollection.document(updatedProducto.id).set(updatedProducto).await()
    }

    // ----------------------------------------------------
    // DELETE (BORRAR): Usa el ID del producto para borrar
    // ----------------------------------------------------

    suspend fun deleteProducto(productoId: String) {
        productosCollection.document(productoId).delete().await()
    }
}