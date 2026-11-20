package com.duoc.dsy1105.model

// Errores del formulario: null = campo válido, texto = mensaje que se muestra al usuario
data class ContactoErrores(
    val nombre: String? = null,
    val organizacion: String? = null,
    val area: String? = null,
    val email: String? = null,
    val telefono: String? = null
) // fin data class ContactoErrores

// Estado completo del formulario de contactos: valores + errores
data class ContactoUiState(
    val nombre: String = "",
    val organizacionId: Long = 0, // 0 = sin seleccionar (es obligatoria)
    val cargo: String = "",
    val area: String = "",
    val email: String = "",
    val telefono: String = "",
    val medioContacto: String = "",
    val estado: String = "Activo",
    val observaciones: String = "",
    val errores: ContactoErrores = ContactoErrores(),
    // Error general (por ejemplo, falta de permiso al guardar)
    val error: String? = null,
    val guardadoExitosamente: Boolean = false
) // fin data class ContactoUiState
