package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.viewmodel.CatalogoViewModel

// Catálogo de eventos de la API. El ViewModel llega por parámetro para poder probar la pantalla
// con un repositorio falso. [usuario] null o sin permiso: solo se consulta, sin botón de importar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    usuario: Usuario?,
    onVolver: () -> Unit
) {
    // Observamos el flujo de datos del ViewModel
    val eventos by viewModel.eventos.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val aviso by viewModel.aviso.collectAsState()
    val puedeImportar = usuario?.perfil?.puedeCrearOportunidades == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de eventos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            mensaje?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            aviso?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(eventos) { evento ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Evento: ${evento.nombre}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${evento.tipo} · ${evento.fechaInicio} · ${evento.comuna}")
                            if (puedeImportar && usuario != null) {
                                TextButton(onClick = { viewModel.importar(evento, usuario) }) {
                                    Text("Importar como oportunidad")
                                }
                            }
                        }
                    }
                }
            }
        } // fin Column

    } // fin Scaffold

} // fin CatalogoScreen
