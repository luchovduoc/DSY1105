package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.model.TiposAccion
import com.duoc.dsy1105.ui.components.CampoFecha
import com.duoc.dsy1105.ui.components.CampoTexto
import com.duoc.dsy1105.ui.components.SelectorDesplegable
import com.duoc.dsy1105.ui.components.SelectorEntidad
import com.duoc.dsy1105.viewmodel.LoginViewModel
import com.duoc.dsy1105.viewmodel.SeguimientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguimientoFormScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    oportunidadId: Long
) {
    val viewModel: SeguimientoViewModel = viewModel()
    val estado by viewModel.estado.collectAsState()
    val contactos by viewModel.contactos.collectAsState()
    val sesion by loginViewModel.usuario.collectAsState()

    LaunchedEffect(estado.guardadoExitosamente) {
        if (estado.guardadoExitosamente) {
            viewModel.limpiarFormulario()
            navController.popBackStack()
        }
    } // fin LaunchedEffect

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar seguimiento") },
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

            SelectorDesplegable(
                etiqueta = "Tipo de acción",
                valor = estado.tipoAccion,
                opciones = TiposAccion.manuales,
                onSeleccion = { viewModel.onTipoAccionChange(it) },
                error = estado.errores.tipoAccion
            )

            CampoFecha(
                etiqueta = "Fecha de la acción",
                valor = estado.fecha,
                onFechaSeleccionada = { viewModel.onFechaChange(it) },
                error = estado.errores.fecha
            )

            SelectorEntidad(
                etiqueta = "Contacto (opcional)",
                seleccionado = contactos.find { it.id == estado.contactoId },
                opciones = contactos,
                textoDe = { it.nombre },
                onSeleccion = { viewModel.onContactoChange(it?.id ?: 0L) },
                textoVacio = "Sin contacto",
                permiteVacio = true
            )

            CampoTexto(
                etiqueta = "Resultado",
                valor = estado.resultado,
                onValorChange = { viewModel.onResultadoChange(it) },
                minLines = 2
            )

            CampoTexto(
                etiqueta = "Próxima acción",
                valor = estado.proximaAccion,
                onValorChange = { viewModel.onProximaAccionChange(it) }
            )

            CampoFecha(
                etiqueta = "Fecha del próximo seguimiento (opcional)",
                valor = estado.fechaProximoSeguimiento,
                onFechaSeleccionada = { viewModel.onFechaProximoChange(it) },
                error = estado.errores.fechaProximo
            )

            CampoTexto(
                etiqueta = "Observaciones",
                valor = estado.observaciones,
                onValorChange = { viewModel.onObservacionesChange(it) },
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Error general (por ejemplo, falta de permiso al guardar)
            estado.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    sesion?.let { usuario -> viewModel.guardar(oportunidadId, usuario) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            } // fin Button

        } // fin Column

    } // fin Scaffold

} // fin SeguimientoFormScreen
