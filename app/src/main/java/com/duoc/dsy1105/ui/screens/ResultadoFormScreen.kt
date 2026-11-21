package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.model.TiposResultado
import com.duoc.dsy1105.ui.components.CampoTexto
import com.duoc.dsy1105.ui.components.SelectorDesplegable
import com.duoc.dsy1105.ui.components.SelectorEntidad
import com.duoc.dsy1105.viewmodel.LoginViewModel
import com.duoc.dsy1105.viewmodel.ResultadoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadoFormScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    resultadoId: Long? = null
) {
    val viewModel: ResultadoViewModel = viewModel()
    val estado by viewModel.estado.collectAsState()
    val activaciones by viewModel.activaciones.collectAsState()
    val oportunidades by viewModel.oportunidades.collectAsState()
    val esEdicion = resultadoId != null

    // Permiso según el perfil (el repositorio lo vuelve a validar al guardar)
    val sesion by loginViewModel.usuario.collectAsState()
    val puedeGuardar = sesion?.perfil?.puedeRegistrarResultados == true

    LaunchedEffect(resultadoId) {
        if (resultadoId != null) viewModel.cargarResultado(resultadoId)
    }

    LaunchedEffect(estado.guardadoExitosamente) {
        if (estado.guardadoExitosamente) {
            viewModel.limpiarFormulario()
            navController.popBackStack()
        }
    } // fin LaunchedEffect

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Resultado" else "Nuevo Resultado") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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

            SelectorEntidad(
                etiqueta = "Activación",
                seleccionado = activaciones.find { it.id == estado.activacionId },
                opciones = activaciones,
                textoDe = { a ->
                    (oportunidades.find { it.id == a.oportunidadId }?.nombre ?: "Oportunidad #${a.oportunidadId}") +
                        " (${a.fecha})"
                },
                onSeleccion = { viewModel.onActivacionChange(it?.id ?: 0L) },
                error = estado.errores.activacion
            )

            SelectorDesplegable(
                etiqueta = "Resultado",
                valor = estado.resultado,
                opciones = TiposResultado.todos,
                onSeleccion = { viewModel.onResultadoChange(it) },
                error = estado.errores.resultado
            )

            CampoTexto(
                etiqueta = "Contactos generados",
                valor = estado.contactosGenerados,
                onValorChange = { viewModel.onContactosChange(it) },
                error = estado.errores.contactos,
                teclado = KeyboardType.Number
            )

            CampoTexto(
                etiqueta = "Evidencia (foto o documento)",
                valor = estado.evidencia,
                onValorChange = { viewModel.onEvidenciaChange(it) }
            )

            CampoTexto(
                etiqueta = "Observaciones",
                valor = estado.observaciones,
                onValorChange = { viewModel.onObservacionesChange(it) },
                minLines = 2
            )

            // Error general (por ejemplo, falta de permiso al guardar)
            estado.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!puedeGuardar) {
                Text(
                    text = "Solo lectura: tu perfil no puede modificar resultados",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    sesion?.let { usuario ->
                        if (resultadoId != null) viewModel.actualizar(resultadoId, usuario)
                        else viewModel.guardar(usuario)
                    }
                },
                enabled = puedeGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esEdicion) "Actualizar" else "Guardar")
            } // fin Button

        } // fin Column

    } // fin Scaffold

} // fin ResultadoFormScreen
