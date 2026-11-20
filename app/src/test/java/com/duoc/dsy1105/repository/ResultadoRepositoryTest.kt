package com.duoc.dsy1105.repository

import com.duoc.dsy1105.database.ResultadoDao
import com.duoc.dsy1105.database.ResultadoEntity
import com.duoc.dsy1105.database.UsuarioDao
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.model.TiposResultado
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk

/**
 * Pruebas de la capa repository: solo Gestión de Activaciones registra resultados.
 */
class ResultadoRepositoryTest : StringSpec({

    val dao = mockk<ResultadoDao>()
    val usuarioDao = mockk<UsuarioDao>()
    val repositorio = ResultadoRepository(dao, usuarioDao)

    beforeTest { clearAllMocks() }

    fun usuario(id: Long, perfil: Perfil) = UsuarioEntity(
        id = id,
        nombre = "Usuario de prueba",
        email = "prueba@ficticio.cl",
        clave = "12345",
        perfil = perfil.etiqueta,
        activo = true
    )

    val resultado = ResultadoEntity(activacionId = 1, resultado = TiposResultado.EXITOSA)

    "Gestion de Activaciones registra un resultado" {
        coEvery { usuarioDao.buscarPorId(3) } returns usuario(3, Perfil.ACTIVACIONES)
        coEvery { dao.insertar(any()) } just Runs

        repositorio.insertar(solicitanteId = 3, resultado = resultado) shouldBe null

        coVerify(exactly = 1) { dao.insertar(resultado) }
    }

    "un ejecutivo no puede registrar resultados" {
        coEvery { usuarioDao.buscarPorId(2) } returns usuario(2, Perfil.EJECUTIVO)

        repositorio.insertar(solicitanteId = 2, resultado = resultado) shouldBe MENSAJE_SIN_PERMISO

        coVerify(exactly = 0) { dao.insertar(any()) }
    }

})
