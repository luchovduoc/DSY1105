package com.duoc.dsy1105.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.dsy1105.ui.screens.EventosScreen
import com.duoc.dsy1105.ui.screens.HomeScreen
import com.duoc.dsy1105.ui.screens.LoginScreen
import com.duoc.dsy1105.ui.screens.OportunidadesScreen
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
    }
}