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
import androidx.compose.material.icons.filled.Delete // AGREGADO
import androidx.compose.material3.AlertDialog // AGREGADO
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton // AGREGADO
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // AGREGADO
import androidx.compose.runtime.remember // AGREGADO
import androidx.compose.runtime.setValue // AGREGADO
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.viewmodel.OrganizacionViewModel
import androidx.compose.foundation.clickable // AGREGADO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionesScreen(navController: NavController) {

    val viewModel: OrganizacionViewModel = viewModel()
    val organizaciones by viewModel.organizaciones.collectAsState()

    // AGREGADO: organizacion pendiente de confirmar (estado visual, vive en la UI)
    var organizacionAEliminar by remember { mutableStateOf<OrganizacionEntity?>(null) }

    // AGREGADO: dialogo de confirmacion
    organizacionAEliminar?.let { organizacion ->
        AlertDialog(
            onDismissRequest = { organizacionAEliminar = null },
            title = { Text("Eliminar organización") },
            text = { Text("¿Seguro que quieres eliminar \"${organizacion.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(organizacion)
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
        }, // fin topBar
        floatingActionButton = {
            FloatingActionButton(onClick = {
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
                    text = "No hay organizaciones registradas",
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
                        onClick = { navController.navigate("organizacion_form?id=${organizacion.id}") }, // AGREGADO
                        onEliminar = { organizacionAEliminar = organizacion } // AGREGADO
                    )
                } // fin items
            } // fin LazyColumn
        }

    } // fin Scaffold

} // fin OrganizacionesScreen

@Composable
fun OrganizacionCard(
    organizacion: OrganizacionEntity,
    onClick: () -> Unit,       // AGREGADO
    onEliminar: () -> Unit // AGREGADO
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // AGREGADO
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // AGREGADO: Row para poner el basurero a la derecha de los datos
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

            // AGREGADO: boton basurero
            IconButton(onClick = onEliminar) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        } // fin Row
    } // fin Card
} // fin OrganizacionCard