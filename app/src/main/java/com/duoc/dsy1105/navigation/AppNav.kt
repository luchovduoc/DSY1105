package com.duoc.dsy1105.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.dsy1105.ui.screens.ContactosScreen
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
            OportunidadesScreen(navController = navController)
        }
        composable("eventos") {
            EventosScreen(navController = navController)
        }
        composable("organizaciones") {
            OrganizacionesScreen(navController = navController)
        }
        composable("contactos") {
            ContactosScreen(navController = navController)
        }
        composable("oportunidad_form?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            OportunidadFormScreen(
                navController = navController,
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
                oportunidadId = id
            )
        }

        composable("organizacion_form") {
            OrganizacionFormScreen(navController = navController)
        }

    } // fin NavHost

} // fin AppNav