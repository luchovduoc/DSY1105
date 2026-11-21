package com.duoc.dsy1105.model

/** Tipos de acción de seguimiento que define el caso DSY1105. */
object TiposAccion {

    const val ENVIO_PROPUESTA = "Envío de propuesta"

    // Los que registra una persona
    val manuales = listOf(
        "Investigación inicial",
        "Búsqueda de contacto",
        "Primer contacto",
        "Envío de presentación",
        ENVIO_PROPUESTA,
        "Solicitud de antecedentes",
        "Reunión",
        "Llamada",
        "Seguimiento",
        "Recepción de respuesta",
        "Confirmación",
        "Cierre"
    )

    // Los que genera la aplicación sola, para que el historial quede completo
    // EN PAUSA: tipos de los registros automaticos del historial
    // const val REGISTRO = "Registro"
    // const val CAMBIO_ESTADO = "Cambio de estado"
} // fin object TiposAccion
