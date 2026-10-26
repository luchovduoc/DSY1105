package com.duoc.dsy1105.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.MyLocation
import coil.compose.AsyncImage
import com.duoc.dsy1105.viewmodel.OportunidadViewModel
import java.io.File
import android.content.Context
import android.location.LocationManager
import android.location.Location
import android.os.CancellationSignal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadFormScreen(
    navController: NavController,
    oportunidadId: Long? = null
) {

    val viewModel: OportunidadViewModel = viewModel()
    val estado by viewModel.estado.collectAsState()
    val esEdicion = oportunidadId != null
    val context = LocalContext.current

    // URI temporal para la foto
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    var prioridadExpandida by remember { mutableStateOf(false) }

    val opcionesPrioridad = listOf(
        "Alta",
        "Media",
        "Baja"
    )
    var potencialExpandido by remember { mutableStateOf(false) }
    var estadoExpandido by remember { mutableStateOf(false) }

    val opcionesPotencial = listOf(
        "Alto",
        "Medio",
        "Bajo"
    )

    val opcionesEstado = listOf(
        "Evento identificado",
        "Contacto pendiente",
        "Contacto realizado",
        "Propuesta enviada",
        "En espera de respuesta",
        "Participación confirmada",
        "Oportunidad descartada"
    )

    // Recibir resultado del QR
    val qrResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("qr_result_fuente", null)
        ?.collectAsState()

    // Aplicar resultado QR al campo fuente
    LaunchedEffect(qrResult?.value) {
        qrResult?.value?.let { contenido ->
            viewModel.onFuenteChange(contenido)
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<String>("qr_result_fuente")
        }
    } // fin LaunchedEffect QR

    // Launcher para abrir la camara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && fotoUri != null) {
            viewModel.onFotoUriChange(fotoUri.toString())
        }
    } // fin cameraLauncher

    // Launcher para solicitar permiso de cámara
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            fotoUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
        } else {
            Toast.makeText(
                context,
                "Se necesita permiso de cámara para tomar la foto",
                Toast.LENGTH_SHORT
            ).show()
        }
    }// fin Launcher para solicitar permiso de cámara

    // Obtener ubicacion actual del dispositivo
    fun obtenerUbicacionActual() {

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val tieneFine =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val tieneCoarse =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!tieneFine && !tieneCoarse) {
            Toast.makeText(
                context,
                "No se cuenta con permiso de ubicación",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val proveedores = listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER
        ).filter { proveedor ->
            try {
                locationManager.isProviderEnabled(proveedor)
            } catch (e: Exception) {
                false
            }
        }

        if (proveedores.isEmpty()) {
            Toast.makeText(
                context,
                "Activa la ubicación del dispositivo",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        fun registrarUbicacion(ubicacion: Location) {

            viewModel.onUbicacionChange(
                ubicacion.latitude,
                ubicacion.longitude
            )

            Toast.makeText(
                context,
                "Ubicación registrada correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }

        fun intentarProveedor(indice: Int) {

            if (indice >= proveedores.size) {

                val ultimaUbicacion =
                    proveedores.mapNotNull { proveedor ->
                        try {
                            locationManager.getLastKnownLocation(proveedor)
                        } catch (e: SecurityException) {
                            null
                        }
                    }.maxByOrNull { it.time }

                if (ultimaUbicacion != null) {

                    registrarUbicacion(ultimaUbicacion)

                } else {

                    Toast.makeText(
                        context,
                        "No fue posible obtener la ubicación. Intenta nuevamente en unos segundos.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                return
            }

            try {

                LocationManagerCompat.getCurrentLocation(
                    locationManager,
                    proveedores[indice],
                    null as CancellationSignal?,
                    ContextCompat.getMainExecutor(context)
                ) { ubicacion: Location? ->

                    if (ubicacion != null) {

                        registrarUbicacion(ubicacion)

                    } else {

                        intentarProveedor(indice + 1)
                    }
                }

            } catch (e: SecurityException) {

                Toast.makeText(
                    context,
                    "No se cuenta con permiso de ubicación",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        intentarProveedor(0)


        val proveedor = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER

            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER

            else -> {
                Toast.makeText(
                    context,
                    "Activa la ubicación del dispositivo",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        try {
            LocationManagerCompat.getCurrentLocation(
                locationManager,
                proveedor,
                null as CancellationSignal?,
                ContextCompat.getMainExecutor(context)
            ) { ubicacion: Location? ->

                if (ubicacion != null) {

                    viewModel.onUbicacionChange(
                        ubicacion.latitude,
                        ubicacion.longitude
                    )

                    Toast.makeText(
                        context,
                        "Ubicación registrada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        context,
                        "No fue posible obtener la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } catch (e: SecurityException) {

            Toast.makeText(
                context,
                "No se cuenta con permiso de ubicación",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Launcher para solicitar permisos de ubicacion
    val permisoUbicacionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->

        val concedido =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (concedido) {

            obtenerUbicacionActual()

        } else {

            Toast.makeText(
                context,
                "Se necesita permiso de ubicación para registrar la posición",
                Toast.LENGTH_SHORT
            ).show()
        }
    }



    // Si es edicion carga los datos
    LaunchedEffect(oportunidadId) {
        if (oportunidadId != null) {
            viewModel.cargarOportunidad(oportunidadId)
        }
    } // fin LaunchedEffect carga

    // Navega de vuelta cuando se guarda exitosamente
    LaunchedEffect(estado.guardadoExitosamente) {
        if (estado.guardadoExitosamente) {
            viewModel.limpiarFormulario()
            navController.popBackStack()
        }
    } // fin LaunchedEffect guardado

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Oportunidad" else "Nueva Oportunidad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }, // fin topBar
        bottomBar = {
            Button(
                onClick = {
                    if (esEdicion) {
                        viewModel.actualizar(oportunidadId!!) {}
                    } else {
                        viewModel.guardar {}
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = !estado.guardando
            ) {
                if (estado.guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (esEdicion) "Actualizar" else "Guardar")
                }
            } // fin Button
        } // fin bottomBar
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Nombre
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre de la oportunidad") },
                isError = estado.nombreError != null,
                supportingText = { Text(estado.nombreError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            // Tipo
            OutlinedTextField(
                value = estado.tipoOportunidad,
                onValueChange = { viewModel.onTipoChange(it) },
                label = { Text("Tipo de oportunidad") },
                isError = estado.tipoError != null,
                supportingText = { Text(estado.tipoError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            // Descripcion
            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                label = { Text("Descripción") },
                isError = estado.descripcionError != null,
                supportingText = { Text(estado.descripcionError ?: "") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Fuente
            OutlinedTextField(
                value = estado.fuente,
                onValueChange = { viewModel.onFuenteChange(it) },
                label = { Text("Fuente de información") },
                isError = estado.fuenteError != null,
                supportingText = { Text(estado.fuenteError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            // Boton escanear QR para fuente
            OutlinedButton(
                onClick = {
                    navController.navigate("qr_scanner?destino=fuente")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Filled.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Escanear QR como fuente")
            } // fin OutlinedButton QR

            // Prioridad
            ExposedDropdownMenuBox(
                expanded = prioridadExpandida,
                onExpandedChange = {
                    prioridadExpandida = !prioridadExpandida
                }
            ) {
                OutlinedTextField(
                    value = estado.nivelPrioridad,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Prioridad") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = prioridadExpandida
                        )
                    },
                    isError = estado.prioridadError != null,
                    supportingText = {
                        Text(estado.prioridadError ?: "")
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = prioridadExpandida,
                    onDismissRequest = {
                        prioridadExpandida = false
                    }
                ) {
                    opcionesPrioridad.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                viewModel.onPrioridadChange(opcion)
                                prioridadExpandida = false
                            }
                        )
                    }
                }
            }

            // Potencial comercial
            ExposedDropdownMenuBox(
                expanded = potencialExpandido,
                onExpandedChange = {
                    potencialExpandido = !potencialExpandido
                }
            ) {
                OutlinedTextField(
                    value = estado.potencialComercial,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Potencial comercial") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = potencialExpandido
                        )
                    },
                    isError = estado.potencialError != null,
                    supportingText = {
                        Text(estado.potencialError ?: "")
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = potencialExpandido,
                    onDismissRequest = {
                        potencialExpandido = false
                    }
                ) {
                    opcionesPotencial.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                viewModel.onPotencialChange(opcion)
                                potencialExpandido = false
                            }
                        )
                    }
                }
            }

            // Estado de gestión
            ExposedDropdownMenuBox(
                expanded = estadoExpandido,
                onExpandedChange = {
                    estadoExpandido = !estadoExpandido
                }
            ) {
                OutlinedTextField(
                    value = estado.estado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado de gestión") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = estadoExpandido
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = estadoExpandido,
                    onDismissRequest = {
                        estadoExpandido = false
                    }
                ) {
                    opcionesEstado.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                viewModel.onEstadoChange(opcion)
                                estadoExpandido = false
                            }
                        )
                    }
                }
            }

            // Observaciones
            OutlinedTextField(
                value = estado.observaciones,
                onValueChange = { viewModel.onObservacionesChange(it) },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Boton ubicacion
            OutlinedButton(
                onClick = {

                    val tieneFine =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                    val tieneCoarse =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                    if (tieneFine || tieneCoarse) {

                        obtenerUbicacionActual()

                    } else {

                        permisoUbicacionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Filled.MyLocation,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    if (estado.latitud != null && estado.longitud != null)
                        "Actualizar ubicación"
                    else
                        "Obtener ubicación"
                )
            }

            if (estado.latitud != null && estado.longitud != null) {

                Text(
                    text = "Ubicación registrada",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = "Latitud: ${estado.latitud}"
                )

                Text(
                    text = "Longitud: ${estado.longitud}"
                )
            }

            // Boton camara
            OutlinedButton(
                onClick = {
                    val archivo = File(
                        context.getExternalFilesDir("Pictures"),
                        "foto_${System.currentTimeMillis()}.jpg"
                    )
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        archivo
                    )
                    fotoUri = uri

                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraLauncher.launch(uri)
                    } else {
                        permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tomar foto")
            } // fin OutlinedButton camara

            // Mostrar foto con animación si existe
            AnimatedVisibility(
                visible = estado.fotoUri.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                AsyncImage(
                    model = estado.fotoUri,
                    contentDescription = "Foto de la oportunidad",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }// fin Mostrar foto con animación si existe

        } // fin Column

    } // fin Scaffold

} // fin OportunidadFormScreen