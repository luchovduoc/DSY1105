package com.duoc.dsy1105.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.dsy1105.ui.screens.ActivacionFormScreen
import com.duoc.dsy1105.ui.screens.ActivacionesScreen
import com.duoc.dsy1105.ui.screens.AlertasScreen
import com.duoc.dsy1105.ui.screens.CatalogoScreen
import com.duoc.dsy1105.viewmodel.CatalogoViewModel
import com.duoc.dsy1105.ui.screens.ContactoFormScreen
import com.duoc.dsy1105.ui.screens.ContactosScreen
import com.duoc.dsy1105.ui.screens.EventoDetalleScreen
import com.duoc.dsy1105.ui.screens.EventoFormScreen
import com.duoc.dsy1105.ui.screens.EventosScreen
import com.duoc.dsy1105.ui.screens.HomeScreen
import com.duoc.dsy1105.ui.screens.LoginScreen
import com.duoc.dsy1105.ui.screens.OportunidadDetalleScreen
import com.duoc.dsy1105.ui.screens.OportunidadFormScreen
import com.duoc.dsy1105.ui.screens.OportunidadesScreen
import com.duoc.dsy1105.ui.screens.OrganizacionFormScreen
import com.duoc.dsy1105.ui.screens.OrganizacionesScreen
import com.duoc.dsy1105.ui.screens.QrScannerScreen
import com.duoc.dsy1105.ui.screens.RegistroScreen
import com.duoc.dsy1105.ui.screens.ResultadoFormScreen
import com.duoc.dsy1105.ui.screens.ResultadosScreen
import com.duoc.dsy1105.ui.screens.SeguimientoFormScreen
import com.duoc.dsy1105.ui.screens.UsuariosScreen
import com.duoc.dsy1105.viewmodel.LoginViewModel

@Composable
fun AppNav(
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }
        composable("registro") {
            RegistroScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }
        composable("oportunidades") {
            OportunidadesScreen(
                navController = navController,
                loginViewModel = loginViewModel
            )
        }
        composable("eventos") {
            EventosScreen(
                navController = navController,
                loginViewModel = loginViewModel
            )
        }
        composable("evento_form?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            EventoFormScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                eventoId = id
            )
        }
        composable("evento_detalle/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            EventoDetalleScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                eventoId = id
            )
        }
        composable("organizaciones") {
            OrganizacionesScreen(
                navController = navController,
                loginViewModel = loginViewModel
            )
        }
        composable("contactos") {
            ContactosScreen(
                navController = navController,
                loginViewModel = loginViewModel
            )
        }
        composable("contacto_form?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            ContactoFormScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                contactoId = id
            )
        }
        composable("seguimiento_form/{oportunidadId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("oportunidadId")?.toLongOrNull() ?: 0L
            SeguimientoFormScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                oportunidadId = id
            )
        }
        composable("activaciones") {
            ActivacionesScreen(
                navController = navController,
                loginViewModel = loginViewModel
            )
        }
        composable("activacion_form?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            ActivacionFormScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                activacionId = id
            )
        }
        composable("resultados") {
            ResultadosScreen(
                navController = navController,
                loginViewModel = loginViewModel
            )
        }
        composable("resultado_form?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            ResultadoFormScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                resultadoId = id
            )
        }
        composable("catalogo") {
            val catalogoViewModel: CatalogoViewModel = viewModel()
            val usuario by loginViewModel.usuario.collectAsState()
            CatalogoScreen(
                viewModel = catalogoViewModel,
                usuario = usuario,
                onVolver = { navController.popBackStack() }
            )
        }
        composable("alertas") {
            AlertasScreen(
                navController = navController,
                loginViewModel = loginViewModel
            )
        }
        composable("usuarios") {
            UsuariosScreen(
                navController = navController,
                loginViewModel = loginViewModel
            )
        }
        composable("oportunidad_form?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            OportunidadFormScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                oportunidadId = id
            )
        }
        composable("qr_scanner?destino={destino}") { backStackEntry ->
            val destino = backStackEntry.arguments?.getString("destino") ?: ""
            QrScannerScreen(
                navController = navController,
                hasCameraPermission = hasCameraPermission,
                onRequestPermission = onRequestPermission,
                onQrScanned = { contenido ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("qr_result_$destino", contenido)
                }
            )
        }

        composable("oportunidad_detalle/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            OportunidadDetalleScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                oportunidadId = id
            )
        }

        composable("organizacion_form?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            OrganizacionFormScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                organizacionId = id
            )
        }

    } // fin NavHost

} // fin AppNav