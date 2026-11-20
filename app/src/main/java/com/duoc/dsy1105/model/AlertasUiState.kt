package com.duoc.dsy1105.model

import com.duoc.dsy1105.database.EventoEntity
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.database.SeguimientoEntity

/** Un seguimiento pendiente: [dias] negativo = atrasado, 0 = hoy, positivo = faltan esos días. */
data class AlertaSeguimiento(
    val oportunidad: OportunidadEntity,
    val seguimiento: SeguimientoEntity,
    val dias: Int
) // fin data class AlertaSeguimiento

/** Avisos internos que se calculan con los datos que el perfil puede ver. */
data class AlertasUiState(
    val seguimientosAtrasados: List<AlertaSeguimiento> = emptyList(),
    val seguimientosProximos: List<AlertaSeguimiento> = emptyList(),
    val eventosProximos: List<EventoEntity> = emptyList(),
    val propuestasSinRespuesta: List<OportunidadEntity> = emptyList(),
    val oportunidadesSinContacto: List<OportunidadEntity> = emptyList(),
    val informacionIncompleta: List<OportunidadEntity> = emptyList(),
    val sinResponsable: List<OportunidadEntity> = emptyList()
) {
    val total: Int
        get() = seguimientosAtrasados.size +
            seguimientosProximos.size +
            eventosProximos.size +
            propuestasSinRespuesta.size +
            oportunidadesSinContacto.size +
            informacionIncompleta.size +
            sinResponsable.size
} // fin data class AlertasUiState
