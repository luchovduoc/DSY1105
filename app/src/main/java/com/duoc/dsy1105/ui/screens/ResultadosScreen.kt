package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.viewmodel.LoginViewModel
import com.duoc.dsy1105.viewmodel.ResultadoViewModel

// Lista de resultados: los ven Gestión de Activaciones y el supervisor (el repositorio valida el resto)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val viewModel: ResultadoViewModel = viewModel()
    val resultados by viewModel.resultados.collectAsState()
    val activaciones by viewModel.activaciones.collectAsState()
    val oportunidades by viewModel.oportunidades.collectAsState()
    val estado by viewModel.estado.collectAsState()
    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (perfil?.puedeRegistrarResultados == true) {
                FloatingActionButton(onClick = { navController.navigate("resultado_form") }) {
                    Icon(Icons.Filled.Add, contentDescription = "Nuevo resultado")
                }
            }
        }
    ) { innerPadding ->

        if (perfil == null || !perfil.puedeVerResultados) {
            Text(
                text = "No tienes permiso para ver esta pantalla",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                estado.error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                if (resultados.isEmpty()) {
                    Text("No hay resultados registrados")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(resultados, key = { it.id }) { resultado ->
                            val activacion = activaciones.find { it.id == resultado.activacionId }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = perfil.puedeRegistrarResultados) {
                                        navController.navigate("resultado_form?id=${resultado.id}")
                                    }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = oportunidades.find { it.id == activacion?.oportunidadId }
                                            ?.nombre ?: "Activación #${resultado.activacionId}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("Resultado: ${resultado.resultado}")
                                    Text("Contactos generados: ${resultado.contactosGenerados}")
                                    if (resultado.evidencia.isNotBlank()) {
                                        Text("Evidencia: ${resultado.evidencia}")
                                    }
                                    if (perfil.puedeRegistrarResultados) {
                                        TextButton(onClick = {
                                            sesion?.let { viewModel.eliminar(it, resultado) }
                                        }) {
                                            Text("Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } // fin Column
        }

    } // fin Scaffold

} // fin ResultadosScreen
