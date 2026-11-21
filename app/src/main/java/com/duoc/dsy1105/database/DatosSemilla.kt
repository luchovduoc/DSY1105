package com.duoc.dsy1105.database

/**
 * Usuarios FICTICIOS de demostración, uno por perfil (el caso exige datos ficticios).
 * Se crean solos al iniciar la app si no existen. Los usuarios que se registran nacen como
 * Ejecutivo y el supervisor les cambia el perfil desde "Gestión de usuarios".
 */
object DatosSemilla {
    const val CLAVE = "12345" // clave simple a propósito: son usuarios de prueba

    const val SUPERVISOR_NOMBRE = "Supervisor Demo"
    const val SUPERVISOR_EMAIL = "supervisor@agencia.cl"

    const val EJECUTIVO_NOMBRE = "Ejecutivo Demo"
    const val EJECUTIVO_EMAIL = "ejecutivo@agencia.cl"

    const val ACTIVACIONES_NOMBRE = "Activaciones Demo"
    const val ACTIVACIONES_EMAIL = "activaciones@agencia.cl"
} // fin object DatosSemilla
