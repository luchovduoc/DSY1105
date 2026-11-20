package com.duoc.dsy1105.model

import com.duoc.dsy1105.utils.Fechas

// Errores del formulario: null = campo válido, texto = mensaje que se muestra al usuario
data class SeguimientoErrores(
    val tipoAccion: String? = null,
    val fecha: String? = null,
    val fechaProximo: String? = null
) // fin data class SeguimientoErrores

// Estado completo del formulario de seguimiento: valores + errores
data class SeguimientoUiState(
    val tipoAccion: String = "",
    val fecha: String = Fechas.hoy(), // por defecto, hoy
    val contactoId: Long = 0, // 0 = sin contacto
    val resultado: String = "",
    val proximaAccion: String = "",
    val fechaProximoSeguimiento: String = "",
    val observaciones: String = "",
    val errores: SeguimientoErrores = SeguimientoErrores(),
    // Error general (por ejemplo, falta de permiso al guardar)
    val error: String? = null,
    val guardadoExitosamente: Boolean = false
) // fin data class SeguimientoUiState
