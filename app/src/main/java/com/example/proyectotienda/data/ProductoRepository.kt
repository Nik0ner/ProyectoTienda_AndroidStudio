package com.example.proyectotienda.data

import com.example.proyectotienda.product.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object ProductoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val productosCollection = db.collection("productos") // Enlace a la colección "productos" en Firebase

    // Leer: Obtiene todos los productos en tiempo real usando un Flow
    fun getProductos() = callbackFlow {
        // Usa Listener para escuchar cambios en la colección
        val subscription = productosCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // Mapea documentos de Firebase a la lista de Producto
                val productos = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Producto::class.java)
                } ?: emptyList()

                trySend(productos)
            }

        // Cierra el listener de Firebase al cerrar el Flow
        awaitClose { subscription.remove() }
    }

    // Leer por ID: Obtiene un producto específico
    suspend fun getProductoById(id: String): Producto? {
        return try {
            val document = productosCollection.document(id).get().await()
            document.toObject(Producto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Crear: Agrega un nuevo producto a la base de datos
    suspend fun addProducto(producto: Producto) {
        productosCollection.add(producto).await()
    }

    // Actualizar: Modifica un producto existente usando su ID
    suspend fun updateProducto(updatedProducto: Producto) {
        productosCollection.document(updatedProducto.id).set(updatedProducto).await()
    }

    // Eliminar: Borra un producto por su ID
    suspend fun deleteProducto(productoId: String) {
        productosCollection.document(productoId).delete().await()
    }
}