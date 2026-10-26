package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.viewmodel.OrganizacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionFormScreen(navController: NavController) {

    val viewModel: OrganizacionViewModel = viewModel()
    val nombre by viewModel.nombre.collectAsState()
    val tipoOrganizacion by viewModel.tipoOrganizacion.collectAsState()
    val rubro by viewModel.rubro.collectAsState()
    val comuna by viewModel.comuna.collectAsState()
    val region by viewModel.region.collectAsState()
    val observaciones by viewModel.observaciones.collectAsState()
    val guardadoExitosamente by viewModel.guardadoExitosamente.collectAsState()

    LaunchedEffect(guardadoExitosamente) {
        if (guardadoExitosamente) {
            viewModel.limpiarFormulario()
            navController.popBackStack()
        }
    } // fin LaunchedEffect

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Organización") },
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

            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tipoOrganizacion,
                onValueChange = { viewModel.onTipoChange(it) },
                label = { Text("Tipo (Empresa, Municipalidad, etc.)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rubro,
                onValueChange = { viewModel.onRubroChange(it) },
                label = { Text("Rubro") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = comuna,
                onValueChange = { viewModel.onComunaChange(it) },
                label = { Text("Comuna") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = region,
                onValueChange = { viewModel.onRegionChange(it) },
                label = { Text("Región") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = observaciones,
                onValueChange = { viewModel.onObservacionesChange(it) },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.guardar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            } // fin Button

        } // fin Column

    } // fin Scaffold

} // fin OrganizacionFormScreen

