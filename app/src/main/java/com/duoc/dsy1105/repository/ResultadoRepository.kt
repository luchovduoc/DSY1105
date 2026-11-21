package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.ResultadoDao
import com.duoc.dsy1105.database.ResultadoEntity
import com.duoc.dsy1105.database.UsuarioDao
import kotlinx.coroutines.flow.Flow

/**
 * Resultados de activaciones. Los ven Gestión de Activaciones y el supervisor; solo Gestión
 * de Activaciones los registra. El permiso se valida AQUÍ y se devuelve null si salió bien,
 * o el mensaje de error.
 */
class ResultadoRepository(
    private val dao: ResultadoDao,
    private val usuarioDao: UsuarioDao
) {

    constructor(context: Context) : this(
        AppDatabase.getDatabase(context).resultadoDao(),
        AppDatabase.getDatabase(context).usuarioDao()
    )

    fun obtenerTodos(): Flow<List<ResultadoEntity>> = dao.obtenerTodos()

    fun obtenerPorId(id: Long): Flow<ResultadoEntity?> = dao.obtenerPorId(id)

    suspend fun insertar(solicitanteId: Long, resultado: ResultadoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeRegistrarResultados) {
            return MENSAJE_SIN_PERMISO
        }
        dao.insertar(resultado)
        return null
    } // fin insertar

    suspend fun actualizar(solicitanteId: Long, resultado: ResultadoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeRegistrarResultados) {
            return MENSAJE_SIN_PERMISO
        }
        dao.actualizar(resultado)
        return null
    } // fin actualizar

    suspend fun eliminar(solicitanteId: Long, resultado: ResultadoEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeRegistrarResultados) {
            return MENSAJE_SIN_PERMISO
        }
        dao.eliminar(resultado)
        return null
    } // fin eliminar

} // fin ResultadoRepository
