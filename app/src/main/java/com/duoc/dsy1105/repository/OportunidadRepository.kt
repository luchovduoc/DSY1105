package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.utils.Fechas
import kotlinx.coroutines.flow.Flow

/**
 * Oportunidades. Las operaciones que modifican datos validan el permiso AQUÍ
 * (no solo en la pantalla) y devuelven null si salió bien, o el mensaje de error.
 */
class OportunidadRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.oportunidadDao()
    private val usuarioDao = db.usuarioDao()
    private val seguimientoDao = db.seguimientoDao()

    fun obtenerTodas(): Flow<List<OportunidadEntity>> {
        return dao.obtenerTodas()
    }


    fun obtenerPorId(id: Long): Flow<OportunidadEntity?> {
        return dao.obtenerPorId(id)
    } // fin obtenerPorId

    // Solo el ejecutivo crea; la oportunidad queda a su nombre
    suspend fun insertar(solicitanteId: Long, oportunidad: OportunidadEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId)
        if (solicitante == null || !solicitante.perfil.puedeCrearOportunidades) {
            return MENSAJE_SIN_PERMISO
        }

        dao.insertar( // EN PAUSA (rastro automatico): antes se guardaba en val nuevoId
            oportunidad.copy(
                responsableId = solicitante.id,
                fechaDeteccion = Fechas.hoy(),
                modificadoPor = solicitante.nombre,
                fechaModificacion = Fechas.ahora()
            )
        )
        // EN PAUSA (rastro automatico): registrarAutomatico(solicitante, nuevoId, TiposAccion.REGISTRO, "Oportunidad registrada")
        return null
    } // fin insertar

    /**
     * Lo que se puede cambiar depende del perfil:
     * - Supervisor: todo (incluye responsable y estado).
     * - Ejecutivo responsable: todo menos el responsable y el estado.
     */
    suspend fun actualizar(solicitanteId: Long, oportunidad: OportunidadEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId) ?: return MENSAJE_SIN_PERMISO
        val actual = dao.buscarPorId(oportunidad.id) ?: return MENSAJE_NO_EXISTE
        val perfil = solicitante.perfil

        val resultado = when {
            perfil == Perfil.SUPERVISOR -> oportunidad

            perfil.puedeEditarOportunidad(actual.responsableId, solicitante.id) ->
                oportunidad.copy(
                    responsableId = actual.responsableId,
                    estado = actual.estado
                )

            // EN PAUSA (perfil Marketing): editaba solo materiales y observaciones de las confirmadas
            // perfil.puedeEditarMateriales &&
            //     perfil.puedeVerOportunidad(actual.estado, actual.responsableId, solicitante.id) ->
            //     actual.copy(
            //         materiales = oportunidad.materiales,
            //         observaciones = oportunidad.observaciones
            //     )

            else -> return MENSAJE_SIN_PERMISO
        }

        dao.actualizar(
            resultado.copy(
                // La fecha de deteccion no cambia al editar
                fechaDeteccion = actual.fechaDeteccion,
                modificadoPor = solicitante.nombre,
                fechaModificacion = Fechas.ahora()
            )
        )

        // EN PAUSA: rastro automatico de cambios de estado
        // // Deja constancia en el historial cuando cambia el estado
        // if (actual.estado != resultado.estado) {
        //     registrarAutomatico(
        //         solicitante,
        //         actual.id,
        //         TiposAccion.CAMBIO_ESTADO,
        //         "De \"${actual.estado}\" a \"${resultado.estado}\""
        //     )
        // }
        return null
    } // fin actualizar

    suspend fun eliminar(solicitanteId: Long, oportunidad: OportunidadEntity): String? {
        val solicitante = usuarioDao.buscarSolicitante(solicitanteId) ?: return MENSAJE_SIN_PERMISO
        val actual = dao.buscarPorId(oportunidad.id) ?: return MENSAJE_NO_EXISTE

        if (!solicitante.perfil.puedeEliminarOportunidad(actual.responsableId, solicitante.id)) {
            return MENSAJE_SIN_PERMISO
        }

        seguimientoDao.eliminarDeOportunidad(actual.id)
        dao.eliminar(actual)
        return null
    } // fin eliminar

    // EN PAUSA: registros automaticos del historial
    // // Registro que genera la app sola, para que el historial quede completo
    // private suspend fun registrarAutomatico(
    //     solicitante: Solicitante,
    //     oportunidadId: Long,
    //     tipo: String,
    //     resultado: String
    // ) {
    //     seguimientoDao.insertar(
    //         SeguimientoEntity(
    //             oportunidadId = oportunidadId,
    //             usuarioId = solicitante.id,
    //             usuarioNombre = solicitante.nombre,
    //             tipoAccion = tipo,
    //             fecha = Fechas.hoy(),
    //             registradoEl = Fechas.ahora(),
    //             resultado = resultado,
    //             automatico = true
    //         )
    //     )
    // } // fin registrarAutomatico

    private companion object {
        const val MENSAJE_NO_EXISTE = "La oportunidad no existe"
    }
}
