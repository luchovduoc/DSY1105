package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.dsy1105.model.AlertasUiState
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.viewmodel.AlertasViewModel
import com.duoc.dsy1105.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val usuario by viewModel.usuario.collectAsState()

    // Avisos internos: se cuentan en el menu y en la pantalla de inicio
    val alertasViewModel: AlertasViewModel = viewModel()
    val alertas by remember(usuario) { alertasViewModel.alertasPara(usuario) }
        .collectAsState(initial = AlertasUiState())

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = usuario?.nombre ?: "Usuario",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = usuario?.perfil?.etiqueta ?: "",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = null) },
                    label = { Text("Oportunidades") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("oportunidades")
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                    label = { Text("Alertas") },
                    badge = { if (alertas.total > 0) Text(alertas.total.toString()) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("alertas")
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Event, contentDescription = null) },
                    label = { Text("Eventos") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }
                        navController.navigate("eventos")}
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Business, contentDescription = null) },
                    label = { Text("Organizaciones") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }
                        navController.navigate("organizaciones")}
                )

                // Gestión de Activaciones no gestiona contactos comerciales
                if (usuario?.perfil?.puedeVerContactos == true) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Filled.People, contentDescription = null) },
                        label = { Text("Contactos") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }
                            navController.navigate("contactos")}
                    )
                }

                // Las activaciones las ven Gestión de Activaciones y el supervisor
                if (usuario?.perfil?.puedeVerActivaciones == true) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Filled.Event, contentDescription = null) },
                        label = { Text("Activaciones") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }
                            navController.navigate("activaciones")}
                    )
                }

                // Los resultados los ven Gestión de Activaciones y el supervisor
                if (usuario?.perfil?.puedeVerResultados == true) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Filled.Event, contentDescription = null) },
                        label = { Text("Resultados") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }
                            navController.navigate("resultados")}
                    )
                }

                // Solo el supervisor ve la gestión de usuarios
                if (usuario?.perfil?.puedeGestionarUsuarios == true) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Filled.ManageAccounts, contentDescription = null) },
                        label = { Text("Gestión de usuarios") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("usuarios")
                        }
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.cerrarSesion()
                        navController.navigate("login")
                    }

                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Oportunidades") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Bienvenido, ${usuario?.nombre ?: "Usuario"}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu flujo de trabajo (${usuario?.perfil?.etiqueta ?: ""})",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Accesos según el perfil: cada uno ve los pasos que le corresponden
                Spacer(modifier = Modifier.height(8.dp))
                accesosDelPerfil(usuario?.perfil).forEach { (titulo, descripcion, ruta) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { navController.navigate(ruta) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = titulo, style = MaterialTheme.typography.titleMedium)
                            Text(text = descripcion, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("alertas") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (alertas.total > 0) {
                                "Tienes ${alertas.total} alertas pendientes"
                            } else {
                                "No tienes alertas pendientes"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Seguimientos, eventos próximos y propuestas sin respuesta",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Pasos del flujo de cada perfil: título, descripción y pantalla a la que lleva
private fun accesosDelPerfil(perfil: Perfil?): List<Triple<String, String, String>> =
    when (perfil) {
        Perfil.EJECUTIVO -> listOf(
            Triple("Oportunidades", "Registra oportunidades y su seguimiento", "oportunidades"),
            Triple("Catálogo de eventos", "Eventos de la API; impórtalos como oportunidad", "catalogo"),
            Triple("Organizaciones", "Empresas con las que se trabaja", "organizaciones"),
            Triple("Contactos", "Personas de contacto de cada organización", "contactos")
        )
        Perfil.SUPERVISOR -> listOf(
            Triple("Oportunidades", "Revisa, cambia el estado y asigna responsable", "oportunidades"),
            Triple("Activaciones", "Consulta las activaciones programadas", "activaciones"),
            Triple("Resultados", "Consulta los resultados de las activaciones", "resultados"),
            Triple("Gestión de usuarios", "Perfiles y cuentas", "usuarios")
        )
        Perfil.ACTIVACIONES -> listOf(
            Triple("Oportunidades confirmadas", "Las que se pueden activar", "oportunidades"),
            Triple("Activaciones", "Programa la activación y asigna su responsable", "activaciones"),
            Triple("Resultados", "Registra el resultado y su evidencia", "resultados")
        )
        null -> emptyList()
    }
