package com.duoc.dsy1105.utils

import com.duoc.dsy1105.database.EventoEntity
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.database.SeguimientoEntity
import com.duoc.dsy1105.model.AlertaSeguimiento
import com.duoc.dsy1105.model.AlertasUiState
import com.duoc.dsy1105.model.EstadosOportunidad
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.model.TiposAccion

/**
 * Calcula los avisos internos (recordatorios) del caso DSY1105. Es una función pura:
 * recibe los datos y devuelve las alertas, sin tocar la base de datos, así se puede probar.
 *
 * Solo considera las oportunidades que el perfil puede ver.
 */
object GeneradorAlertas {

    const val DIAS_EVENTOS = 30 // eventos que empiezan en los próximos 30 días
    const val DIAS_SEGUIMIENTOS = 7 // seguimientos que vencen en los próximos 7 días

    fun generar(
        perfil: Perfil,
        usuarioId: Long,
        oportunidades: List<OportunidadEntity>,
        seguimientos: List<SeguimientoEntity>,
        eventos: List<EventoEntity>
    ): AlertasUiState {

        // Las descartadas ya no requieren gestión
        val visibles = oportunidades.filter {
            perfil.puedeVerOportunidad(it.estado, it.responsableId, usuarioId) &&
                it.estado != EstadosOportunidad.DESCARTADA
        }

        // Eventos que empiezan pronto (del más cercano al más lejano)
        val eventosProximos = eventos
            .mapNotNull { evento ->
                Fechas.diasDesdeHoy(evento.fechaInicio)
                    ?.takeIf { it in 0..DIAS_EVENTOS }
                    ?.let { evento to it }
            }
            .sortedBy { it.second }
            .map { it.first }

        // La última acción registrada por el equipo define el próximo seguimiento pendiente
        val ultimaAccion = seguimientos
            .filter { !it.automatico }
            .groupBy { it.oportunidadId }
            .mapValues { (_, lista) -> lista.maxByOrNull { it.id } }

        val conProximoSeguimiento = visibles.mapNotNull { oportunidad ->
            val ultimo = ultimaAccion[oportunidad.id] ?: return@mapNotNull null
            val dias = Fechas.diasDesdeHoy(ultimo.fechaProximoSeguimiento)
                ?: return@mapNotNull null
            AlertaSeguimiento(oportunidad, ultimo, dias)
        }

        return AlertasUiState(
            seguimientosAtrasados = conProximoSeguimiento
                .filter { it.dias < 0 }
                .sortedBy { it.dias },
            seguimientosProximos = conProximoSeguimiento
                .filter { it.dias in 0..DIAS_SEGUIMIENTOS }
                .sortedBy { it.dias },
            eventosProximos = eventosProximos,
            // Con solo cuatro estados, la propuesta sin respuesta se deduce del historial:
            // la ultima accion registrada fue enviar la propuesta
            propuestasSinRespuesta = visibles.filter {
                ultimaAccion[it.id]?.tipoAccion == TiposAccion.ENVIO_PROPUESTA
            },
            oportunidadesSinContacto = visibles.filter {
                it.estado == EstadosOportunidad.PENDIENTE
            },
            informacionIncompleta = visibles.filter { it.eventoId == 0L },
            // Solo el supervisor asigna responsables, así que solo a él le sirve este aviso
            sinResponsable = if (perfil.puedeAsignarResponsable) {
                visibles.filter { it.responsableId == 0L }
            } else {
                emptyList()
            }
        )
    } // fin generar

} // fin object GeneradorAlertas
