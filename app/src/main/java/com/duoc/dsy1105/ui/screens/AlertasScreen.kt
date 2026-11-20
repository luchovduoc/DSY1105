package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.model.AlertasUiState
import com.duoc.dsy1105.utils.GeneradorAlertas
import com.duoc.dsy1105.viewmodel.AlertasViewModel
import com.duoc.dsy1105.viewmodel.LoginViewModel

// Avisos internos (el caso permite implementar las notificaciones como avisos dentro de la app)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val viewModel: AlertasViewModel = viewModel()
    val sesion by loginViewModel.usuario.collectAsState()
    val alertas by remember(sesion) { viewModel.alertasPara(sesion) }
        .collectAsState(initial = AlertasUiState())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertas") },
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

        if (alertas.total == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes alertas pendientes",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Seccion(
                    titulo = "Seguimientos atrasados",
                    cantidad = alertas.seguimientosAtrasados.size,
                    destacada = true
                ) {
                    alertas.seguimientosAtrasados.forEach { alerta ->
                        ItemAlerta(
                            titulo = alerta.oportunidad.nombre,
                            detalle = "Debía hacerse el ${alerta.seguimiento.fechaProximoSeguimiento} " +
                                "(hace ${-alerta.dias} días)" +
                                accionPendiente(alerta.seguimiento.proximaAccion),
                            onClick = { navController.navigate("oportunidad_detalle/${alerta.oportunidad.id}") }
                        )
                    }
                }

                Seccion(
                    titulo = "Seguimientos próximos (${GeneradorAlertas.DIAS_SEGUIMIENTOS} días)",
                    cantidad = alertas.seguimientosProximos.size
                ) {
                    alertas.seguimientosProximos.forEach { alerta ->
                        ItemAlerta(
                            titulo = alerta.oportunidad.nombre,
                            detalle = (if (alerta.dias == 0) "Hoy" else "En ${alerta.dias} días") +
                                " (${alerta.seguimiento.fechaProximoSeguimiento})" +
                                accionPendiente(alerta.seguimiento.proximaAccion),
                            onClick = { navController.navigate("oportunidad_detalle/${alerta.oportunidad.id}") }
                        )
                    }
                }

                Seccion(
                    titulo = "Eventos próximos (${GeneradorAlertas.DIAS_EVENTOS} días)",
                    cantidad = alertas.eventosProximos.size
                ) {
                    alertas.eventosProximos.forEach { evento ->
                        ItemAlerta(
                            titulo = evento.nombre,
                            detalle = "${rangoDeFechas(evento)} · ${etiquetaTiempo(evento)}",
                            onClick = { navController.navigate("evento_detalle/${evento.id}") }
                        )
                    }
                }

                Seccion(
                    titulo = "Propuestas sin respuesta",
                    cantidad = alertas.propuestasSinRespuesta.size
                ) {
                    alertas.propuestasSinRespuesta.forEach { oportunidad ->
                        ItemAlerta(
                            titulo = oportunidad.nombre,
                            detalle = oportunidad.estado,
                            onClick = { navController.navigate("oportunidad_detalle/${oportunidad.id}") }
                        )
                    }
                }

                Seccion(
                    titulo = "Oportunidades sin contacto",
                    cantidad = alertas.oportunidadesSinContacto.size
                ) {
                    alertas.oportunidadesSinContacto.forEach { oportunidad ->
                        ItemAlerta(
                            titulo = oportunidad.nombre,
                            detalle = oportunidad.estado,
                            onClick = { navController.navigate("oportunidad_detalle/${oportunidad.id}") }
                        )
                    }
                }

                Seccion(
                    titulo = "Información incompleta",
                    cantidad = alertas.informacionIncompleta.size
                ) {
                    alertas.informacionIncompleta.forEach { oportunidad ->
                        ItemAlerta(
                            titulo = oportunidad.nombre,
                            detalle = "Sin evento asociado",
                            onClick = { navController.navigate("oportunidad_detalle/${oportunidad.id}") }
                        )
                    }
                }

                Seccion(
                    titulo = "Sin responsable asignado",
                    cantidad = alertas.sinResponsable.size
                ) {
                    alertas.sinResponsable.forEach { oportunidad ->
                        ItemAlerta(
                            titulo = oportunidad.nombre,
                            detalle = "Asígnala a un ejecutivo",
                            onClick = { navController.navigate("oportunidad_detalle/${oportunidad.id}") }
                        )
                    }
                }

            } // fin Column
        }

    } // fin Scaffold

} // fin AlertasScreen

private fun accionPendiente(proximaAccion: String): String =
    if (proximaAccion.isBlank()) "" else " · $proximaAccion"

// Una seccion solo se muestra si tiene alertas
@Composable
private fun Seccion(
    titulo: String,
    cantidad: Int,
    destacada: Boolean = false,
    contenido: @Composable () -> Unit
) {
    if (cantidad == 0) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "$titulo ($cantidad)",
            style = MaterialTheme.typography.titleMedium,
            color = if (destacada) MaterialTheme.colorScheme.error else Color.Unspecified
        )
        contenido()
    }
} // fin Seccion

@Composable
private fun ItemAlerta(
    titulo: String,
    detalle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = detalle,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} // fin ItemAlerta
