package com.duoc.dsy1105.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import com.duoc.dsy1105.model.EstadosOportunidad
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.model.TiposEvento
import com.duoc.dsy1105.ui.components.BarraBusqueda
import com.duoc.dsy1105.ui.components.FilaFiltros
import com.duoc.dsy1105.ui.components.FiltroChip
import com.duoc.dsy1105.utils.DatosChile
import com.duoc.dsy1105.viewmodel.LoginViewModel
import com.duoc.dsy1105.viewmodel.OportunidadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadesScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {

    val viewModel: OportunidadViewModel = viewModel()
    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil
    val filtros by viewModel.filtros.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()

    // Solo se listan las oportunidades que el perfil puede ver
    val oportunidades by remember(sesion) { viewModel.oportunidadesVisibles(sesion) }
        .collectAsState(initial = emptyList())
    val interactionSource = remember { MutableInteractionSource() }
    val presionado by interactionSource.collectIsPressedAsState()

    val elevacionFab by animateDpAsState(
        targetValue = if (presionado) 2.dp else 8.dp,
        label = "elevacionFab"
    )

    Scaffold(
        topBar = {
            Column {
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
                BarraBusqueda(
                    texto = filtros.texto,
                    onTextoChange = viewModel::onFiltroTextoChange
                )
                FilaFiltros {
                    FiltroChip("Estado", filtros.estado, EstadosOportunidad.todos, viewModel::onFiltroEstadoChange)
                    FiltroChip("Tipo", filtros.tipo, TiposEvento.todos, viewModel::onFiltroTipoChange)
                    FiltroChip("Prioridad", filtros.prioridad, listOf("Alta", "Media", "Baja"), viewModel::onFiltroPrioridadChange)
                    FiltroChip("Región", filtros.region, DatosChile.regiones, viewModel::onFiltroRegionChange)
                    // Solo el supervisor ve las de todos, asi que solo a el le sirve filtrar por responsable
                    if (perfil?.puedeAsignarResponsable == true) {
                        FiltroChip(
                            etiqueta = "Responsable",
                            seleccion = usuarios.find { it.id == filtros.responsableId }?.nombre ?: "",
                            opciones = usuarios
                                .filter { Perfil.desdeEtiqueta(it.perfil) == Perfil.EJECUTIVO }
                                .map { it.nombre },
                            onSeleccion = { nombre ->
                                viewModel.onFiltroResponsableChange(
                                    usuarios.find { it.nombre == nombre }?.id ?: 0L
                                )
                            }
                        )
                    }
                }
            }
        }, // fin topBar
        floatingActionButton = {
            // Solo el ejecutivo crea oportunidades
            if (perfil?.puedeCrearOportunidades == true) FloatingActionButton(onClick = {
                navController.navigate("oportunidad_form")
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar"
                )

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
                    text = if (filtros.hayFiltros) "Ninguna oportunidad coincide con la búsqueda" else when (perfil) {
                        Perfil.EJECUTIVO -> "No tienes oportunidades asignadas"
                        Perfil.ACTIVACIONES -> "No hay oportunidades confirmadas"
                        else -> "No hay oportunidades registradas"
                    },
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
                            navController.navigate("oportunidad_detalle/${oportunidad.id}")
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