package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.viewmodel.OportunidadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadesScreen(navController: NavController) {

    val viewModel: OportunidadViewModel = viewModel()
    val oportunidades by viewModel.oportunidades.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Oportunidades") },
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
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("oportunidad_form")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        } // fin floatingActionButton
    ) { innerPadding ->

        if (oportunidades.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay oportunidades registradas",
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
                items(oportunidades) { oportunidad ->
                    OportunidadCard(
                        oportunidad = oportunidad,
                        onClick = {
                            navController.navigate("oportunidad_form?id=${oportunidad.id}")
                        }
                    )
                } // fin items
            } // fin LazyColumn
        }

    } // fin Scaffold

} // fin OportunidadesScreen

@Composable
fun OportunidadCard(
    oportunidad: OportunidadEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = oportunidad.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = oportunidad.tipoOportunidad,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Prioridad: ${oportunidad.nivelPrioridad}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = oportunidad.estado,
                    style = MaterialTheme.typography.bodySmall
                )
            } // fin Row
        } // fin Column
    } // fin Card
} // fin OportunidadCard