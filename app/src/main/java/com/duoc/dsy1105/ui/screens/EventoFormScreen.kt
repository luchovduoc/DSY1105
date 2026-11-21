package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.model.TiposEvento
import com.duoc.dsy1105.ui.components.CampoFecha
import com.duoc.dsy1105.ui.components.CampoTexto
import com.duoc.dsy1105.ui.components.SelectorDesplegable
import com.duoc.dsy1105.ui.components.SelectorEntidad
import com.duoc.dsy1105.ui.components.SelectorRegionComuna
import com.duoc.dsy1105.viewmodel.EventoViewModel
import com.duoc.dsy1105.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoFormScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    eventoId: Long? = null
) {
    val viewModel: EventoViewModel = viewModel()
    val estado by viewModel.estado.collectAsState()
    val organizaciones by viewModel.organizaciones.collectAsState()
    val esEdicion = eventoId != null

    // Permiso segun el perfil (el repositorio lo vuelve a validar al guardar)
    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil
    val puedeGuardar = perfil != null && (
        if (esEdicion) perfil.puedeEditarEventos else perfil.puedeCrearEventos
        )

    // Si es edicion, carga los datos una sola vez
    LaunchedEffect(eventoId) {
        if (eventoId != null) {
            viewModel.cargarEvento(eventoId)
        }
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
                title = { Text(if (esEdicion) "Editar Evento" else "Nuevo Evento") },
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

            CampoTexto(
                etiqueta = "Nombre del evento",
                valor = estado.nombre,
                onValorChange = { viewModel.onNombreChange(it) },
                error = estado.errores.nombre
            )

            SelectorDesplegable(
                etiqueta = "Tipo de evento",
                valor = estado.tipo,
                opciones = TiposEvento.todos,
                onSeleccion = { viewModel.onTipoChange(it) },
                error = estado.errores.tipo
            )

            CampoTexto(
                etiqueta = "Descripción",
                valor = estado.descripcion,
                onValorChange = { viewModel.onDescripcionChange(it) },
                minLines = 2
            )

            CampoFecha(
                etiqueta = "Fecha de inicio",
                valor = estado.fechaInicio,
                onFechaSeleccionada = { viewModel.onFechaInicioChange(it) },
                error = estado.errores.fechaInicio
            )

            CampoFecha(
                etiqueta = "Fecha de término (opcional)",
                valor = estado.fechaTermino,
                onFechaSeleccionada = { viewModel.onFechaTerminoChange(it) },
                error = estado.errores.fechaTermino
            )

            CampoTexto(
                etiqueta = "Horario (ej: 09:00 a 18:00)",
                valor = estado.horario,
                onValorChange = { viewModel.onHorarioChange(it) }
            )

            SelectorRegionComuna(
                region = estado.region,
                comuna = estado.comuna,
                onRegionChange = { viewModel.onRegionChange(it) },
                onComunaChange = { viewModel.onComunaChange(it) },
                errorRegion = estado.errores.region,
                errorComuna = estado.errores.comuna
            )

            SelectorEntidad(
                etiqueta = "Organización responsable",
                seleccionado = organizaciones.find { it.id == estado.organizacionId },
                opciones = organizaciones,
                textoDe = { it.nombre },
                onSeleccion = { viewModel.onOrganizacionChange(it?.id ?: 0L) },
                textoVacio = "Sin organización",
                permiteVacio = true
            )

            CampoTexto(
                etiqueta = "Público esperado",
                valor = estado.publicoEsperado,
                onValorChange = { viewModel.onPublicoEsperadoChange(it) }
            )

            CampoTexto(
                etiqueta = "Cantidad estimada de asistentes",
                valor = estado.cantidadEstimada,
                onValorChange = { viewModel.onCantidadChange(it) },
                error = estado.errores.cantidad,
                teclado = KeyboardType.Number
            )

            CampoTexto(
                etiqueta = "Condiciones de participación",
                valor = estado.condiciones,
                onValorChange = { viewModel.onCondicionesChange(it) },
                minLines = 2
            )

            CampoTexto(
                etiqueta = "Observaciones",
                valor = estado.observaciones,
                onValorChange = { viewModel.onObservacionesChange(it) },
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                    text = "Solo lectura: tu perfil no puede modificar eventos",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    sesion?.let { usuario ->
                        if (eventoId != null) viewModel.actualizar(eventoId, usuario)
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

} // fin EventoFormScreen
