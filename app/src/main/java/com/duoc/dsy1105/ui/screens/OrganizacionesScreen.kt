package com.duoc.dsy1105.ui.screens

import android.R.attr.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.ui.components.BarraBusqueda
import com.duoc.dsy1105.viewmodel.LoginViewModel
import com.duoc.dsy1105.viewmodel.OrganizacionViewModel
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionesScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {

    val viewModel: OrganizacionViewModel = viewModel()
    val organizaciones by viewModel.organizaciones.collectAsState()
    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil
    val puedeEditar = perfil?.puedeEditarOrganizaciones == true
    val usuarioId = sesion?.id?.toLong() ?: 0L
    val estado by viewModel.estado.collectAsState()
    val filtroTexto by viewModel.filtroTexto.collectAsState()

    // Organizacion pendiente de confirmar (estado visual, vive en la UI)
    var organizacionAEliminar by remember { mutableStateOf<OrganizacionEntity?>(null) }

    // Dialogo de confirmacion
    organizacionAEliminar?.let { organizacion ->
        AlertDialog(
            onDismissRequest = { organizacionAEliminar = null },
            title = { Text("Eliminar organización") },
            text = { Text("¿Seguro que quieres eliminar \"${organizacion.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    sesion?.let { viewModel.eliminar(it, organizacion) }
                    organizacionAEliminar = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { organizacionAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        ) // fin AlertDialog
    } // fin dialogo

    // Si no se pudo eliminar (sin permiso o registro en uso) se explica por que
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
            Column {
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
                BarraBusqueda(
                    texto = filtroTexto,
                    onTextoChange = viewModel::onFiltroTextoChange
                )
            }
        }, // fin topBar
        floatingActionButton = {
            // Solo el ejecutivo registra organizaciones
            if (perfil?.puedeCrearOrganizaciones == true) FloatingActionButton(onClick = {
                navController.navigate("organizacion_form")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        } // fin floatingActionButton
    ) { innerPadding ->

        if (organizaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (filtroTexto.isNotBlank()) "Ninguna organización coincide con la búsqueda" else "No hay organizaciones registradas",
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
                items(organizaciones) { organizacion ->
                    OrganizacionCard(
                        organizacion = organizacion,
                        // Abre el formulario solo quien puede editar; elimina solo el supervisor
                        onClick = if (puedeEditar) {
                            { navController.navigate("organizacion_form?id=${organizacion.id}") }
                        } else null,
                        onEliminar = if (perfil?.puedeEliminarRegistro(organizacion.creadoPorId, usuarioId) == true) {
                            { organizacionAEliminar = organizacion }
                        } else null
                    )
                } // fin items
            } // fin LazyColumn
        }

    } // fin Scaffold

} // fin OrganizacionesScreen

@Composable
fun OrganizacionCard(
    organizacion: OrganizacionEntity,
    onClick: (() -> Unit)?,       // null = solo lectura
    onEliminar: (() -> Unit)? // null = el perfil no puede eliminar
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Row para poner el basurero a la derecha de los datos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
                } // fin Row datos
            } // fin Column

            // Boton basurero
            if (onEliminar != null) {
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
        } // fin Row
    } // fin Card
} // fin OrganizacionCard