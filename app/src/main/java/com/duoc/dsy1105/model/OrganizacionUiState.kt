package com.duoc.dsy1105.model

// Errores del formulario: null = campo válido, texto = mensaje que se muestra al usuario
data class OrganizacionErrores(
    val nombre: String? = null,
    val tipo: String? = null,
    val rubro: String? = null,
    val region: String? = null,
    val comuna: String? = null
) // fin data class OrganizacionErrores

// Estado completo del formulario de organizaciones: valores + errores
data class OrganizacionUiState(
    val nombre: String = "",
    val tipoOrganizacion: String = "",
    val rubro: String = "",
    val comuna: String = "",
    val region: String = "",
    val observaciones: String = "",
    val errores: OrganizacionErrores = OrganizacionErrores(),
    // Error general (por ejemplo, falta de permiso al guardar)
    val error: String? = null,
    val guardadoExitosamente: Boolean = false
) // fin data class OrganizacionUiState
