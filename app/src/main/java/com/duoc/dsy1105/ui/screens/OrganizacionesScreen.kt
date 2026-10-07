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
import com.duoc.dsy1105.model.Organizacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionesScreen(navController: NavController) {

    val organizaciones = listOf(
        Organizacion(
            id = 1,
            nombre = "Municipalidad de Santiago",
            tipoOrganizacion = "Municipalidad",
            rubro = "Sector público",
            comuna = "Santiago",
            region = "Metropolitana",
            sitioWeb = "www.municipalidaddesantiago.cl",
            publicoAsociado = "Funcionarios y vecinos",
            observaciones = ""
        ),
        Organizacion(
            id = 2,
            nombre = "Empresa Ficticia S.A.",
            tipoOrganizacion = "Empresa",
            rubro = "Tecnología",
            comuna = "Providencia",
            region = "Metropolitana",
            sitioWeb = "www.empresaficticia.cl",
            publicoAsociado = "Colaboradores",
            observaciones = ""
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Organizaciones") },
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
            FloatingActionButton(onClick = { /* agregar organizacion */ }) {
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
            items(organizaciones) { organizacion ->
                OrganizacionCard(organizacion = organizacion)
            }
        }
    }
}

@Composable
fun OrganizacionCard(organizacion: Organizacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = organizacion.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = organizacion.tipoOrganizacion,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = organizacion.comuna,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = organizacion.region,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

