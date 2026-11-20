package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.viewmodel.LoginViewModel
import com.duoc.dsy1105.viewmodel.UsuariosViewModel

// Gestión de usuarios: solo el supervisor asigna perfiles y activa o desactiva cuentas.
// La pantalla oculta las opciones, pero el control real está en UsuarioRepository.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val viewModel: UsuariosViewModel = viewModel()

    val sesion by loginViewModel.usuario.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()
    val estado by viewModel.estado.collectAsState()
    val solicitante = sesion

    // EN PAUSA: dialogo para eliminar cuentas
    // // Cuenta pendiente de confirmar (estado visual, vive en la UI)
    // var usuarioAEliminar by remember { mutableStateOf<UsuarioEntity?>(null) }

    // usuarioAEliminar?.let { usuario ->
    //     AlertDialog(
    //         onDismissRequest = { usuarioAEliminar = null },
    //         title = { Text("Eliminar cuenta") },
    //         text = {
    //             Text(
    //                 "¿Seguro que quieres eliminar a \"${usuario.nombre}\"? Solo se puede si no tiene " +
    //                     "actividad registrada; si la tiene, desactívala. Esta acción no se puede deshacer."
    //             )
    //         },
    //         confirmButton = {
    //             TextButton(onClick = {
    //                 solicitante?.let { viewModel.onEliminar(it.id.toLong(), usuario.id) }
    //                 usuarioAEliminar = null
    //             }) {
    //                 Text("Eliminar")
    //             }
    //         },
    //         dismissButton = {
    //             TextButton(onClick = { usuarioAEliminar = null }) {
    //                 Text("Cancelar")
    //             }
    //         }
    //     ) // fin AlertDialog
    // } // fin dialogo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de usuarios") },
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

        if (solicitante == null || !solicitante.perfil.puedeGestionarUsuarios) {
            Text(
                text = "No tienes permiso para ver esta pantalla",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Error de la última acción (por ejemplo, intentar quitar al último supervisor)
                estado.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (usuarios.isEmpty()) {
                    Text("No hay usuarios registrados")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(usuarios, key = { it.id }) { usuario ->
                            UsuarioItem(
                                usuario = usuario,
                                esCuentaPropia = usuario.id == solicitante.id.toLong(),
                                onCambiarPerfil = { perfil ->
                                    viewModel.onCambiarPerfil(
                                        solicitante.id.toLong(),
                                        usuario.id,
                                        perfil
                                    )
                                },
                                onCambiarActivo = { activo ->
                                    viewModel.onCambiarActivo(
                                        solicitante.id.toLong(),
                                        usuario.id,
                                        activo
                                    )
                                },
                                // EN PAUSA: boton eliminar
                                // onEliminar = { usuarioAEliminar = usuario }
                            )
                        }
                    }
                }
            } // fin Column
        }

    } // fin Scaffold

} // fin UsuariosScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsuarioItem(
    usuario: UsuarioEntity,
    esCuentaPropia: Boolean,
    onCambiarPerfil: (Perfil) -> Unit,
    onCambiarActivo: (Boolean) -> Unit,
    // EN PAUSA: boton eliminar
    // onEliminar: () -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    val perfilActual = Perfil.desdeEtiqueta(usuario.perfil)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (esCuentaPropia) "${usuario.nombre} (tú)" else usuario.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                // EN PAUSA: boton eliminar cuenta
                // // Tu propia cuenta no se elimina desde aqui
                // IconButton(onClick = onEliminar, enabled = !esCuentaPropia) {
                //     Icon(Icons.Filled.Delete, contentDescription = "Eliminar cuenta")
                // }
            }
            Text(
                text = usuario.email,
                style = MaterialTheme.typography.bodySmall
            )

            // Tu propia cuenta no se edita aquí (evita quedarte sin acceso por error)
            ExposedDropdownMenuBox(
                expanded = expandido && !esCuentaPropia,
                onExpandedChange = { if (!esCuentaPropia) expandido = !expandido }
            ) {
                OutlinedTextField(
                    value = perfilActual.etiqueta,
                    onValueChange = {},
                    readOnly = true,
                    enabled = !esCuentaPropia,
                    label = { Text("Perfil") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandido && !esCuentaPropia,
                    onDismissRequest = { expandido = false }
                ) {
                    Perfil.entries.forEach { perfil ->
                        DropdownMenuItem(
                            text = { Text(perfil.etiqueta) },
                            onClick = {
                                expandido = false
                                onCambiarPerfil(perfil)
                            }
                        )
                    }
                }
            } // fin ExposedDropdownMenuBox

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (usuario.activo) "Cuenta activa" else "Cuenta desactivada")
                Switch(
                    checked = usuario.activo,
                    onCheckedChange = onCambiarActivo,
                    enabled = !esCuentaPropia
                )
            }
        }
    }
} // fin UsuarioItem
