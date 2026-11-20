package com.duoc.dsy1105.repository

import com.duoc.dsy1105.database.ActivacionDao
import com.duoc.dsy1105.database.ActivacionEntity
import com.duoc.dsy1105.database.UsuarioDao
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.Perfil
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk

/**
 * Pruebas de la capa repository: solo Gestión de Activaciones gestiona activaciones.
 */
class ActivacionRepositoryTest : StringSpec({

    val dao = mockk<ActivacionDao>()
    val usuarioDao = mockk<UsuarioDao>()
    val repositorio = ActivacionRepository(dao, usuarioDao)

    beforeTest { clearAllMocks() }

    fun usuario(id: Long, perfil: Perfil) = UsuarioEntity(
        id = id,
        nombre = "Usuario de prueba",
        email = "prueba@ficticio.cl",
        clave = "12345",
        perfil = perfil.etiqueta,
        activo = true
    )

    val activacion = ActivacionEntity(
        oportunidadId = 1,
        responsableId = 3,
        fecha = "10/12/2026",
        lugar = "Stand ficticio"
    )

    "Gestion de Activaciones crea una activacion" {
        coEvery { usuarioDao.buscarPorId(3) } returns usuario(3, Perfil.ACTIVACIONES)
        coEvery { dao.insertar(any()) } just Runs

        repositorio.insertar(solicitanteId = 3, activacion = activacion) shouldBe null

        coVerify(exactly = 1) { dao.insertar(activacion) }
    }

    "el supervisor no puede crear activaciones" {
        coEvery { usuarioDao.buscarPorId(1) } returns usuario(1, Perfil.SUPERVISOR)

        repositorio.insertar(solicitanteId = 1, activacion = activacion) shouldBe MENSAJE_SIN_PERMISO

        coVerify(exactly = 0) { dao.insertar(any()) }
    }

})
