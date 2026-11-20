package com.duoc.dsy1105.model

/**
 * Estados de gestión de una oportunidad (los cuatro de la lámina del profesor).
 * El supervisor gestiona el estado; el ejecutivo la deja en "En gestión" al registrar su primera gestión.
 */
object EstadosOportunidad {
    const val PENDIENTE = "Pendiente"
    const val EN_GESTION = "En gestión"
    const val CONFIRMADA = "Confirmada"
    const val DESCARTADA = "Descartada"

    val todos = listOf(
        PENDIENTE,
        EN_GESTION,
        CONFIRMADA,
        DESCARTADA
    )

    // EN PAUSA: los siete estados del anexo del caso. Se dejan comentados por si el profesor
    // pide volver a ellos (Pendiente agrupaba los dos primeros; En gestión, los tres siguientes).
    // const val IDENTIFICADO = "Evento identificado"
    // const val CONTACTO_PENDIENTE = "Contacto pendiente"
    // const val CONTACTO_REALIZADO = "Contacto realizado"
    // const val PROPUESTA_ENVIADA = "Propuesta enviada"
    // const val EN_ESPERA = "En espera de respuesta"
    // const val CONFIRMADA = "Participación confirmada"
    // const val DESCARTADA = "Oportunidad descartada"
} // fin object EstadosOportunidad

/**
 * Perfiles de usuario (los tres flujos de la lámina del profesor).
 *
 * En la base de datos se guarda la [etiqueta] (texto). Todo texto desconocido se trata como
 * EJECUTIVO (el perfil con menos privilegios), nunca como supervisor.
 *
 * Los permisos viven aquí para que UI, ViewModel y repositorio consulten la misma regla.
 * La pantalla los usa para mostrar u ocultar opciones; los repositorios los usan para
 * rechazar la operación aunque la pantalla no la hubiera ocultado.
 */
enum class Perfil(val etiqueta: String) {
    EJECUTIVO("Ejecutivo Comercial"),
    SUPERVISOR("Supervisor Comercial"),
    ACTIVACIONES("Gestión de Activaciones");

    // EN PAUSA: MARKETING("Marketing") fue el tercer perfil del caso. La lámina del profesor lo
    // reemplaza por "Gestión de Activaciones". Para volver atrás: descomentar esta línea y
    // revisar los `when` de abajo, que cubren los tres perfiles.

    // ---------- Usuarios ----------

    /** Solo el supervisor decide el perfil y el estado de las cuentas. */
    val puedeGestionarUsuarios: Boolean
        get() = this == SUPERVISOR

    // ---------- Oportunidades ----------

    /** El ejecutivo registra las oportunidades; quedan a su nombre. */
    val puedeCrearOportunidades: Boolean
        get() = this == EJECUTIVO

    /** Solo el supervisor asigna el responsable de una oportunidad. */
    val puedeAsignarResponsable: Boolean
        get() = this == SUPERVISOR

    /** Solo el supervisor gestiona el estado (Pendiente, En gestión, Confirmada, Descartada). */
    val puedeCambiarEstado: Boolean
        get() = this == SUPERVISOR

    // EN PAUSA (perfil Marketing): los materiales de la activación.
    // val puedeEditarMateriales: Boolean
    //     get() = this == MARKETING || this == SUPERVISOR

    /**
     * Ejecutivo: solo las suyas. Supervisor: todas.
     * Gestión de Activaciones: solo las confirmadas (ahí hay algo que coordinar).
     */
    fun puedeVerOportunidad(estado: String, responsableId: Long, usuarioId: Long): Boolean =
        when (this) {
            SUPERVISOR -> true
            EJECUTIVO -> esResponsable(responsableId, usuarioId)
            ACTIVACIONES -> estado == EstadosOportunidad.CONFIRMADA
        }

    /** Ejecutivo: las suyas. Supervisor: todas (puede corregir datos). Activaciones: no. */
    fun puedeEditarOportunidad(responsableId: Long, usuarioId: Long): Boolean =
        when (this) {
            SUPERVISOR -> true
            EJECUTIVO -> esResponsable(responsableId, usuarioId)
            ACTIVACIONES -> false
        }

    /** Las observaciones de una oportunidad las editan quienes pueden editarla. */
    fun puedeEditarObservaciones(estado: String, responsableId: Long, usuarioId: Long): Boolean =
        puedeEditarOportunidad(responsableId, usuarioId)

    /**
     * El ejecutivo elimina las suyas y el supervisor cualquiera (igual que puede corregirlas).
     * Al eliminar una oportunidad se elimina también su historial.
     */
    fun puedeEliminarOportunidad(responsableId: Long, usuarioId: Long): Boolean =
        when (this) {
            SUPERVISOR -> true
            EJECUTIVO -> esResponsable(responsableId, usuarioId)
            ACTIVACIONES -> false
        }

    /** Registrar una accion de seguimiento exige poder gestionar la oportunidad. */
    fun puedeRegistrarSeguimiento(responsableId: Long, usuarioId: Long): Boolean =
        puedeEditarOportunidad(responsableId, usuarioId)

    // ---------- Organizaciones ----------

    val puedeCrearOrganizaciones: Boolean
        get() = this == EJECUTIVO

    val puedeEditarOrganizaciones: Boolean
        get() = this == EJECUTIVO || this == SUPERVISOR


    // ---------- Eventos ----------
    // Todos consultan los eventos (Gestión de Activaciones revisa fechas y condiciones)

    val puedeCrearEventos: Boolean
        get() = this == EJECUTIVO

    val puedeEditarEventos: Boolean
        get() = this == EJECUTIVO || this == SUPERVISOR


    // ---------- Activaciones ----------
    // Las ven Gestión de Activaciones y el supervisor; solo Gestión de Activaciones las gestiona
    // (asigna el responsable de cada activación)

    val puedeVerActivaciones: Boolean
        get() = this == ACTIVACIONES || this == SUPERVISOR

    val puedeGestionarActivaciones: Boolean
        get() = this == ACTIVACIONES


    // ---------- Resultados de activaciones ----------
    // Los ven Gestión de Activaciones y el supervisor; solo Gestión de Activaciones los registra

    val puedeVerResultados: Boolean
        get() = this == ACTIVACIONES || this == SUPERVISOR

    val puedeRegistrarResultados: Boolean
        get() = this == ACTIVACIONES


    // ---------- Contactos ----------
    // Gestión de Activaciones no gestiona contactos comerciales: no ve este modulo

    val puedeVerContactos: Boolean
        get() = this != ACTIVACIONES

    val puedeCrearContactos: Boolean
        get() = this == EJECUTIVO

    val puedeEditarContactos: Boolean
        get() = this == EJECUTIVO || this == SUPERVISOR

    /**
     * Organizaciones, eventos y contactos: por ahora solo el supervisor elimina.
     * EN PAUSA: el ejecutivo eliminaba lo que creó y el repositorio no dejaba eliminar un
     * registro que otros usan (ver las lineas comentadas).
     */
    fun puedeEliminarRegistro(creadoPorId: Long, usuarioId: Long): Boolean =
        when (this) {
            SUPERVISOR -> true
            // EN PAUSA (el creador elimina lo suyo): EJECUTIVO -> esResponsable(creadoPorId, usuarioId)
            EJECUTIVO -> false
            ACTIVACIONES -> false
        }

    // Un usuarioId en 0 significa "sin sesión": nunca es responsable de nada
    private fun esResponsable(responsableId: Long, usuarioId: Long): Boolean =
        usuarioId != 0L && responsableId == usuarioId

    companion object {
        fun desdeEtiqueta(texto: String): Perfil =
            entries.firstOrNull { it.etiqueta == texto } ?: EJECUTIVO
    }
} // fin enum Perfil
