package com.duoc.dsy1105.model

// Errores por campo del registro: null = campo válido
data class RegistroErrores(
    val nombre: String? = null,
    val email: String? = null,
    val clave: String? = null,
    val confirmarClave: String? = null
) // fin data class RegistroErrores

// Estado completo de la pantalla de registro
data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val errores: RegistroErrores = RegistroErrores(),
    // Error general (por ejemplo, email ya registrado)
    val error: String? = null,
    val registroExitoso: Boolean = false
) // fin data class RegistroUiState
