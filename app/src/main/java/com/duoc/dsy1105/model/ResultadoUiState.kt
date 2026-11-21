package com.duoc.dsy1105.model

// Resultados posibles de una activación
object TiposResultado {
    const val EXITOSA = "Exitosa"
    const val PARCIAL = "Parcial"
    const val NO_LOGRADA = "No lograda"

    val todos = listOf(EXITOSA, PARCIAL, NO_LOGRADA)
} // fin TiposResultado

// Errores del formulario: null = campo válido, texto = mensaje que se muestra al usuario
data class ResultadoErrores(
    val activacion: String? = null,
    val resultado: String? = null,
    val contactos: String? = null
) // fin data class ResultadoErrores

// Estado completo del formulario de resultados: valores + errores
data class ResultadoUiState(
    val activacionId: Long = 0,
    val resultado: String = "",
    val contactosGenerados: String = "", // se escribe como texto y se valida como número
    val evidencia: String = "",
    val observaciones: String = "",
    val errores: ResultadoErrores = ResultadoErrores(),
    // Error general (por ejemplo, falta de permiso al guardar)
    val error: String? = null,
    val guardadoExitosamente: Boolean = false
) // fin data class ResultadoUiState
