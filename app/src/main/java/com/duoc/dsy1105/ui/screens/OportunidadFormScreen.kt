package com.duoc.dsy1105.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.material.icons.filled.ErrorOutline
import coil.compose.AsyncImage
import com.duoc.dsy1105.model.EstadosOportunidad
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.model.TiposEvento
import com.duoc.dsy1105.ui.components.SelectorDesplegable
import com.duoc.dsy1105.ui.components.SelectorEntidad
import com.duoc.dsy1105.viewmodel.LoginViewModel
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
    loginViewModel: LoginViewModel,
    oportunidadId: Long? = null
) {

    val viewModel: OportunidadViewModel = viewModel()
    val estado by viewModel.estado.collectAsState()
    val esEdicion = oportunidadId != null

    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil
    val usuarioId = sesion?.id?.toLong() ?: 0L
    val usuarios by viewModel.usuarios.collectAsState()
    val eventos by viewModel.eventos.collectAsState()
    val organizaciones by viewModel.organizaciones.collectAsState()
    val contactos by viewModel.contactos.collectAsState()

    // Permisos segun el perfil (el repositorio los vuelve a validar al guardar)
    val puedeEditarDatos = perfil != null && (
        if (esEdicion) perfil.puedeEditarOportunidad(estado.responsableId, usuarioId)
        else perfil.puedeCrearOportunidades
        )
    val puedeEditarObservaciones = perfil != null && (
        if (esEdicion) perfil.puedeEditarObservaciones(estado.estado, estado.responsableId, usuarioId)
        else perfil.puedeCrearOportunidades
        )
    // EN PAUSA (perfil Marketing): val puedeEditarMateriales = esEdicion && perfil?.puedeEditarMateriales == true
    // El estado lo gestiona el supervisor (el ejecutivo pasa a En gestion al registrar su primera gestion)
    val puedeCambiarEstado = perfil?.puedeCambiarEstado == true
    val puedeAsignarResponsable = esEdicion && perfil?.puedeAsignarResponsable == true
    val puedeGuardar = puedeEditarDatos || puedeEditarObservaciones
    // En edicion, los datos se cargan de forma asincrona: antes no se sabe si hay permiso
    val datosCargados = !esEdicion || estado.nombre.isNotEmpty()
    var responsableExpandido by remember { mutableStateOf(false) }
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

    val opcionesEstado = EstadosOportunidad.todos

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
            viewModel.onFotoError(null)
            fotoUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
        } else {
            viewModel.onFotoError("Se necesita permiso de cámara para tomar la foto")
        }
    }// fin Launcher para solicitar permiso de cámara

    // Obtener ubicacion actual del dispositivo.
    // Los avisos se guardan en el estado (errores.ubicacion) y se muestran bajo el boton.
    fun obtenerUbicacionActual() {

        viewModel.onUbicacionError(null)

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
            viewModel.onUbicacionError("No se cuenta con permiso de ubicación")
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
            viewModel.onUbicacionError("Activa la ubicación del dispositivo")
            return
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
                    viewModel.onUbicacionChange(
                        ultimaUbicacion.latitude,
                        ultimaUbicacion.longitude
                    )
                } else {
                    viewModel.onUbicacionError(
                        "No fue posible obtener la ubicación. Intenta nuevamente en unos segundos."
                    )
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
                        viewModel.onUbicacionChange(
                            ubicacion.latitude,
                            ubicacion.longitude
                        )
                    } else {
                        intentarProveedor(indice + 1)
                    }
                }

            } catch (e: SecurityException) {
                viewModel.onUbicacionError("No se cuenta con permiso de ubicación")
            }
        }

        intentarProveedor(0)
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
            viewModel.onUbicacionError(
                "Se necesita permiso de ubicación para registrar la posición"
            )
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
                    sesion?.let { usuario ->
                        if (esEdicion) {
                            viewModel.actualizar(oportunidadId!!, usuario) {}
                        } else {
                            viewModel.guardar(usuario) {}
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                enabled = !estado.guardando && puedeGuardar
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

            // Avisos segun el perfil
            if (datosCargados && !puedeGuardar) {
                Text(
                    text = "Solo lectura: tu perfil no puede modificar esta oportunidad",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            // EN PAUSA (perfil Marketing): aviso "Como Marketing puedes registrar materiales y observaciones"

            // Error general (por ejemplo, falta de permiso al guardar)
            estado.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Nombre
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre de la oportunidad") },
                enabled = puedeEditarDatos,
                isError = estado.errores.nombre != null,
                supportingText = { Text(estado.errores.nombre ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            // Tipo (lista de tipos del caso; permite filtrar por tipo)
            SelectorDesplegable(
                etiqueta = "Tipo de oportunidad",
                valor = estado.tipoOportunidad,
                opciones = TiposEvento.todos,
                onSeleccion = { viewModel.onTipoChange(it) },
                error = estado.errores.tipo,
                enabled = puedeEditarDatos
            )

            // Descripcion
            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                label = { Text("Descripción") },
                enabled = puedeEditarDatos,
                isError = estado.errores.descripcion != null,

                trailingIcon = {
                    if (estado.errores.descripcion != null) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = "Error"
                        )
                    }
                },
                supportingText = { Text(estado.errores.descripcion ?: "") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Fuente
            OutlinedTextField(
                value = estado.fuente,
                onValueChange = { viewModel.onFuenteChange(it) },
                label = { Text("Fuente de información") },
                enabled = puedeEditarDatos,
                isError = estado.errores.fuente != null,
                trailingIcon = {
                    if (estado.errores.fuente != null) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = "Error"
                        )
                    }
                },
                supportingText = { Text(estado.errores.fuente ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            // Boton escanear QR para fuente
            OutlinedButton(
                onClick = {
                    navController.navigate("qr_scanner?destino=fuente")
                },
                enabled = puedeEditarDatos,
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

            // Evento asociado (opcional)
            SelectorEntidad(
                etiqueta = "Evento asociado",
                seleccionado = eventos.find { it.id == estado.eventoId },
                opciones = eventos,
                textoDe = { it.nombre },
                onSeleccion = { viewModel.onEventoChange(it?.id ?: 0L) },
                enabled = puedeEditarDatos,
                textoVacio = "Sin evento",
                permiteVacio = true
            )

            // Organización y contacto asociados (opcionales)
            SelectorEntidad(
                etiqueta = "Organización",
                seleccionado = organizaciones.find { it.id == estado.organizacionId },
                opciones = organizaciones,
                textoDe = { it.nombre },
                onSeleccion = { viewModel.onOrganizacionChange(it?.id ?: 0L) },
                enabled = puedeEditarDatos,
                textoVacio = "Sin organización",
                permiteVacio = true
            )

            // Solo se ofrecen los contactos de la organización elegida
            SelectorEntidad(
                etiqueta = "Contacto",
                seleccionado = contactos.find { it.id == estado.contactoId },
                opciones = contactos.filter { it.organizacionId == estado.organizacionId },
                textoDe = { it.nombre },
                onSeleccion = { viewModel.onContactoChange(it?.id ?: 0L) },
                enabled = puedeEditarDatos && estado.organizacionId != 0L,
                textoVacio = "Sin contacto",
                permiteVacio = true
            )

            // Prioridad
            ExposedDropdownMenuBox(
                expanded = prioridadExpandida,
                onExpandedChange = {
                    if (puedeEditarDatos) prioridadExpandida = !prioridadExpandida
                }
            ) {
                OutlinedTextField(
                    value = estado.nivelPrioridad,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Prioridad") },
                    enabled = puedeEditarDatos,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = prioridadExpandida
                        )
                    },
                    isError = estado.errores.prioridad != null,
                    supportingText = {
                        Text(estado.errores.prioridad ?: "")
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
                    if (puedeEditarDatos) potencialExpandido = !potencialExpandido
                }
            ) {
                OutlinedTextField(
                    value = estado.potencialComercial,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Potencial comercial") },
                    enabled = puedeEditarDatos,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = potencialExpandido
                        )
                    },
                    isError = estado.errores.potencial != null,
                    supportingText = {
                        Text(estado.errores.potencial ?: "")
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
                    if (puedeCambiarEstado) estadoExpandido = !estadoExpandido
                }
            ) {
                OutlinedTextField(
                    value = estado.estado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado de gestión") },
                    enabled = puedeCambiarEstado,
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
                enabled = puedeEditarObservaciones,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // EN PAUSA (perfil Marketing): campo de materiales
            // OutlinedTextField(
            //     value = estado.materiales,
            //     onValueChange = { viewModel.onMaterialesChange(it) },
            //     label = { Text("Materiales (Marketing)") },
            //     enabled = puedeEditarMateriales,
            //     modifier = Modifier.fillMaxWidth(),
            //     minLines = 2
            // )

            // Responsable: lo asigna el supervisor; los demas perfiles solo lo ven
            val ejecutivosActivos = usuarios.filter {
                it.activo && Perfil.desdeEtiqueta(it.perfil) == Perfil.EJECUTIVO
            }
            val nombreResponsable =
                usuarios.find { it.id == estado.responsableId }?.nombre ?: "Sin asignar"

            ExposedDropdownMenuBox(
                expanded = responsableExpandido && puedeAsignarResponsable,
                onExpandedChange = {
                    if (puedeAsignarResponsable) responsableExpandido = !responsableExpandido
                }
            ) {
                OutlinedTextField(
                    value = nombreResponsable,
                    onValueChange = {},
                    readOnly = true,
                    enabled = puedeAsignarResponsable,
                    label = { Text("Responsable") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = responsableExpandido
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = responsableExpandido && puedeAsignarResponsable,
                    onDismissRequest = { responsableExpandido = false }
                ) {
                    ejecutivosActivos.forEach { ejecutivo ->
                        DropdownMenuItem(
                            text = { Text(ejecutivo.nombre) },
                            onClick = {
                                viewModel.onResponsableChange(ejecutivo.id)
                                responsableExpandido = false
                            }
                        )
                    }
                }
            } // fin Responsable

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
                enabled = puedeEditarDatos,
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

            // Aviso de ubicación (permiso denegado, ubicación apagada, sin señal)
            estado.errores.ubicacion?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
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
                enabled = puedeEditarDatos,
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

            // Aviso de cámara (permiso denegado)
            estado.errores.foto?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

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