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
import com.duoc.dsy1105.database.ContactoEntity
import com.duoc.dsy1105.model.CatalogosContacto
import com.duoc.dsy1105.ui.components.BarraBusqueda
import com.duoc.dsy1105.ui.components.FilaFiltros
import com.duoc.dsy1105.ui.components.FiltroChip
import com.duoc.dsy1105.viewmodel.ContactoViewModel
import com.duoc.dsy1105.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactosScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val viewModel: ContactoViewModel = viewModel()
    val contactos by viewModel.contactos.collectAsState()
    val organizaciones by viewModel.organizaciones.collectAsState()
    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil
    val puedeEditar = perfil?.puedeEditarContactos == true
    val usuarioId = sesion?.id?.toLong() ?: 0L
    val estado by viewModel.estado.collectAsState()
    val filtros by viewModel.filtros.collectAsState()

    // Contacto pendiente de confirmar (estado visual, vive en la UI)
    var contactoAEliminar by remember { mutableStateOf<ContactoEntity?>(null) }

    contactoAEliminar?.let { contacto ->
        AlertDialog(
            onDismissRequest = { contactoAEliminar = null },
            title = { Text("Eliminar contacto") },
            text = { Text("¿Seguro que quieres eliminar a \"${contacto.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    sesion?.let { viewModel.eliminar(it, contacto) }
                    contactoAEliminar = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { contactoAEliminar = null }) {
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
                BarraBusqueda(
                    texto = filtros.texto,
                    onTextoChange = viewModel::onFiltroTextoChange
                )
                FilaFiltros {
                    FiltroChip("Área", filtros.area, CatalogosContacto.areas, viewModel::onFiltroAreaChange)
                    FiltroChip("Estado", filtros.estado, CatalogosContacto.estados, viewModel::onFiltroEstadoChange)
                }
            }
        }, // fin topBar
        floatingActionButton = {
            // Solo el ejecutivo registra contactos
            if (perfil?.puedeCrearContactos == true) FloatingActionButton(onClick = {
                navController.navigate("contacto_form")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        } // fin floatingActionButton
    ) { innerPadding ->

        if (perfil?.puedeVerContactos != true) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu perfil no tiene acceso al módulo de contactos",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (contactos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (filtros.hayFiltros) "Ningún contacto coincide con los filtros" else "No hay contactos registrados",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(contactos, key = { it.id }) { contacto ->
                    ContactoCard(
                        contacto = contacto,
                        organizacion = organizaciones.find { it.id == contacto.organizacionId }?.nombre,
                        // Abre el formulario solo quien puede editar; elimina solo el supervisor
                        onClick = if (puedeEditar) {
                            { navController.navigate("contacto_form?id=${contacto.id}") }
                        } else null,
                        onEliminar = if (perfil?.puedeEliminarRegistro(contacto.creadoPorId, usuarioId) == true) {
                            { contactoAEliminar = contacto }
                        } else null
                    )
                } // fin items
            } // fin LazyColumn
        }

    } // fin Scaffold

} // fin ContactosScreen

@Composable
fun ContactoCard(
    contacto: ContactoEntity,
    organizacion: String?,
    onClick: (() -> Unit)?,
    onEliminar: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contacto.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listOf(contacto.cargo, contacto.area)
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = organizacion ?: "Sin organización",
                    style = MaterialTheme.typography.bodySmall
                )
                if (contacto.email.isNotBlank()) {
                    Text(
                        text = contacto.email,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (contacto.telefono.isNotBlank()) {
                    Text(
                        text = contacto.telefono,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = contacto.estado,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (contacto.fechaUltimoContacto.isNotBlank()) {
                        Text(
                            text = "Último contacto: ${contacto.fechaUltimoContacto}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } // fin Column

            if (onEliminar != null) {
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
        } // fin Row
    } // fin Card
} // fin ContactoCard
