package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.SeguimientoEntity
import com.duoc.dsy1105.model.EstadosOportunidad
import com.duoc.dsy1105.utils.Fechas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Seguimientos (historial de gestiones). Todos los perfiles que ven una oportunidad ven su
 * historial; registrar una acción exige poder gestionar esa oportunidad (el permiso se
 * valida AQUÍ). Devuelve null si salió bien, o el mensaje de error.
 */
class SeguimientoRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.seguimientoDao()
    private val oportunidadDao = db.oportunidadDao()
    private val contactoDao = db.contactoDao()
    private val usuarioDao = db.usuarioDao()

    fun obtenerPorOportunidad(oportunidadId: Long): Flow<List<SeguimientoEntity>> =
        dao.obtenerPorOportunidad(oportunidadId)

    fun obtenerTodos(): Flow<List<SeguimientoEntity>> = dao.obtenerTodos()

    suspend fun insertar(solicitanteId: Long, seguimiento: SeguimientoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId) ?: return MENSAJE_SIN_PERMISO
        val oportunidad = oportunidadDao.buscarPorId(seguimiento.oportunidadId)
            ?: return "La oportunidad no existe"

        if (!solicitante.perfil.puedeRegistrarSeguimiento(oportunidad.responsableId, solicitante.id)) {
            return MENSAJE_SIN_PERMISO
        }

        val contacto = if (seguimiento.contactoId != 0L) {
            contactoDao.obtenerPorId(seguimiento.contactoId).first()
        } else null

        dao.insertar(
            seguimiento.copy(
                usuarioId = solicitante.id,
                usuarioNombre = solicitante.nombre,
                contactoNombre = contacto?.nombre ?: "",
                registradoEl = Fechas.ahora(),
                automatico = false
            )
        )

        // La primera gestion pasa la oportunidad de Pendiente a En gestion
        if (oportunidad.estado == EstadosOportunidad.PENDIENTE) {
            oportunidadDao.actualizar(oportunidad.copy(estado = EstadosOportunidad.EN_GESTION))
        }

        // Deja registrada la fecha del ultimo contacto en la ficha del contacto
        if (contacto != null) {
            contactoDao.actualizarUltimoContacto(contacto.id, seguimiento.fecha)
        }
        return null
    } // fin insertar

} // fin SeguimientoRepository
