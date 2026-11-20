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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.model.CatalogosContacto
import com.duoc.dsy1105.ui.components.CampoTexto
import com.duoc.dsy1105.ui.components.SelectorDesplegable
import com.duoc.dsy1105.ui.components.SelectorEntidad
import com.duoc.dsy1105.viewmodel.ContactoViewModel
import com.duoc.dsy1105.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoFormScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    contactoId: Long? = null
) {
    val viewModel: ContactoViewModel = viewModel()
    val estado by viewModel.estado.collectAsState()
    val organizaciones by viewModel.organizaciones.collectAsState()
    val esEdicion = contactoId != null

    // Permiso segun el perfil (el repositorio lo vuelve a validar al guardar)
    val sesion by loginViewModel.usuario.collectAsState()
    val perfil = sesion?.perfil
    val puedeGuardar = perfil != null && (
        if (esEdicion) perfil.puedeEditarContactos else perfil.puedeCrearContactos
        )

    // Si es edicion, carga los datos una sola vez
    LaunchedEffect(contactoId) {
        if (contactoId != null) {
            viewModel.cargarContacto(contactoId)
        }
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
                title = { Text(if (esEdicion) "Editar Contacto" else "Nuevo Contacto") },
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

            // El caso exige datos ficticios: nada de personas, correos ni teléfonos reales
            Text(
                text = "Usa solo datos ficticios (nombre, correo y teléfono).",
                style = MaterialTheme.typography.bodySmall
            )

            CampoTexto(
                etiqueta = "Nombre",
                valor = estado.nombre,
                onValorChange = { viewModel.onNombreChange(it) },
                error = estado.errores.nombre
            )

            SelectorEntidad(
                etiqueta = "Organización",
                seleccionado = organizaciones.find { it.id == estado.organizacionId },
                opciones = organizaciones,
                textoDe = { it.nombre },
                onSeleccion = { viewModel.onOrganizacionChange(it?.id ?: 0L) },
                error = estado.errores.organizacion
            )

            CampoTexto(
                etiqueta = "Cargo",
                valor = estado.cargo,
                onValorChange = { viewModel.onCargoChange(it) }
            )

            SelectorDesplegable(
                etiqueta = "Área",
                valor = estado.area,
                opciones = CatalogosContacto.areas,
                onSeleccion = { viewModel.onAreaChange(it) },
                error = estado.errores.area
            )

            CampoTexto(
                etiqueta = "Correo (ficticio)",
                valor = estado.email,
                onValorChange = { viewModel.onEmailChange(it) },
                error = estado.errores.email,
                teclado = KeyboardType.Email
            )

            CampoTexto(
                etiqueta = "Teléfono (ficticio)",
                valor = estado.telefono,
                onValorChange = { viewModel.onTelefonoChange(it) },
                error = estado.errores.telefono,
                teclado = KeyboardType.Phone
            )

            SelectorDesplegable(
                etiqueta = "Medio de contacto preferente",
                valor = estado.medioContacto,
                opciones = CatalogosContacto.medios,
                onSeleccion = { viewModel.onMedioContactoChange(it) },
                permiteVacio = true,
                textoVacio = "Sin preferencia"
            )

            SelectorDesplegable(
                etiqueta = "Estado del contacto",
                valor = estado.estado,
                opciones = CatalogosContacto.estados,
                onSeleccion = { viewModel.onEstadoChange(it) }
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

            if (!puedeGuardar) {
                Text(
                    text = "Solo lectura: tu perfil no puede modificar contactos",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    sesion?.let { usuario ->
                        if (contactoId != null) viewModel.actualizar(contactoId, usuario)
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

} // fin ContactoFormScreen
