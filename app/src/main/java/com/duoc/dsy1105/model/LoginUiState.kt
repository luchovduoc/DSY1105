package com.duoc.dsy1105.model

// Errores por campo del login: null = campo válido
data class LoginErrores(
    val email: String? = null,
    val password: String? = null
) // fin data class LoginErrores

// Estado completo de la pantalla de login
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val errores: LoginErrores = LoginErrores(),
    // Error general (credenciales incorrectas, cuenta desactivada)
    val error: String? = null
) // fin data class LoginUiState
