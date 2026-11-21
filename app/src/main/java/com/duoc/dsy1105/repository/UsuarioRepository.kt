package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.Perfil
import kotlinx.coroutines.flow.Flow

/**
 * Gestión de usuarios. Las reglas de permiso se validan AQUÍ (no solo en la UI):
 * el solicitante se vuelve a leer desde la base de datos, así un perfil
 * desactualizado en memoria no puede saltarse el control.
 *
 * Cada función devuelve null si todo salió bien, o el mensaje de error.
 */
class UsuarioRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.usuarioDao()
    // EN PAUSA (eliminar cuentas): private val oportunidadDao = db.oportunidadDao()
    // EN PAUSA (eliminar cuentas): private val seguimientoDao = db.seguimientoDao()

    fun obtenerTodos(): Flow<List<UsuarioEntity>> = dao.obtenerTodos()

    suspend fun cambiarPerfil(
        solicitanteId: Long,
        usuarioId: Long,
        nuevoPerfil: Perfil
    ): String? {
        validarSolicitante(solicitanteId)?.let { return it }

        val objetivo = dao.buscarPorId(usuarioId) ?: return MENSAJE_NO_EXISTE
        if (objetivo.perfil == nuevoPerfil.etiqueta) return null

        if (nuevoPerfil != Perfil.SUPERVISOR && esUltimoSupervisorActivo(objetivo)) {
            return MENSAJE_ULTIMO_SUPERVISOR
        }

        dao.actualizar(objetivo.copy(perfil = nuevoPerfil.etiqueta))
        return null
    } // fin cambiarPerfil

    suspend fun cambiarActivo(
        solicitanteId: Long,
        usuarioId: Long,
        activo: Boolean
    ): String? {
        validarSolicitante(solicitanteId)?.let { return it }

        val objetivo = dao.buscarPorId(usuarioId) ?: return MENSAJE_NO_EXISTE
        if (objetivo.activo == activo) return null

        if (!activo && esUltimoSupervisorActivo(objetivo)) {
            return MENSAJE_ULTIMO_SUPERVISOR
        }

        dao.actualizar(objetivo.copy(activo = activo))
        return null
    } // fin cambiarActivo

    // EN PAUSA: eliminar cuentas sin actividad
    // /**
    //  * Solo se elimina una cuenta SIN actividad (sin oportunidades a su cargo ni acciones
    //  * registradas), para no dejar huérfano el historial. Si tiene actividad, se desactiva.
    //  */
    // suspend fun eliminar(solicitanteId: Long, usuarioId: Long): String? {
    //     validarSolicitante(solicitanteId)?.let { return it }

    //     if (solicitanteId == usuarioId) return "No puedes eliminar tu propia cuenta"

    //     val objetivo = dao.buscarPorId(usuarioId) ?: return MENSAJE_NO_EXISTE
    //     if (esUltimoSupervisorActivo(objetivo)) return MENSAJE_ULTIMO_SUPERVISOR

    //     val oportunidades = oportunidadDao.contarPorResponsable(usuarioId)
    //     val acciones = seguimientoDao.contarPorUsuario(usuarioId)
    //     if (oportunidades + acciones > 0) {
    //         return "No se puede eliminar: tiene $oportunidades oportunidad(es) y $acciones acción(es) " +
    //             "registradas. Desactiva la cuenta en su lugar."
    //     }

    //     dao.eliminar(objetivo)
    //     return null
    // } // fin eliminar

    // Solo un supervisor activo puede gestionar usuarios
    private suspend fun validarSolicitante(solicitanteId: Long): String? {
        val solicitante = dao.buscarPorId(solicitanteId)
        val autorizado = solicitante != null &&
            solicitante.activo &&
            Perfil.desdeEtiqueta(solicitante.perfil).puedeGestionarUsuarios
        return if (autorizado) null else MENSAJE_SIN_PERMISO
    }

    // Evita dejar la aplicación sin ningún supervisor activo
    private suspend fun esUltimoSupervisorActivo(usuario: UsuarioEntity): Boolean {
        val esSupervisorActivo = usuario.activo &&
            Perfil.desdeEtiqueta(usuario.perfil) == Perfil.SUPERVISOR
        return esSupervisorActivo &&
            dao.contarActivosConPerfil(Perfil.SUPERVISOR.etiqueta) <= 1
    }

    private companion object {
        const val MENSAJE_SIN_PERMISO = "No tienes permiso para gestionar usuarios"
        const val MENSAJE_NO_EXISTE = "El usuario no existe"
        const val MENSAJE_ULTIMO_SUPERVISOR = "Debe existir al menos un supervisor activo"
    }

} // fin UsuarioRepository
