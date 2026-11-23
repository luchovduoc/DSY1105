package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import com.duoc.dsy1105.model.EventoRemoto
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.viewmodel.CatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    usuario: Usuario?,
    onVolver: () -> Unit
) {
    val eventos by viewModel.eventos.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val aviso by viewModel.aviso.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarEventos()
    }

    val perfil = usuario?.perfil?.etiqueta.orEmpty()

    val puedeCrear = perfil == "Ejecutivo Comercial"
    val puedeEditar =
        perfil == "Ejecutivo Comercial" ||
                perfil == "Supervisor Comercial"
    val puedeEliminar = perfil == "Supervisor Comercial"

    val puedeImportar =
        usuario?.perfil?.puedeCrearOportunidades == true

    var mostrarFormulario by remember { mutableStateOf(false) }
    var eventoEditando by remember {
        mutableStateOf<EventoRemoto?>(null)
    }

    var eventoEliminar by remember {
        mutableStateOf<EventoRemoto?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de eventos") },
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

            if (puedeCrear && usuario != null) {
                Button(
                    onClick = {
                        eventoEditando = null
                        mostrarFormulario = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear evento remoto")
                }
            }

            mensaje?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            aviso?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(eventos) { evento ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Evento: ${evento.nombre}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                "${evento.tipo} · " +
                                        "${evento.fechaInicio} · " +
                                        evento.comuna
                            )

                            if (evento.region.isNotBlank()) {
                                Text("Región: ${evento.region}")
                            }

                            if (puedeImportar && usuario != null) {
                                TextButton(
                                    onClick = {
                                        viewModel.importar(
                                            evento,
                                            usuario
                                        )
                                    }
                                ) {
                                    Text("Importar como oportunidad")
                                }
                            }

                            if (
                                usuario != null &&
                                (puedeEditar || puedeEliminar)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {

                                    if (puedeEditar) {
                                        TextButton(
                                            onClick = {
                                                eventoEditando = evento
                                                mostrarFormulario = true
                                            },
                                            modifier =
                                                Modifier.weight(1f)
                                        ) {
                                            Text("Editar")
                                        }
                                    }

                                    if (puedeEliminar) {
                                        TextButton(
                                            onClick = {
                                                eventoEliminar = evento
                                            },
                                            modifier =
                                                Modifier.weight(1f)
                                        ) {
                                            Text("Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario && usuario != null) {
        FormularioEventoRemoto(
            evento = eventoEditando,
            onCancelar = {
                mostrarFormulario = false
                eventoEditando = null
            },
            onGuardar = { evento ->

                if (eventoEditando == null) {
                    viewModel.crearEvento(
                        evento = evento,
                        solicitante = usuario
                    )
                } else {
                    viewModel.actualizarEvento(
                        evento = evento,
                        solicitante = usuario
                    )
                }

                mostrarFormulario = false
                eventoEditando = null
            }
        )
    }

    eventoEliminar?.let { evento ->

        if (usuario != null) {
            AlertDialog(
                onDismissRequest = {
                    eventoEliminar = null
                },
                title = {
                    Text("Eliminar evento")
                },
                text = {
                    Text(
                        "¿Deseas eliminar \"${evento.nombre}\" " +
                                "de MongoDB?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.eliminarEvento(
                                evento = evento,
                                solicitante = usuario
                            )
                            eventoEliminar = null
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            eventoEliminar = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun FormularioEventoRemoto(
    evento: EventoRemoto?,
    onCancelar: () -> Unit,
    onGuardar: (EventoRemoto) -> Unit
) {
    var nombre by remember(evento) {
        mutableStateOf(evento?.nombre.orEmpty())
    }

    var tipo by remember(evento) {
        mutableStateOf(evento?.tipo.orEmpty())
    }

    var descripcion by remember(evento) {
        mutableStateOf(evento?.descripcion.orEmpty())
    }

    var fechaInicio by remember(evento) {
        mutableStateOf(evento?.fechaInicio.orEmpty())
    }

    var comuna by remember(evento) {
        mutableStateOf(evento?.comuna.orEmpty())
    }

    var region by remember(evento) {
        mutableStateOf(evento?.region.orEmpty())
    }

    val formularioValido =
        nombre.isNotBlank() &&
                tipo.isNotBlank() &&
                fechaInicio.isNotBlank() &&
                comuna.isNotBlank() &&
                region.isNotBlank()

    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text(
                if (evento == null) {
                    "Crear evento"
                } else {
                    "Editar evento"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tipo,
                    onValueChange = { tipo = it },
                    label = { Text("Tipo *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = { Text("Fecha inicio *") },
                    placeholder = {
                        Text("dd/MM/yyyy")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = comuna,
                    onValueChange = { comuna = it },
                    label = { Text("Comuna *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Región *") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = formularioValido,
                onClick = {
                    val actualizado =
                        (evento ?: EventoRemoto()).copy(
                            nombre = nombre.trim(),
                            tipo = tipo.trim(),
                            descripcion = descripcion.trim(),
                            fechaInicio = fechaInicio.trim(),
                            comuna = comuna.trim(),
                            region = region.trim()
                        )

                    onGuardar(actualizado)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}