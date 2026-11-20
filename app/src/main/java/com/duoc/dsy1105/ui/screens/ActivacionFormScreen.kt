package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.ui.components.CampoFecha
import com.duoc.dsy1105.ui.components.CampoTexto
import com.duoc.dsy1105.ui.components.SelectorEntidad
import com.duoc.dsy1105.viewmodel.ActivacionViewModel
import com.duoc.dsy1105.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivacionFormScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    activacionId: Long? = null
) {
    val viewModel: ActivacionViewModel = viewModel()
    val estado by viewModel.estado.collectAsState()
    val oportunidades by viewModel.oportunidades.collectAsState()
    val responsables by viewModel.responsables.collectAsState()
    val esEdicion = activacionId != null

    // Permiso según el perfil (el repositorio lo vuelve a validar al guardar)
    val sesion by loginViewModel.usuario.collectAsState()
    val puedeGuardar = sesion?.perfil?.puedeGestionarActivaciones == true

    LaunchedEffect(activacionId) {
        if (activacionId != null) viewModel.cargarActivacion(activacionId)
    }

    LaunchedEffect(estado.guardadoExitosamente) {
        if (estado.guardadoExitosamente) {
            viewModel.limpiarFormulario()
            navController.popBackStack()
        }
    } // fin LaunchedEffect

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Activación" else "Nueva Activación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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

            SelectorEntidad(
                etiqueta = "Oportunidad confirmada",
                seleccionado = oportunidades.find { it.id == estado.oportunidadId },
                opciones = oportunidades,
                textoDe = { it.nombre },
                onSeleccion = { viewModel.onOportunidadChange(it?.id ?: 0L) },
                error = estado.errores.oportunidad
            )

            SelectorEntidad(
                etiqueta = "Responsable de la activación",
                seleccionado = responsables.find { it.id == estado.responsableId },
                opciones = responsables,
                textoDe = { it.nombre },
                onSeleccion = { viewModel.onResponsableChange(it?.id ?: 0L) },
                error = estado.errores.responsable
            )

            CampoFecha(
                etiqueta = "Fecha de la activación",
                valor = estado.fecha,
                onFechaSeleccionada = { viewModel.onFechaChange(it) },
                error = estado.errores.fecha
            )

            CampoTexto(
                etiqueta = "Lugar",
                valor = estado.lugar,
                onValorChange = { viewModel.onLugarChange(it) },
                error = estado.errores.lugar
            )

            CampoTexto(
                etiqueta = "Observaciones",
                valor = estado.observaciones,
                onValorChange = { viewModel.onObservacionesChange(it) },
                minLines = 2
            )

            // Error general (por ejemplo, falta de permiso al guardar)
            estado.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!puedeGuardar) {
                Text(
                    text = "Solo lectura: tu perfil no puede modificar activaciones",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    sesion?.let { usuario ->
                        if (activacionId != null) viewModel.actualizar(activacionId, usuario)
                        else viewModel.guardar(usuario)
                    }
                },
                enabled = puedeGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esEdicion) "Actualizar" else "Guardar")
            } // fin Button

        } // fin Column

    } // fin Scaffold

} // fin ActivacionFormScreen
