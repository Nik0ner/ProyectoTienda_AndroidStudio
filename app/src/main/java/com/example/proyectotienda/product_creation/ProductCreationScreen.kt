package com.example.proyectotienda.product_creation

import androidx.compose.foundation.Image
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.product_creation.viewmodel.ProductCreationViewModel
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.product_creation.viewmodel.ProductCreationUiState
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap

// 🚨 IMPORTACIONES REQUERIDAS
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.io.File
import androidx.core.content.FileProvider
import android.content.Context
import androidx.compose.material.icons.filled.PhotoLibrary
import java.io.InputStream
import java.io.OutputStream

// ----------------------------------------------------------------------
// 📸 FUNCIONES AUXILIARES (DEJADAS FUERA DE LOS COMPOSABLES PARA BREVEDAD)
// ----------------------------------------------------------------------

/** Crea una URI de destino estable para la cámara. */
fun createCameraTempUri(context: Context): Uri {
    val tempFile = File.createTempFile("temp_image", ".jpg", context.filesDir).apply { createNewFile() }
    return FileProvider.getUriForFile(context, "com.example.proyectotienda.fileprovider", tempFile)
}

/** Copia la URI de la Galería a un archivo interno estable. */
fun copyUriToTempFile(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.filesDir, "upload_temp_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }
        FileProvider.getUriForFile(context, "com.example.proyectotienda.fileprovider", tempFile)
    } catch (e: Exception) {
        null
    }
}


// ----------------------------------------------------------------------
// 💻 PANTALLA PRINCIPAL (ProductCreationScreen)
// ----------------------------------------------------------------------

@Composable
fun ProductCreationScreen(
    navController: NavController,
    viewModel: ProductCreationViewModel = viewModel()
) {
    val estado by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }


    // --- LANZADORES DE ACTIVIDADES ---

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.setShowSourceDialog(false)
            if (uri != null) {
                val stableUri = copyUriToTempFile(context, uri)
                if (stableUri != null) {
                    viewModel.onImageSelected(stableUri)
                } else {
                    viewModel.setError("Error al copiar la imagen de la galería internamente.")
                }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            viewModel.setShowSourceDialog(false)
            if (success && tempCameraUri != null) {
                viewModel.onImageSelected(tempCameraUri)
            }
            tempCameraUri = null
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            viewModel.setShowSourceDialog(false)
            if (isGranted) {
                val uri = createCameraTempUri(context)
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } else {
                viewModel.setError("Permiso de cámara denegado.")
            }
        }
    )


    // --- LÓGICA DE EFECTOS (Navegación y Errores) ---

    LaunchedEffect(estado.creacionExitosa) {
        if (estado.creacionExitosa) {
            navController.navigate(Screens.HomeScreen.route) { popUpTo(Screens.Login.route) { inclusive = true } }
            viewModel.resetCreacionExitosa()
        }
    }

    LaunchedEffect(estado.errorMessage) {
        estado.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(message = msg, actionLabel = "OK", duration = SnackbarDuration.Short)
            viewModel.clearError()
        }
    }

    // --- DIÁLOGO DE SELECCIÓN ---

    if (estado.showSourceDialog) {
        ImageSourceDialog(
            onDismiss = { viewModel.setShowSourceDialog(false) },
            onGalleryClick = { galleryLauncher.launch("image/*") },
            onCameraClick = {
                when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                    PackageManager.PERMISSION_GRANTED -> {
                        val uri = createCameraTempUri(context)
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                    else -> permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }

    // --- SCAFFOLD Y CONTENIDO PRINCIPAL ---

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        ProductCreationContent(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            estado = estado,
            viewModel = viewModel,
            navController = navController
        )
    }
}

// ----------------------------------------------------------------------
// --- 3. DIÁLOGO DE SELECCIÓN (Sin cambios) ---
// ----------------------------------------------------------------------

@Composable
fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Imagen", style = MaterialTheme.typography.titleMedium) },
        text = { Text("¿Desde dónde deseas cargar la imagen?") },
        confirmButton = {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Button(onClick = onGalleryClick, modifier = Modifier.fillMaxWidth()) { Text("Galería") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCameraClick, modifier = Modifier.fillMaxWidth()) { Text("Cámara") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}


// ----------------------------------------------------------------------
// --- 4. PRODUCT CREATION CONTENT (AJUSTADO PARA SER MÁS COMPACTO) ---
// ----------------------------------------------------------------------

@Composable
fun ProductCreationContent(
    modifier: Modifier = Modifier,
    estado: ProductCreationUiState,
    viewModel: ProductCreationViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    // Lógica de carga de Bitmap para Preview
    val previewBitmap: ImageBitmap? = remember(estado.imagenUri) {
        estado.imagenUri?.let { uri ->
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    BitmapFactory.decodeStream(input)?.asImageBitmap()
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.White,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Botón de Cerrar (X)
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cerrar/Volver",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("Agregar Producto", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campos de Texto (Espaciados de 16.dp)
                    OutlinedTextField(
                        value = estado.nombre,
                        onValueChange = { viewModel.onNombreChange(it) },
                        label = { Text("Nombre del Producto") },
                        isError = estado.errorNombre,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (estado.errorNombre) {
                        Text("El nombre no puede estar vacío.", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = estado.descripcion,
                        onValueChange = { viewModel.onDescripcionChange(it) },
                        label = { Text("Descripción") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = estado.precio,
                        onValueChange = { viewModel.onPrecioChange(it) },
                        label = { Text("Precio ($)") },
                        isError = estado.errorPrecio,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (estado.errorPrecio) {
                        Text("Ingrese un precio válido (número mayor a 0).", color = MaterialTheme.colorScheme.error)
                    }

                    // 🚨 AJUSTE DE ESPACIADO: Reducido de 24.dp a 16.dp antes del botón de imagen
                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón circular para subir imagen
                    Button(
                        onClick = { if (!estado.isLoading) viewModel.setShowSourceDialog(true) },
                        modifier = Modifier.size(56.dp),
                        enabled = !estado.isLoading,
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = "Subir Imagen",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // 🚨 AJUSTE DE ESPACIADO: Reducido a 8.dp entre el botón y el texto
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Subir Imagen")

                    // 💡 PREVIEW DE IMAGEN
                    if (previewBitmap != null) {
                        // 🚨 AJUSTE DE ESPACIADO: Reducido a 8.dp antes de la preview
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            bitmap = previewBitmap,
                            contentDescription = "Preview del producto",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                    } else if (estado.imagenUri != null) {
                        // 🚨 AJUSTE DE ESPACIADO: Reducido a 8.dp antes del mensaje de error
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("❌ Error al cargar preview de imagen.", color = Color.Red)
                    }

                    // 🚨 AJUSTE DE ESPACIADO: Reducido de 32.dp a 16.dp antes del botón de guardar
                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de Guardar (CREATE)
                    Button(
                        onClick = { viewModel.onGuardarProductoClick() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !estado.isLoading
                    ) {
                        if (estado.isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Text("GUARDAR PRODUCTO")
                        }
                    }
                    // 🚨 NOTA: Se eliminó el Spacer final. El padding del Column (32.dp) será el espacio más bajo.
                }
            }
        }
    }
}