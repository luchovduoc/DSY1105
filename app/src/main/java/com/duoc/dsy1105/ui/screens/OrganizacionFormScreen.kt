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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.MaterialTheme
import com.duoc.dsy1105.viewmodel.LoginViewModel
import com.duoc.dsy1105.ui.components.SelectorRegionComuna
import com.duoc.dsy1105.viewmodel.OrganizacionViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionFormScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    organizacionId: Long? = null
) {

    val viewModel: OrganizacionViewModel = viewModel()
    val esEdicion = organizacionId != null

    // Permiso segun el perfil (el repositorio lo vuelve a validar al guardar)
    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil
    val puedeGuardar = perfil != null && (
        if (esEdicion) perfil.puedeEditarOrganizaciones else perfil.puedeCrearOrganizaciones
        )

// Si es edicion, carga los datos una sola vez
    LaunchedEffect(organizacionId) {
        if (organizacionId != null) {
            viewModel.cargarOrganizacion(organizacionId)
        }
    }
    // Estado único del formulario: valores y errores vienen del mismo objeto
    val estado by viewModel.estado.collectAsState()
    val nombre = estado.nombre
    val tipoOrganizacion = estado.tipoOrganizacion
    val rubro = estado.rubro
    val comuna = estado.comuna
    val region = estado.region
    val observaciones = estado.observaciones
    val guardadoExitosamente = estado.guardadoExitosamente
    val nombreError = estado.errores.nombre
    val tipoError = estado.errores.tipo
    val rubroError = estado.errores.rubro
    val regionError = estado.errores.region
    val comunaError = estado.errores.comuna



    LaunchedEffect(guardadoExitosamente) {
        if (guardadoExitosamente) {
            viewModel.limpiarFormulario()
            navController.popBackStack()
        }
    } // fin LaunchedEffect

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Organización" else "Nueva Organización") },
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

            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre") },
                isError = nombreError != null,
                supportingText = {
                    nombreError?.let {
                        Text(it)
                    }
                },
                trailingIcon = {
                    if (nombreError != null) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = "Error"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tipoOrganizacion,
                onValueChange = { viewModel.onTipoChange(it) },
                label = { Text("Tipo (Empresa, Municipalidad, etc.)") },
                isError = tipoError != null,
                supportingText = {
                    tipoError?.let {
                        Text(it)
                    }
                },
                trailingIcon = {
                    if (tipoError != null) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = "Error"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rubro,
                onValueChange = { viewModel.onRubroChange(it) },
                label = { Text("Rubro") },
                isError = rubroError != null,
                supportingText = {
                    rubroError?.let {
                        Text(it)
                    }
                },
                trailingIcon = {
                    if (rubroError != null) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = "Error"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            SelectorRegionComuna(
                region = region,
                comuna = comuna,
                onRegionChange = { viewModel.onRegionChange(it) },
                onComunaChange = { viewModel.onComunaChange(it) },
                errorRegion = regionError,
                errorComuna = comunaError
            )

            OutlinedTextField(
                value = observaciones,
                onValueChange = { viewModel.onObservacionesChange(it) },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
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

            if (!puedeGuardar) {
                Text(
                    text = "Solo lectura: tu perfil no puede modificar organizaciones",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    sesion?.let { usuario ->
                        if (organizacionId != null) viewModel.actualizar(organizacionId, usuario)
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

} // fin OrganizacionFormScreen

