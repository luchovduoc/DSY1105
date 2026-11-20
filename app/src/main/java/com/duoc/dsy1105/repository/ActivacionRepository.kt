package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.ActivacionDao
import com.duoc.dsy1105.database.ActivacionEntity
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.UsuarioDao
import kotlinx.coroutines.flow.Flow

/**
 * Activaciones. Las ven Gestión de Activaciones y el supervisor; solo Gestión de Activaciones
 * las crea, edita y elimina. El permiso se valida AQUÍ y se devuelve null si salió bien,
 * o el mensaje de error.
 */
class ActivacionRepository(
    private val dao: ActivacionDao,
    private val usuarioDao: UsuarioDao
) {

    constructor(context: Context) : this(
        AppDatabase.getDatabase(context).activacionDao(),
        AppDatabase.getDatabase(context).usuarioDao()
    )

    fun obtenerTodas(): Flow<List<ActivacionEntity>> = dao.obtenerTodas()

    fun obtenerPorId(id: Long): Flow<ActivacionEntity?> = dao.obtenerPorId(id)

    suspend fun insertar(solicitanteId: Long, activacion: ActivacionEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeGestionarActivaciones) {
            return MENSAJE_SIN_PERMISO
        }
        dao.insertar(activacion)
        return null
    } // fin insertar

    suspend fun actualizar(solicitanteId: Long, activacion: ActivacionEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeGestionarActivaciones) {
            return MENSAJE_SIN_PERMISO
        }
        dao.actualizar(activacion)
        return null
    } // fin actualizar

    suspend fun eliminar(solicitanteId: Long, activacion: ActivacionEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeGestionarActivaciones) {
            return MENSAJE_SIN_PERMISO
        }
        dao.eliminar(activacion)
        return null
    } // fin eliminar

} // fin ActivacionRepository
