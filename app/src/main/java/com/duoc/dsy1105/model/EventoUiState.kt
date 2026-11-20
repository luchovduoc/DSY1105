package com.duoc.dsy1105.model

// Errores del formulario: null = campo válido, texto = mensaje que se muestra al usuario
data class EventoErrores(
    val nombre: String? = null,
    val tipo: String? = null,
    val fechaInicio: String? = null,
    val fechaTermino: String? = null,
    val region: String? = null,
    val comuna: String? = null,
    val cantidad: String? = null
) // fin data class EventoErrores

// Estado completo del formulario de eventos: valores + errores
data class EventoUiState(
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "",
    val fechaTermino: String = "",
    val horario: String = "",
    val region: String = "",
    val comuna: String = "",
    val organizacionId: Long = 0,
    val publicoEsperado: String = "",
    val cantidadEstimada: String = "", // se escribe como texto y se valida como número
    val condiciones: String = "",
    val observaciones: String = "",
    val errores: EventoErrores = EventoErrores(),
    // Error general (por ejemplo, falta de permiso al guardar)
    val error: String? = null,
    val guardadoExitosamente: Boolean = false
) // fin data class EventoUiState
