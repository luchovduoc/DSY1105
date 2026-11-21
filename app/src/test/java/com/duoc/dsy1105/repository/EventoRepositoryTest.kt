package com.duoc.dsy1105.repository

import com.duoc.dsy1105.database.EventoDao
import com.duoc.dsy1105.database.EventoEntity
import com.duoc.dsy1105.database.UsuarioDao
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.Perfil
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

/**
 * Pruebas de la capa repository: el permiso se valida en el repositorio, no solo en la pantalla.
 * Los DAOs son falsos (MockK): se comprueba la regla sin una base de datos real.
 */
class EventoRepositoryTest : StringSpec({

    val dao = mockk<EventoDao>()
    val usuarioDao = mockk<UsuarioDao>()
    val repositorio = EventoRepository(dao, usuarioDao)

    fun usuario(id: Long, perfil: Perfil) = UsuarioEntity(
        id = id,
        nombre = "Usuario de prueba",
        email = "prueba@ficticio.cl",
        clave = "12345",
        perfil = perfil.etiqueta,
        activo = true
    )

    fun evento() = EventoEntity(
        nombre = "Feria de prueba",
        tipo = "Feria empresarial",
        fechaInicio = "10/11/2026",
        comuna = "Santiago",
        region = "Región Metropolitana de Santiago"
    )

    "Gestión de Activaciones no puede crear un evento" {
        coEvery { usuarioDao.buscarPorId(1) } returns usuario(1, Perfil.ACTIVACIONES)

        val resultado = repositorio.insertar(solicitanteId = 1, evento = evento())

        resultado shouldBe MENSAJE_SIN_PERMISO
        // No se llego a guardar nada
        coVerify(exactly = 0) { dao.insertar(any()) }
    }

})
