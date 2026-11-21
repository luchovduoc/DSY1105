package com.duoc.dsy1105.backend

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

// La app envía quién es el usuario en los encabezados X-Perfil y X-Usuario-Id.
// Consultar es libre; crear, editar y eliminar dependen del perfil.
@RestController
@RequestMapping("/eventos")
class EventoController(private val repositorio: EventoRepository) {

    @GetMapping
    fun listar(): List<Evento> = repositorio.findAll()

    @GetMapping("/{id}")
    fun ver(@PathVariable id: String): Evento = buscar(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(
        @RequestHeader("X-Perfil") perfil: String,
        @RequestHeader("X-Usuario-Id") usuarioId: Long,
        @RequestBody evento: Evento
    ): Evento {
        exigir(Permisos.puedeCrear(perfil))
        validar(evento)
        return repositorio.save(evento.copy(id = null, creadoPorId = usuarioId))
    }

    @PutMapping("/{id}")
    fun editar(
        @RequestHeader("X-Perfil") perfil: String,
        @PathVariable id: String,
        @RequestBody evento: Evento
    ): Evento {
        exigir(Permisos.puedeEditar(perfil))
        validar(evento)
        val actual = buscar(id)
        // El creador original no cambia al editar
        return repositorio.save(evento.copy(id = id, creadoPorId = actual.creadoPorId))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(
        @RequestHeader("X-Perfil") perfil: String,
        @RequestHeader("X-Usuario-Id") usuarioId: Long,
        @PathVariable id: String
    ) {
        val actual = buscar(id)
        exigir(Permisos.puedeEliminar(perfil, actual.creadoPorId, usuarioId))
        repositorio.delete(actual)
    }

    private fun buscar(id: String): Evento =
        repositorio.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "El evento no existe")
        }

    private fun exigir(permitido: Boolean) {
        if (!permitido) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción")
        }
    }

    private fun validar(evento: Evento) {
        val faltan = evento.nombre.isBlank() || evento.tipo.isBlank() || evento.fechaInicio.isBlank() ||
            evento.comuna.isBlank() || evento.region.isBlank()
        if (faltan) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan datos obligatorios del evento")
        }
    }
}
