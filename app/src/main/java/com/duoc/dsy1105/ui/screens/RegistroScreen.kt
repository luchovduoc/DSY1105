package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
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
    val error by viewModel.error.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()
    val nombre by viewModel.registroNombre.collectAsState()
    val email by viewModel.registroEmail.collectAsState()
    val clave by viewModel.registroClave.collectAsState()
    val confirmarClave by viewModel.registroConfirmarClave.collectAsState()

    val nombreError by viewModel.registroNombreError.collectAsState()
    val emailError by viewModel.registroEmailError.collectAsState()
    val claveError by viewModel.registroClaveError.collectAsState()
    val confirmarError by viewModel.registroConfirmarError.collectAsState()

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
                onValueChange = { viewModel.onRegistroNombreChange(it) },
                label = { Text("Nombre completo") },
                isError = nombreError != null,
                supportingText = {
                    nombreError?.let {
                        Text(it)
                    }
                },
                trailingIcon = {
                    if (nombreError != null) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = "Error"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onRegistroEmailChange(it) },
                label = { Text("Email") },
                isError = emailError != null,
                supportingText = {
                    emailError?.let {
                        Text(it)
                    }
                },
                trailingIcon = {
                    if (emailError != null) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = "Error"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Clave
            OutlinedTextField(
                value = clave,
                onValueChange = { viewModel.onRegistroClaveChange(it) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = claveError != null,
                supportingText = {
                    claveError?.let {
                        Text(it)
                    }
                },
                trailingIcon = {
                    if (claveError != null) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = "Error"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Confirmar clave
            OutlinedTextField(
                value = confirmarClave,
                onValueChange = { viewModel.onRegistroConfirmarClaveChange(it) },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = confirmarError != null,
                supportingText = {
                    confirmarError?.let {
                        Text(it)
                    }
                },
                trailingIcon = {
                    if (confirmarError != null) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = "Error"
                        )
                    }
                },
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
                    viewModel.registrarFormulario()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            } // fin Button

        } // fin Column

    } // fin Scaffold

} // fin RegistroScreen

