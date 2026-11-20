package com.duoc.dsy1105.backend

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

// Se prueba el controlador con un repositorio falso: no hace falta MongoDB
class EventoControllerTest {

    private val repositorio = mockk<EventoRepository>(relaxed = true)
    private val controlador = EventoController(repositorio)

    private val evento = Evento(
        nombre = "Feria de prueba",
        tipo = "Feria empresarial",
        fechaInicio = "10/11/2026",
        comuna = "Santiago",
        region = "Región Metropolitana de Santiago"
    )

    @Test
    fun `Marketing no puede crear un evento`() {
        val error = assertThrows<ResponseStatusException> {
            controlador.crear("Marketing", 1, evento)
        }

        assertEquals(HttpStatus.FORBIDDEN, error.statusCode)
        verify(exactly = 0) { repositorio.save(any()) }
    }

    @Test
    fun `el ejecutivo crea un evento y queda como creador`() {
        every { repositorio.save(any()) } answers { firstArg() }

        val creado = controlador.crear("Ejecutivo Comercial", 7, evento)

        assertEquals(7L, creado.creadoPorId)
    }

    @Test
    fun `un ejecutivo no elimina un evento creado por otro`() {
        every { repositorio.findById("abc") } returns Optional.of(evento.copy(id = "abc", creadoPorId = 5))

        val error = assertThrows<ResponseStatusException> {
            controlador.eliminar("Ejecutivo Comercial", 7, "abc")
        }

        assertEquals(HttpStatus.FORBIDDEN, error.statusCode)
        verify(exactly = 0) { repositorio.delete(any()) }
    }

    @Test
    fun `el supervisor elimina cualquier evento`() {
        val existente = evento.copy(id = "abc", creadoPorId = 5)
        every { repositorio.findById("abc") } returns Optional.of(existente)

        controlador.eliminar("Supervisor Comercial", 1, "abc")

        verify(exactly = 1) { repositorio.delete(existente) }
    }

    @Test
    fun `un evento sin datos obligatorios se rechaza`() {
        val error = assertThrows<ResponseStatusException> {
            controlador.crear("Ejecutivo Comercial", 7, Evento())
        }

        assertEquals(HttpStatus.BAD_REQUEST, error.statusCode)
    }
}
