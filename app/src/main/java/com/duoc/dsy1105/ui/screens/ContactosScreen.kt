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
import com.duoc.dsy1105.model.Contacto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactosScreen(navController: NavController) {

    val contactos = listOf(
        Contacto(
            id = 1,
            nombre = "Juan Pérez",
            organizacion = null,
            cargo = "Jefe de RRHH",
            area = "Recursos Humanos",
            email = "juan.perez@ficticio.cl",
            telefono = "+56912345678",
            medioContactoPreferente = "Email",
            estado = "Activo",
            fechaUltimoContacto = "2026-09-01",
            observaciones = ""
        ),
        Contacto(
            id = 2,
            nombre = "María González",
            organizacion = null,
            cargo = "Coordinadora de Eventos",
            area = "Eventos",
            email = "maria.gonzalez@ficticio.cl",
            telefono = "+56987654321",
            medioContactoPreferente = "Teléfono",
            estado = "Activo",
            fechaUltimoContacto = "2026-09-05",
            observaciones = ""
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contactos") },
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
            FloatingActionButton(onClick = { /* agregar contacto */ }) {
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
            items(contactos) { contacto ->
                ContactoCard(contacto = contacto)
            }
        }
    }
}

@Composable
fun ContactoCard(contacto: Contacto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = contacto.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${contacto.cargo} - ${contacto.area}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = contacto.email,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = contacto.estado,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

