package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.dsy1105.viewmodel.FeriadoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeriadosScreen(
    viewModel: FeriadoViewModel,
    onVolver: () -> Unit
) {
    val feriados by viewModel.feriados.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Feriados de Chile")
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
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

            Text(
                text = "API externa · Nager.Date",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = "Feriados oficiales utilizados como apoyo para la planificación de eventos.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (cargando) {
                CircularProgressIndicator()
            }

            mensaje?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                items(feriados) { feriado ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = feriado.localName,
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text("Fecha: ${feriado.date}")

                            if (
                                feriado.name.isNotBlank() &&
                                feriado.name != feriado.localName
                            ) {
                                Text(
                                    text = feriado.name,
                                    style =
                                        MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
