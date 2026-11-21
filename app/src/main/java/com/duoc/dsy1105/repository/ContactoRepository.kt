package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.ContactoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Contactos. Las operaciones validan el permiso AQUÍ y devuelven null si salió bien,
 * o el mensaje de error. Gestión de Activaciones no tiene acceso a este módulo.
 */
class ContactoRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.contactoDao()
    private val usuarioDao = db.usuarioDao()
    // EN PAUSA (comprobar si esta en uso): private val seguimientoDao = db.seguimientoDao()

    fun obtenerTodos(): Flow<List<ContactoEntity>> = dao.obtenerTodos()

    fun obtenerPorId(id: Long): Flow<ContactoEntity?> = dao.obtenerPorId(id)

    // Solo el ejecutivo registra contactos; queda anotado quién lo creó
    suspend fun insertar(solicitanteId: Long, contacto: ContactoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeCrearContactos) {
            return MENSAJE_SIN_PERMISO
        }
        dao.insertar(contacto.copy(creadoPorId = solicitante.id))
        return null
    } // fin insertar

    // Ejecutivo y supervisor pueden editar (el creador original no cambia)
    suspend fun actualizar(solicitanteId: Long, contacto: ContactoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeEditarContactos) {
            return MENSAJE_SIN_PERMISO
        }
        val actual = dao.obtenerPorId(contacto.id).first() ?: return MENSAJE_NO_EXISTE
        dao.actualizar(contacto.copy(creadoPorId = actual.creadoPorId))
        return null
    } // fin actualizar

    // Solo el supervisor elimina (en pausa: el creador elimina lo suyo y no se elimina lo que está en uso)
    suspend fun eliminar(solicitanteId: Long, contacto: ContactoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId) ?: return MENSAJE_SIN_PERMISO
        val actual = dao.obtenerPorId(contacto.id).first() ?: return MENSAJE_NO_EXISTE

        if (!solicitante.perfil.puedeEliminarRegistro(actual.creadoPorId, solicitante.id)) {
            return MENSAJE_SIN_PERMISO
        }

        // EN PAUSA: no eliminar un contacto que figura en seguimientos
        // val seguimientos = seguimientoDao.contarPorContacto(actual.id)
        // if (seguimientos > 0) {
        //     return "No se puede eliminar: figura en $seguimientos seguimiento(s)"
        // }

        dao.eliminar(actual)
        return null
    } // fin eliminar

} // fin ContactoRepository
