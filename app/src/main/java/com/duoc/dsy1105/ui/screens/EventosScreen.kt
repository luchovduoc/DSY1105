package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.database.EventoEntity
import com.duoc.dsy1105.model.FiltrosEvento
import com.duoc.dsy1105.model.TiposEvento
import com.duoc.dsy1105.ui.components.BarraBusqueda
import com.duoc.dsy1105.ui.components.FilaFiltros
import com.duoc.dsy1105.ui.components.FiltroChip
import com.duoc.dsy1105.utils.DatosChile
import com.duoc.dsy1105.utils.Fechas
import com.duoc.dsy1105.viewmodel.EventoViewModel
import com.duoc.dsy1105.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val viewModel: EventoViewModel = viewModel()
    val eventos by viewModel.eventos.collectAsState()
    val organizaciones by viewModel.organizaciones.collectAsState()
    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil
    val filtros by viewModel.filtros.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Eventos") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
                BarraBusqueda(
                    texto = filtros.texto,
                    onTextoChange = viewModel::onFiltroTextoChange
                )
                FilaFiltros {
                    FiltroChip("Fecha", filtros.fecha, FiltrosEvento.opcionesFecha, viewModel::onFiltroFechaChange)
                    FiltroChip("Tipo", filtros.tipo, TiposEvento.todos, viewModel::onFiltroTipoChange)
                    FiltroChip("Región", filtros.region, DatosChile.regiones, viewModel::onFiltroRegionChange)
                }
            }
        }, // fin topBar
        floatingActionButton = {
            // Solo el ejecutivo registra eventos
            if (perfil?.puedeCrearEventos == true) FloatingActionButton(onClick = {
                navController.navigate("evento_form")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        } // fin floatingActionButton
    ) { innerPadding ->

        if (eventos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (filtros.hayFiltros) "Ningún evento coincide con los filtros" else "No hay eventos registrados",
                    style = MaterialTheme.typography.bodyMedium
                )
            } // fin Box
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(eventos, key = { it.id }) { evento ->
                    EventoCard(
                        evento = evento,
                        organizacion = organizaciones.find { it.id == evento.organizacionId }?.nombre,
                        onClick = { navController.navigate("evento_detalle/${evento.id}") }
                    )
                } // fin items
            } // fin LazyColumn
        }

    } // fin Scaffold

} // fin EventosScreen

@Composable
fun EventoCard(
    evento: EventoEntity,
    organizacion: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = evento.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = evento.tipo,
                style = MaterialTheme.typography.bodySmall
            )
            if (organizacion != null) {
                Text(
                    text = organizacion,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = rangoDeFechas(evento),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = etiquetaTiempo(evento),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "${evento.comuna}, ${evento.region}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} // fin EventoCard

// "15/10/2026" o "15/10/2026 - 16/10/2026"
fun rangoDeFechas(evento: EventoEntity): String =
    if (evento.fechaTermino.isBlank() || evento.fechaTermino == evento.fechaInicio) {
        evento.fechaInicio
    } else {
        "${evento.fechaInicio} - ${evento.fechaTermino}"
    }

// Cuánto falta para el evento (o si ya terminó)
fun etiquetaTiempo(evento: EventoEntity): String {
    val dias = Fechas.diasDesdeHoy(evento.fechaInicio) ?: return ""
    val diasFin = Fechas.diasDesdeHoy(evento.fechaTermino.ifBlank { evento.fechaInicio }) ?: dias
    return when {
        diasFin < 0 -> "Finalizado"
        dias > 1 -> "En $dias días"
        dias == 1 -> "Mañana"
        dias == 0 -> "Hoy"
        else -> "En curso"
    }
} // fin etiquetaTiempo
