package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duoc.dsy1105.viewmodel.LoginViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val usuario by viewModel.usuario.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = usuario?.nombre ?: "Usuario",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = usuario?.perfil ?: "",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar Sesión")
        }
    }
}

