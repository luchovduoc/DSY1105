package com.duoc.dsy1105.repository

import com.duoc.dsy1105.database.UsuarioDao
import com.duoc.dsy1105.model.Perfil

/**
 * Quien pide una operación, leído desde la base de datos (no desde la sesión en memoria),
 * para que un perfil desactualizado no pueda saltarse los permisos.
 */
data class Solicitante(
    val id: Long,
    val nombre: String,
    val perfil: Perfil
)

// Devuelve null si el usuario no existe o su cuenta está desactivada
suspend fun UsuarioDao.buscarSolicitante(id: Long): Solicitante? {
    val usuario = buscarPorId(id) ?: return null
    if (!usuario.activo) return null
    return Solicitante(usuario.id, usuario.nombre, Perfil.desdeEtiqueta(usuario.perfil))
} // fin buscarSolicitante

const val MENSAJE_SIN_PERMISO = "No tienes permiso para realizar esta acción"
const val MENSAJE_NO_EXISTE = "El registro no existe"
