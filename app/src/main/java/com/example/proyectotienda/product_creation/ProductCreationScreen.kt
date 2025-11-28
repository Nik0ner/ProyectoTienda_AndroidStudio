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
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap

// 🚨 NUEVAS IMPORTACIONES REQUERIDAS PARA PERMISOS
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
// --------------------------------------------------

import java.io.File
import androidx.core.content.FileProvider
import android.content.Context
import java.io.InputStream
import java.io.OutputStream

// ----------------------------------------------------------------------
// 📸 FUNCIONES AUXILIARES (Fuera del Composable para evitar recomposiciones)
// ----------------------------------------------------------------------

/**
 * Crea una URI de destino estable para que la cámara guarde la imagen.
 * Utiliza FileProvider para garantizar que la cámara tenga permisos de escritura.
 * @param context Contexto de la aplicación.
 * @return URI de FileProvider para la imagen temporal.
 */
fun createCameraTempUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "temp_image",
        ".jpg",
        context.filesDir // USAR filesDir (más estable que cacheDir)
    ).apply {
        createNewFile()
    }
    return FileProvider.getUriForFile(
        context,
        "com.example.proyectotienda.fileprovider", // ¡DEBE COINCIDIR EXACTAMENTE!
        tempFile
    )
}

/**
 * Copia la URI temporal de la Galería a un archivo interno estable (usando FileProvider).
 * Esto es necesario para asegurar que la URI sea accesible y persista.
 * @param context Contexto de la aplicación.
 * @param uri La URI de contenido (content://) obtenida de la galería.
 * @return URI de FileProvider del archivo copiado, o null si falla.
 */
fun copyUriToTempFile(context: Context, uri: Uri): Uri? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.filesDir, "upload_temp_${System.currentTimeMillis()}.jpg") // USAR filesDir

        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return FileProvider.getUriForFile(
            context,
            "com.example.proyectotienda.fileprovider", // ¡DEBE COINCIDIR EXACTAMENTE!
            tempFile
        )

    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


// ----------------------------------------------------------------------
// 💻 PANTALLA PRINCIPAL
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreationScreen(
    navController: NavController,
    viewModel: ProductCreationViewModel = viewModel()
) {
    val estado by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() } // Para errores

    val appBarcolor = MaterialTheme.colorScheme.primary
    val appBarContent = MaterialTheme.colorScheme.onPrimary

    // --- 1. ESTADO TEMPORAL PARA LA CÁMARA ---
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }


    // LANZADOR PARA GALERÍA
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.setShowSourceDialog(false)
            if (uri != null) {
                // 🚨 PUNTO CRÍTICO 1: Llama a copyUriToTempFile para obtener una URI estable (FileProvider)
                val stableUri = copyUriToTempFile(context, uri)

                // 🚨 PUNTO CRÍTICO 2: Solo si la copia fue exitosa, pasamos la URI estable al ViewModel
                if (stableUri != null) {
                    viewModel.onImageSelected(stableUri)
                } else {
                    viewModel.setError("Error al copiar la imagen de la galería internamente.")
                    viewModel.onImageSelected(null)
                }
            } else {
                viewModel.onImageSelected(null)
            }
        }
    )

    // LANZADOR PARA CÁMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            viewModel.setShowSourceDialog(false)
            if (success && tempCameraUri != null) {
                // 🚨 CRÍTICO: Aquí se usa la URI de FileProvider creada antes.
                viewModel.onImageSelected(tempCameraUri)
            } else {
                viewModel.onImageSelected(null)
            }
            tempCameraUri = null // Limpiar la URI temporal después de usarla
        }
    )

    // ----------------------------------------------------
    // 🚨 NUEVO CÓDIGO: LANZADOR DE PERMISOS DE CÁMARA
    // ----------------------------------------------------
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // Permiso concedido, lanzar la cámara
                val uri = createCameraTempUri(context)
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } else {
                // Permiso denegado
                viewModel.setError("Permiso de cámara denegado. No se puede tomar la foto.")
            }
            viewModel.setShowSourceDialog(false)
        }
    )
    // ----------------------------------------------------


    // --- LÓGICA DE NAVEGACIÓN Y ERRORES ---

    LaunchedEffect(estado.creacionExitosa) {
        if (estado.creacionExitosa) {
            // Navegación exitosa
            navController.navigate(Screens.HomeScreen.route) {
                // Esto limpia la pila de navegación hasta Login y va a Home (asumiendo que Login es el inicio)
                popUpTo(Screens.Login.route) { inclusive = true }
            }
            viewModel.resetCreacionExitosa()
        }
    }

    // Muestra SnackBar si hay error
    LaunchedEffect(estado.errorMessage) {
        estado.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    // --- 2. DIÁLOGO DE SELECCIÓN Y LÓGICA DE PERMISOS ---

    if (estado.showSourceDialog) {
        ImageSourceDialog(
            onDismiss = { viewModel.setShowSourceDialog(false) },
            onGalleryClick = {
                // La Galería no requiere permisos runtime a partir de Android 13 para GetContent
                galleryLauncher.launch("image/*")
            },
            onCameraClick = {
                // 🚨 Lógica de verificación de permisos de cámara
                when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                    PackageManager.PERMISSION_GRANTED -> {
                        // Permiso concedido, lanzar la cámara directamente
                        val uri = createCameraTempUri(context)
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                    else -> {
                        // Solicitar permiso runtime
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Agregamos SnackBar Host
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
                .fillMaxSize(),
            estado = estado,
            viewModel = viewModel
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
    viewModel: ProductCreationViewModel
) {
    val context = LocalContext.current

    // Lógica de carga de Bitmap para Preview
    val previewBitmap: ImageBitmap? = remember(estado.imagenUri) {
        if (estado.imagenUri == null) {
            return@remember null
        }
        try {
            val inputStream = context.contentResolver.openInputStream(estado.imagenUri)
            inputStream?.use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        } catch (e: Exception) {
            // Nota: Aquí el error puede ser solo un problema de preview, la URI sigue siendo válida
            // Si el problema es la URI en sí, se manejaría mejor en la lógica de selección/copia.
            null
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

                // Campos Nombre, Descripción, Precio
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


                Spacer(modifier = Modifier.height(24.dp))

                // 💥 BOTÓN PARA SUBIR IMAGEN 💥
                Button(
                    onClick = {
                        if (!estado.isLoading) viewModel.setShowSourceDialog(true)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !estado.isLoading, // Deshabilitar si está cargando
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("SUBIR IMAGEN")
                }

                // 💡 PREVIEW DE IMAGEN
                if (previewBitmap != null) {
                    Spacer(modifier = Modifier.height(16.dp))
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
                    // Muestra el mensaje de error si la URI existe pero la carga falló
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("❌ Error al cargar preview de imagen.", color = Color.Red)
                }


                Spacer(modifier = Modifier.height(32.dp))

                // 4. Botón de Guardar (CREATE)
                Button(
                    onClick = { viewModel.onGuardarProductoClick() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !estado.isLoading // Deshabilitar si está cargando
                ) {
                    if (estado.isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("GUARDAR PRODUCTO")
                    }
                }
            }
        }
    }
}