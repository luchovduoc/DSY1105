package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duoc.dsy1105.model.Evento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(navController: NavController) {

    val eventos = listOf(
        Evento(
            id = 1,
            nombre = "Feria Laboral USACH 2026",
            tipo = "Feria empresarial",
            descripcion = "Feria anual de empleabilidad",
            fechaInicio = "2026-10-15",
            fechaTermino = "2026-10-16",
            horario = "09:00 - 18:00",
            direccion = "Av. Ecuador 3769",
            comuna = "Estación Central",
            region = "Metropolitana",
            organizacion = null,
            publicoEsperado = "Estudiantes y profesionales",
            cantidadEstimadaAsistentes = 5000,
            condicionesParticipacion = "Stand disponible",
            observaciones = ""
        )
    )

    Scaffold(
        topBar = {
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* agregar evento */ }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(eventos) { evento ->
                EventoCard(evento = evento)
            }
        }
    }
}

@Composable
fun EventoCard(evento: Evento) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = evento.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = evento.tipo,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = evento.fechaInicio,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = evento.comuna,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

