package com.duoc.dsy1105.backend

// Las mismas reglas de Perfil de la app, para validarlas también en el servidor
object Permisos {
    private const val EJECUTIVO = "Ejecutivo Comercial"
    private const val SUPERVISOR = "Supervisor Comercial"

    fun puedeCrear(perfil: String) = perfil == EJECUTIVO

    fun puedeEditar(perfil: String) = perfil == EJECUTIVO || perfil == SUPERVISOR

    // Solo el supervisor elimina (igual que la app)
    // EN PAUSA (el creador elimina lo suyo): || (perfil == EJECUTIVO && usuarioId != 0L && creadoPorId == usuarioId)
    fun puedeEliminar(perfil: String, creadoPorId: Long, usuarioId: Long) =
        perfil == SUPERVISOR
}
