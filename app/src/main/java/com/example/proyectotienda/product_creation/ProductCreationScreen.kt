package com.example.proyectotienda.product_creation

import androidx.compose.foundation.Image
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectotienda.product_creation.viewmodel.ProductCreationViewModel
import com.example.proyectotienda.navigation.Screens
import com.example.proyectotienda.R
import com.example.proyectotienda.product_creation.viewmodel.ProductCreationUiState
import androidx.compose.ui.text.style.TextOverflow

import java.io.File
import androidx.core.content.FileProvider
import android.content.Context
import java.io.InputStream
// ------------------------------------------

// 📸 FUNCIÓN AUXILIAR PARA CREAR URI TEMPORAL DE LA CÁMARA
fun createTempImageUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "temp_image",
        ".jpg",
        context.cacheDir
    ).apply {
        createNewFile()
    }
    return FileProvider.getUriForFile(
        context,
        "com.example.proyectotienda.fileprovider",
        tempFile
    )
}

// 💾 FUNCIÓN AUXILIAR PARA COPIAR URI A ARCHIVO ESTABLE
fun copyUriToTempFile(context: Context, uri: Uri): Uri? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "upload_temp_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Devuelve la URI estable del nuevo archivo
        return Uri.fromFile(tempFile)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreationScreen(
    navController: NavController,
    viewModel: ProductCreationViewModel = viewModel()
) {
    val estado by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Configuramos colores de la barra superior
    val appBarcolor = MaterialTheme.colorScheme.primary
    val appBarContent = MaterialTheme.colorScheme.onPrimary

    // --- 💥 1. ESTADO TEMPORAL PARA LA CÁMARA 💥 ---
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }


    // LANZADOR PARA GALERÍA (MODIFICADO: Copia la URI a un archivo estable)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                // 💥 COPIA EL ARCHIVO Y PASA LA URI ESTABLE
                val stableUri = copyUriToTempFile(context, uri)
                viewModel.onImageSelected(stableUri)
            }
        }
    )

    // LANZADOR PARA CÁMARA (MODIFICADO: Copia la URI a un archivo estable)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempImageUri != null) {
                // 💥 COPIA EL ARCHIVO Y PASA LA URI ESTABLE
                val stableUri = copyUriToTempFile(context, tempImageUri!!)
                viewModel.onImageSelected(stableUri) // Envía la URI estable al ViewModel
            } else {
                viewModel.setShowSourceDialog(false)
            }
        }
    )

    // --- LÓGICA DE NAVEGACIÓN ---

    LaunchedEffect(estado.creacionExitosa) {
        if (estado.creacionExitosa) {
            navController.navigate(Screens.HomeScreen.route) {
                popUpTo(Screens.Login.route) { inclusive = true }
            }
            viewModel.resetCreacionExitosa()
        }
    }

    // --- 2. CONTROL DEL DIÁLOGO ---

    if (estado.showSourceDialog) {
        ImageSourceDialog(
            onDismiss = { viewModel.setShowSourceDialog(false) },
            onGalleryClick = {
                viewModel.setShowSourceDialog(false)
                galleryLauncher.launch("image/*")
            },
            onCameraClick = {
                val uri = createTempImageUri(context)
                tempImageUri = uri
                viewModel.setShowSourceDialog(false)
                cameraLauncher.launch(uri)
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(110.dp),
                title = { Text("Crear Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxHeight()) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver", modifier = Modifier.size(32.dp))
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.trafalgar),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(240.dp)
                            .fillMaxWidth(0.2f),
                        contentScale = ContentScale.Fit
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBarcolor,
                    actionIconContentColor = appBarContent,
                    navigationIconContentColor = appBarContent,
                    titleContentColor = appBarContent
                )
            )
        }
    ) { paddingValues ->
        ProductCreationContent(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.DarkGray),
            estado = estado,
            viewModel = viewModel,
            navController = navController
        )
    }
}

// --- 3. DIÁLOGO DE SELECCIÓN ---
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
                Button(onClick = onGalleryClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Galería")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCameraClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Cámara")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


// --- 4. PRODUCT CREATION CONTENT ---
@Composable
fun ProductCreationContent(
    modifier: Modifier = Modifier,
    estado: ProductCreationUiState,
    viewModel: ProductCreationViewModel,
    navController: NavController
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.White,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Ingreso de Producto", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Campo Nombre
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

                // 2. Campo Descripción
                OutlinedTextField(
                    value = estado.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = { Text("Descripción") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Campo Precio
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

                Spacer(modifier = Modifier.height(24.dp))

                // 💥 BOTÓN PARA SUBIR IMAGEN 💥
                Button(
                    onClick = { viewModel.setShowSourceDialog(true) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("SUBIR IMAGEN")
                }

                // 💡 PREVIEW DE IMAGEN/CONFIRMACIÓN DE URI
                if (estado.imagenUri != null) {
                    val uri = estado.imagenUri

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Imagen seleccionada: OK (URI: ${uri.toString().take(30)}...)",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 4. Botón de Guardar (CREATE)
                Button(
                    onClick = { viewModel.onGuardarProductoClick() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("GUARDAR PRODUCTO")
                }
            }
        }
    }
}