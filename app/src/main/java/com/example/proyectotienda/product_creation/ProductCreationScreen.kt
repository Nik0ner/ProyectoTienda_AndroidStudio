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
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.product_creation.viewmodel.ProductCreationViewModel
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.product_creation.viewmodel.ProductCreationUiState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.io.File
import androidx.core.content.FileProvider
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.shape.RoundedCornerShape
import java.io.InputStream
import java.io.OutputStream
import androidx.compose.foundation.shape.CircleShape

// ---------------------------
// FUNCIONES AUXILIARES
// ---------------------------

fun createCameraTempUri(context: Context): Uri {
    val tempFile = File.createTempFile("temp_image", ".jpg", context.filesDir).apply { createNewFile() }
    return FileProvider.getUriForFile(context, "com.example.proyectotienda.fileprovider", tempFile)
}

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


// ---------------------------
// PANTALLA PRINCIPAL
// ---------------------------

@Composable
fun ProductCreationScreen(
    navController: NavController,
    viewModel: ProductCreationViewModel = viewModel()
) {
    val estado by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.setShowSourceDialog(false)
            if (uri != null) {
                val stableUri = copyUriToTempFile(context, uri)
                if (stableUri != null) {
                    viewModel.onImageSelected(stableUri)
                } else {
                    viewModel.setError("Error al copiar la imagen de la galería.")
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
        onResult = { granted ->
            viewModel.setShowSourceDialog(false)
            if (granted) {
                val uri = createCameraTempUri(context)
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } else {
                viewModel.setError("Permiso de cámara denegado.")
            }
        }
    )

    LaunchedEffect(estado.creacionExitosa) {
        if (estado.creacionExitosa) {
            navController.navigate(Screens.HomeScreen.route) {
                popUpTo(Screens.Login.route) { inclusive = true }
            }
            viewModel.resetCreacionExitosa()
        }
    }

    LaunchedEffect(estado.errorMessage) {
        estado.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        ProductCreationCard(
            modifier = Modifier.padding(it),
            estado = estado,
            viewModel = viewModel,
            navController = navController
        )
    }
}


// ---------------------------
// TARJETA (Similar al login pero con otros colores)
// ---------------------------

@Composable
fun ProductCreationCard(
    modifier: Modifier = Modifier,
    estado: ProductCreationUiState,
    viewModel: ProductCreationViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    val darkGray = Color(0xFF111111)           // Fondo tarjeta (casi negro)
    val yellow = Color(0xFFFFFF33)            // Amarillo OFICIAL 🚨

    val previewBitmap: ImageBitmap? = remember(estado.imagenUri) {
        estado.imagenUri?.let { uri ->
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    BitmapFactory.decodeStream(input)?.asImageBitmap()
                }
            } catch (e: Exception) { null }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            color = darkGray,
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ----------- BOTÓN CERRAR (icono negro) -------------
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = -8.dp) // 👉 Empuja más a la esquina
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFFFFFF33) // 👉 Amarillo oficial
                        )
                    }
                }

                Text(
                    "Agregar Producto",
                    style = MaterialTheme.typography.headlineSmall,
                    color = yellow
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ----------- CAMPOS DE TEXTO -------------
                OutlinedTextField(
                    value = estado.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre del producto", color = yellow) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = yellow,
                        focusedIndicatorColor = yellow,
                        unfocusedIndicatorColor = yellow.copy(alpha = 0.3f),
                        focusedLabelColor = yellow,
                        unfocusedLabelColor = yellow.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = estado.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = { Text("Descripción", color = yellow) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = yellow,
                        focusedIndicatorColor = yellow,
                        unfocusedIndicatorColor = yellow.copy(alpha = 0.3f),
                        focusedLabelColor = yellow,
                        unfocusedLabelColor = yellow.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = estado.precio,
                    onValueChange = { viewModel.onPrecioChange(it) },
                    label = { Text("Precio ($)", color = yellow) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = yellow,
                        focusedIndicatorColor = yellow,
                        unfocusedIndicatorColor = yellow.copy(alpha = 0.3f),
                        focusedLabelColor = yellow,
                        unfocusedLabelColor = yellow.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ----------- BOTÓN CIRCULAR PARA IMAGEN -------------
                Button(
                    onClick = { viewModel.setShowSourceDialog(true) },
                    modifier = Modifier.size(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = yellow,
                        contentColor = Color.Black   // Icono negro
                    ),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = "Subir Imagen")
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (previewBitmap != null) {
                    Image(
                        bitmap = previewBitmap,
                        contentDescription = "Preview",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF222222)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ----------- BOTÓN GUARDAR -------------
                Button(
                    onClick = { viewModel.onGuardarProductoClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = yellow,
                        contentColor = Color.Black    // TEXTO NEGRO
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GUARDAR PRODUCTO")
                }
            }
        }
    }
}



@Composable
fun whiteTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color.White,
    focusedIndicatorColor = Color.White,
    unfocusedIndicatorColor = Color.White,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White
)


// ---------------------------
// DIÁLOGO SELECCIÓN IMAGEN
// ---------------------------

@Composable
fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar imagen") },
        text = { Text("Elige el origen de la imagen") },
        confirmButton = {
            Column {
                Button(onClick = onGalleryClick, modifier = Modifier.fillMaxWidth()) { Text("Galería") }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onCameraClick, modifier = Modifier.fillMaxWidth()) { Text("Cámara") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
