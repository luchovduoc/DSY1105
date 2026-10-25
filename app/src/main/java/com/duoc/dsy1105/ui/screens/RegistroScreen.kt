package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duoc.dsy1105.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }
    var errorConfirmar by remember { mutableStateOf<String?>(null) }

    val error by viewModel.error.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()

    // Cuando el registro es exitoso vuelve al login
    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            viewModel.limpiarRegistro()
            navController.popBackStack()
        }
    } // fin LaunchedEffect

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
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

            Text(
                text = "Registro de usuario",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            // Clave
            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Confirmar clave
            OutlinedTextField(
                value = confirmarClave,
                onValueChange = {
                    confirmarClave = it
                    errorConfirmar = null
                },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = errorConfirmar != null,
                supportingText = { Text(errorConfirmar ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            // Error del repository
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            } // fin if error

            Spacer(modifier = Modifier.height(8.dp))

            // Boton registrar
            Button(
                onClick = {
                    if (clave != confirmarClave) {
                        errorConfirmar = "Las contraseñas no coinciden"
                        return@Button
                    }
                    if (nombre.isBlank() || email.isBlank() || clave.isBlank()) {
                        errorConfirmar = "Todos los campos son obligatorios"
                        return@Button
                    }
                    viewModel.registrar(nombre, email, clave)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            } // fin Button

        } // fin Column

    } // fin Scaffold

} // fin RegistroScreen

