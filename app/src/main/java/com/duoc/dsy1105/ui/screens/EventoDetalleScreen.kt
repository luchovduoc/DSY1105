package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.viewmodel.EventoViewModel
import com.duoc.dsy1105.viewmodel.LoginViewModel

// Detalle de solo lectura: lo ven todos los perfiles (Gestión de Activaciones revisa fechas y condiciones).
// Editar y eliminar aparecen solo para quien tiene permiso.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetalleScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    eventoId: Long
) {
    val viewModel: EventoViewModel = viewModel()
    val eventos by viewModel.eventos.collectAsState()
    val organizaciones by viewModel.organizaciones.collectAsState()
    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil
    val usuarioId = sesion?.id?.toLong() ?: 0L
    val estado by viewModel.estado.collectAsState()
    val evento = eventos.find { it.id == eventoId }

    var mostrarDialogo by remember { mutableStateOf(false) }

    if (mostrarDialogo && evento != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Eliminar evento") },
            text = { Text("¿Seguro que quieres eliminar \"${evento.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    // Solo vuelve a la lista si se elimino; si no, se explica por que
                    sesion?.let { viewModel.eliminar(it, evento) { navController.popBackStack() } }
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        ) // fin AlertDialog
    } // fin if mostrarDialogo

    // Si no se pudo eliminar (sin permiso o evento en uso) se explica por que
    estado.error?.let { mensaje ->
        AlertDialog(
            onDismissRequest = { viewModel.limpiarError() },
            title = { Text("No se pudo eliminar") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = { viewModel.limpiarError() }) {
                    Text("Entendido")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (evento != null &&
                        perfil?.puedeEliminarRegistro(evento.creadoPorId, usuarioId) == true
                    ) {
                        IconButton(onClick = { mostrarDialogo = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            )
        } // fin topBar
    ) { innerPadding ->

        if (evento == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Evento no encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = evento.nombre,
                    style = MaterialTheme.typography.headlineSmall
                )

                InfoRow("Tipo", evento.tipo)
                InfoRow("Fecha", rangoDeFechas(evento))
                if (etiquetaTiempo(evento).isNotEmpty()) {
                    InfoRow("Estado", etiquetaTiempo(evento))
                }
                if (evento.horario.isNotBlank()) InfoRow("Horario", evento.horario)
                InfoRow("Ubicación", "${evento.comuna}, ${evento.region}")
                InfoRow(
                    "Organización",
                    organizaciones.find { it.id == evento.organizacionId }?.nombre
                        ?: "Sin organización"
                )
                if (evento.publicoEsperado.isNotBlank()) {
                    InfoRow("Público esperado", evento.publicoEsperado)
                }
                if (evento.cantidadEstimada > 0) {
                    InfoRow("Asistentes estimados", evento.cantidadEstimada.toString())
                }
                if (evento.descripcion.isNotBlank()) InfoRow("Descripción", evento.descripcion)
                if (evento.condiciones.isNotBlank()) {
                    InfoRow("Condiciones de participación", evento.condiciones)
                }
                if (evento.observaciones.isNotBlank()) {
                    InfoRow("Observaciones", evento.observaciones)
                }

                if (perfil?.puedeEditarEventos == true) {
                    Button(
                        onClick = { navController.navigate("evento_form?id=${evento.id}") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Editar evento")
                    }
                }
            } // fin Column
        }

    } // fin Scaffold

} // fin EventoDetalleScreen
