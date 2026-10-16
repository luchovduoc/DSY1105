package com.duoc.dsy1105.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.duoc.dsy1105.viewmodel.OportunidadViewModel
import java.io.File

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

    // Launcher para abrir la camara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && fotoUri != null) {
            viewModel.onFotoUriChange(fotoUri.toString())
        }
    } // fin cameraLauncher

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
        } // fin topBar
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

            // Prioridad
            OutlinedTextField(
                value = estado.nivelPrioridad,
                onValueChange = { viewModel.onPrioridadChange(it) },
                label = { Text("Prioridad (Alta / Media / Baja)") },
                isError = estado.prioridadError != null,
                supportingText = { Text(estado.prioridadError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            // Potencial comercial
            OutlinedTextField(
                value = estado.potencialComercial,
                onValueChange = { viewModel.onPotencialChange(it) },
                label = { Text("Potencial comercial (Alto / Medio / Bajo)") },
                isError = estado.potencialError != null,
                supportingText = { Text(estado.potencialError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            // Observaciones
            OutlinedTextField(
                value = estado.observaciones,
                onValueChange = { viewModel.onObservacionesChange(it) },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                    cameraLauncher.launch(uri)
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

            // Mostrar foto si existe
            if (estado.fotoUri.isNotEmpty()) {
                AsyncImage(
                    model = estado.fotoUri,
                    contentDescription = "Foto de la oportunidad",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } // fin if foto

            Spacer(modifier = Modifier.height(8.dp))

            // Boton guardar
            Button(
                onClick = {
                    if (esEdicion) {
                        viewModel.actualizar(oportunidadId!!) {}
                    } else {
                        viewModel.guardar {}
                    }
                },
                modifier = Modifier.fillMaxWidth(),
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
            } // fin Button guardar

        } // fin Column

    } // fin Scaffold

} // fin OportunidadFormScreen