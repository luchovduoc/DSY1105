package com.duoc.dsy1105.utils

import com.duoc.dsy1105.database.ContactoEntity
import com.duoc.dsy1105.database.EventoEntity
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.model.FiltrosContacto
import com.duoc.dsy1105.model.FiltrosEvento
import com.duoc.dsy1105.model.FiltrosOportunidad

/**
 * Filtros de las listas. Son funciones puras (reciben la lista y los criterios y devuelven
 * la lista filtrada), así se pueden probar sin base de datos.
 */
object Filtros {

    // Busqueda de texto: sin importar mayusculas, en cualquiera de los campos
    private fun coincide(texto: String, vararg campos: String): Boolean =
        texto.isBlank() || campos.any { it.contains(texto.trim(), ignoreCase = true) }

    fun oportunidades(
        lista: List<OportunidadEntity>,
        filtros: FiltrosOportunidad,
        eventos: List<EventoEntity>
    ): List<OportunidadEntity> =
        lista.filter { o ->
            coincide(filtros.texto, o.nombre, o.descripcion, o.fuente) &&
                (filtros.estado.isBlank() || o.estado == filtros.estado) &&
                (filtros.tipo.isBlank() || o.tipoOportunidad == filtros.tipo) &&
                (filtros.prioridad.isBlank() || o.nivelPrioridad == filtros.prioridad) &&
                (filtros.responsableId == 0L || o.responsableId == filtros.responsableId) &&
                (filtros.region.isBlank() ||
                    eventos.find { it.id == o.eventoId }?.region == filtros.region)
        }

    fun eventos(lista: List<EventoEntity>, filtros: FiltrosEvento): List<EventoEntity> =
        lista.filter { e ->
            coincide(filtros.texto, e.nombre, e.descripcion, e.comuna) &&
                (filtros.tipo.isBlank() || e.tipo == filtros.tipo) &&
                (filtros.region.isBlank() || e.region == filtros.region) &&
                coincideFecha(e, filtros.fecha)
        }

    // Un evento sigue "proximo" hasta que termina (la fecha de termino, o la de inicio si no tiene)
    private fun coincideFecha(evento: EventoEntity, fecha: String): Boolean {
        if (fecha.isBlank()) return true
        val diasFin = Fechas.diasDesdeHoy(evento.fechaTermino.ifBlank { evento.fechaInicio })
            ?: return fecha == FiltrosEvento.FECHA_PROXIMOS
        return if (fecha == FiltrosEvento.FECHA_PROXIMOS) diasFin >= 0 else diasFin < 0
    }

    fun contactos(lista: List<ContactoEntity>, filtros: FiltrosContacto): List<ContactoEntity> =
        lista.filter { c ->
            coincide(filtros.texto, c.nombre, c.cargo, c.email) &&
                (filtros.area.isBlank() || c.area == filtros.area) &&
                (filtros.estado.isBlank() || c.estado == filtros.estado)
        }

    fun organizaciones(lista: List<OrganizacionEntity>, texto: String): List<OrganizacionEntity> =
        lista.filter { o -> coincide(texto, o.nombre, o.rubro, o.comuna) }

} // fin object Filtros
