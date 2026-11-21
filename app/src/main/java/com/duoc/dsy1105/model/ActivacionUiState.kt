package com.duoc.dsy1105.model

// Errores del formulario: null = campo válido, texto = mensaje que se muestra al usuario
data class ActivacionErrores(
    val oportunidad: String? = null,
    val responsable: String? = null,
    val fecha: String? = null,
    val lugar: String? = null
) // fin data class ActivacionErrores

// Estado completo del formulario de activaciones: valores + errores
data class ActivacionUiState(
    val oportunidadId: Long = 0,
    val responsableId: Long = 0,
    val fecha: String = "",
    val lugar: String = "",
    val observaciones: String = "",
    val errores: ActivacionErrores = ActivacionErrores(),
    // Error general (por ejemplo, falta de permiso al guardar)
    val error: String? = null,
    val guardadoExitosamente: Boolean = false
) // fin data class ActivacionUiState
