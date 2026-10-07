package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duoc.dsy1105.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val usuario by viewModel.usuario.collectAsState()
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
                    text = usuario?.perfil ?: "",
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

                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.People, contentDescription = null) },
                    label = { Text("Contactos") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )

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
                    text = "Selecciona una opción del menú",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}