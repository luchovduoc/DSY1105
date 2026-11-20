package com.duoc.dsy1105.model

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Pruebas de la capa model: los permisos que define Perfil.
 * Son Kotlin puro (no necesitan Android), así que corren en la JVM.
 */
class PerfilTest : StringSpec({

    // Un caso de la matriz: quién es, de quién es la oportunidad y qué debería poder hacer
    data class Caso(
        val descripcion: String,
        val perfil: Perfil,
        val responsableId: Long,
        val usuarioId: Long,
        val estado: String,
        val puedeVer: Boolean,
        val puedeEditar: Boolean,
        val puedeEliminar: Boolean
    )

    "la matriz de permisos sobre oportunidades respeta el perfil y el dueño" {
        val casos = listOf(
            Caso("ejecutivo sobre una oportunidad suya", Perfil.EJECUTIVO, 5, 5, EstadosOportunidad.PENDIENTE, true, true, true),
            Caso("ejecutivo sobre la de otro", Perfil.EJECUTIVO, 7, 5, EstadosOportunidad.PENDIENTE, false, false, false),
            Caso("ejecutivo sobre una sin responsable", Perfil.EJECUTIVO, 0, 5, EstadosOportunidad.PENDIENTE, false, false, false),
            Caso("supervisor sobre la de cualquiera", Perfil.SUPERVISOR, 7, 5, EstadosOportunidad.PENDIENTE, true, true, true),
            Caso("activaciones sobre una confirmada", Perfil.ACTIVACIONES, 7, 5, EstadosOportunidad.CONFIRMADA, true, false, false),
            Caso("activaciones sobre una no confirmada", Perfil.ACTIVACIONES, 7, 5, EstadosOportunidad.PENDIENTE, false, false, false)
        )

        casos.forEach { c ->
            withClue(c.descripcion) {
                c.perfil.puedeVerOportunidad(c.estado, c.responsableId, c.usuarioId) shouldBe c.puedeVer
                c.perfil.puedeEditarOportunidad(c.responsableId, c.usuarioId) shouldBe c.puedeEditar
                c.perfil.puedeEliminarOportunidad(c.responsableId, c.usuarioId) shouldBe c.puedeEliminar
            }
        }
    }

    "un texto de perfil desconocido se trata como Ejecutivo, nunca como supervisor" {
        Perfil.desdeEtiqueta("Administrador") shouldBe Perfil.EJECUTIVO
        Perfil.desdeEtiqueta("Marketing") shouldBe Perfil.EJECUTIVO // perfil en pausa
        Perfil.desdeEtiqueta("supervisor comercial") shouldBe Perfil.EJECUTIVO // distingue mayusculas
        Perfil.desdeEtiqueta("") shouldBe Perfil.EJECUTIVO

        // Las etiquetas validas sí se reconocen
        Perfil.desdeEtiqueta("Supervisor Comercial") shouldBe Perfil.SUPERVISOR
        Perfil.desdeEtiqueta("Gestión de Activaciones") shouldBe Perfil.ACTIVACIONES
    }

    "eliminar organizaciones, eventos y contactos: solo el supervisor (la regla del creador esta en pausa)" {
        // EN PAUSA (el creador elimina lo suyo): ... creadoPorId = 5, usuarioId = 5) shouldBe true
        // Por ahora ni siquiera el creador elimina
        Perfil.EJECUTIVO.puedeEliminarRegistro(creadoPorId = 5, usuarioId = 5) shouldBe false
        // Otro ejecutivo (id 6) no puede eliminar lo de 5
        Perfil.EJECUTIVO.puedeEliminarRegistro(creadoPorId = 5, usuarioId = 6) shouldBe false
        // Los registros antiguos no tienen creador (0): ningun ejecutivo coincide
        Perfil.EJECUTIVO.puedeEliminarRegistro(creadoPorId = 0, usuarioId = 5) shouldBe false
        // El supervisor elimina cualquiera, incluso los sin creador
        Perfil.SUPERVISOR.puedeEliminarRegistro(creadoPorId = 5, usuarioId = 9) shouldBe true
        Perfil.SUPERVISOR.puedeEliminarRegistro(creadoPorId = 0, usuarioId = 9) shouldBe true
        // Gestión de Activaciones nunca elimina
        Perfil.ACTIVACIONES.puedeEliminarRegistro(creadoPorId = 9, usuarioId = 9) shouldBe false
    }

})
