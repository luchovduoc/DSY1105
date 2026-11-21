package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.EventoDao
import com.duoc.dsy1105.database.EventoEntity
import com.duoc.dsy1105.database.UsuarioDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Eventos. Todos pueden consultarlos; las operaciones que modifican datos
 * validan el permiso AQUÍ y devuelven null si salió bien, o el mensaje de error.
 */
class EventoRepository(
    private val dao: EventoDao,
    private val usuarioDao: UsuarioDao
    // EN PAUSA (comprobar si esta en uso): private val oportunidadDao: OportunidadDao
) {

    // Uso normal de la app: los DAOs salen de la base de datos real.
    // En las pruebas se pasan DAOs falsos al constructor principal.
    constructor(context: Context) : this(
        AppDatabase.getDatabase(context).eventoDao(),
        AppDatabase.getDatabase(context).usuarioDao()
        // EN PAUSA (comprobar si esta en uso): oportunidadDao()
    )

    fun obtenerTodos(): Flow<List<EventoEntity>> = dao.obtenerTodos()

    fun obtenerPorId(id: Long): Flow<EventoEntity?> = dao.obtenerPorId(id)

    // Solo el ejecutivo registra eventos; queda anotado quién lo creó
    suspend fun insertar(solicitanteId: Long, evento: EventoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeCrearEventos) {
            return MENSAJE_SIN_PERMISO
        }
        dao.insertar(evento.copy(creadoPorId = solicitante.id))
        return null
    } // fin insertar

    // Ejecutivo y supervisor pueden editar (el creador original no cambia)
    suspend fun actualizar(solicitanteId: Long, evento: EventoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeEditarEventos) {
            return MENSAJE_SIN_PERMISO
        }
        val actual = dao.obtenerPorId(evento.id).first() ?: return MENSAJE_NO_EXISTE
        dao.actualizar(evento.copy(creadoPorId = actual.creadoPorId))
        return null
    } // fin actualizar

    // Solo el supervisor elimina (en pausa: el creador elimina lo suyo y no se elimina lo que está en uso)
    suspend fun eliminar(solicitanteId: Long, evento: EventoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId) ?: return MENSAJE_SIN_PERMISO
        val actual = dao.obtenerPorId(evento.id).first() ?: return MENSAJE_NO_EXISTE

        if (!solicitante.perfil.puedeEliminarRegistro(actual.creadoPorId, solicitante.id)) {
            return MENSAJE_SIN_PERMISO
        }

        // EN PAUSA: no eliminar un evento que tiene oportunidades
        // val oportunidades = oportunidadDao.contarPorEvento(actual.id)
        // if (oportunidades > 0) {
        //     return "No se puede eliminar: la usan $oportunidades oportunidad(es)"
        // }

        dao.eliminar(actual)
        return null
    } // fin eliminar

} // fin EventoRepository
