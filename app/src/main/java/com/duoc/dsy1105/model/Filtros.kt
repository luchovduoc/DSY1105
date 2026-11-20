package com.duoc.dsy1105.model

// Filtros de las listas. Un valor vacío ("" o 0) significa "sin filtrar por ese criterio".

data class FiltrosOportunidad(
    val texto: String = "",
    val estado: String = "",
    val tipo: String = "",
    val prioridad: String = "",
    val region: String = "", // región del evento asociado
    val responsableId: Long = 0
) {
    val hayFiltros: Boolean
        get() = texto.isNotBlank() || estado.isNotBlank() || tipo.isNotBlank() ||
            prioridad.isNotBlank() || region.isNotBlank() || responsableId != 0L
} // fin data class FiltrosOportunidad

data class FiltrosEvento(
    val texto: String = "",
    val tipo: String = "",
    val region: String = "",
    val fecha: String = "" // "", FECHA_PROXIMOS o FECHA_PASADOS
) {
    val hayFiltros: Boolean
        get() = texto.isNotBlank() || tipo.isNotBlank() || region.isNotBlank() || fecha.isNotBlank()

    companion object {
        const val FECHA_PROXIMOS = "Próximos"
        const val FECHA_PASADOS = "Pasados"
        val opcionesFecha = listOf(FECHA_PROXIMOS, FECHA_PASADOS)
    }
} // fin data class FiltrosEvento

data class FiltrosContacto(
    val texto: String = "",
    val area: String = "",
    val estado: String = ""
) {
    val hayFiltros: Boolean
        get() = texto.isNotBlank() || area.isNotBlank() || estado.isNotBlank()
} // fin data class FiltrosContacto
