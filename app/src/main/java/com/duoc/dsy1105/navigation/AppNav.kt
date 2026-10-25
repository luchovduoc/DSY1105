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
import com.duoc.dsy1105.ui.screens.OportunidadFormScreen
import com.duoc.dsy1105.ui.screens.OportunidadesScreen
import com.duoc.dsy1105.ui.screens.OrganizacionesScreen
import com.duoc.dsy1105.ui.screens.RegistroScreen
import com.duoc.dsy1105.viewmodel.LoginViewModel


@Composable
fun AppNav() {
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

        composable("registro") {
            RegistroScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }


    }
}