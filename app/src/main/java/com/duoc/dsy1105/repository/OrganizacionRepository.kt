package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.OrganizacionDao
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.database.UsuarioDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Organizaciones. Todos pueden consultarlas; las operaciones que modifican datos
 * validan el permiso AQUÍ y devuelven null si salió bien, o el mensaje de error.
 */
class OrganizacionRepository(
    private val dao: OrganizacionDao,
    private val usuarioDao: UsuarioDao
    // EN PAUSA (comprobar si esta en uso): private val eventoDao: EventoDao, private val contactoDao: ContactoDao
) {

    // Uso normal de la app: los DAOs salen de la base de datos real.
    // En las pruebas se pasan DAOs falsos al constructor principal.
    constructor(context: Context) : this(
        AppDatabase.getDatabase(context).organizacionDao(),
        AppDatabase.getDatabase(context).usuarioDao()
        // EN PAUSA (comprobar si esta en uso): eventoDao() y contactoDao()
    )

    fun obtenerTodas(): Flow<List<OrganizacionEntity>> {
        return dao.obtenerTodas()
    } // fin obtenerTodas

    fun obtenerPorId(id: Long): Flow<OrganizacionEntity?> {
        return dao.obtenerPorId(id)
    } // fin obtenerPorId

    // Solo el ejecutivo registra organizaciones; queda anotado quién la creó
    suspend fun insertar(solicitanteId: Long, organizacion: OrganizacionEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeCrearOrganizaciones) {
            return MENSAJE_SIN_PERMISO
        }
        dao.insertar(organizacion.copy(creadoPorId = solicitante.id))
        return null
    } // fin insertar

    // Ejecutivo y supervisor pueden editar (el creador original no cambia)
    suspend fun actualizar(solicitanteId: Long, organizacion: OrganizacionEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeEditarOrganizaciones) {
            return MENSAJE_SIN_PERMISO
        }
        val actual = dao.obtenerPorId(organizacion.id).first() ?: return MENSAJE_NO_EXISTE
        dao.actualizar(organizacion.copy(creadoPorId = actual.creadoPorId))
        return null
    } // fin actualizar

    // Solo el supervisor elimina (en pausa: el creador elimina lo suyo y no se elimina lo que está en uso)
    suspend fun eliminar(solicitanteId: Long, organizacion: OrganizacionEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId) ?: return MENSAJE_SIN_PERMISO
        val actual = dao.obtenerPorId(organizacion.id).first() ?: return MENSAJE_NO_EXISTE

        if (!solicitante.perfil.puedeEliminarRegistro(actual.creadoPorId, solicitante.id)) {
            return MENSAJE_SIN_PERMISO
        }

        // EN PAUSA: no eliminar una organizacion que tiene eventos o contactos
        // val eventos = eventoDao.contarPorOrganizacion(actual.id)
        // val contactos = contactoDao.contarPorOrganizacion(actual.id)
        // if (eventos + contactos > 0) {
        //     return "No se puede eliminar: la usan $eventos evento(s) y $contactos contacto(s)"
        // }

        dao.eliminar(actual)
        return null
    } // fin eliminar

} // fin OrganizacionRepository
