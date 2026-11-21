package com.duoc.dsy1105.repository

import com.duoc.dsy1105.database.OrganizacionDao
import com.duoc.dsy1105.database.OrganizacionEntity
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
import kotlinx.coroutines.flow.flowOf

/**
 * Pruebas de la capa repository: el permiso para eliminar se valida en el repositorio.
 */
class OrganizacionRepositoryTest : StringSpec({

    val dao = mockk<OrganizacionDao>()
    val usuarioDao = mockk<UsuarioDao>()
    val repositorio = OrganizacionRepository(dao, usuarioDao)

    beforeTest { clearAllMocks() }

    fun usuario(id: Long, perfil: Perfil) = UsuarioEntity(
        id = id,
        nombre = "Usuario de prueba",
        email = "prueba@ficticio.cl",
        clave = "12345",
        perfil = perfil.etiqueta,
        activo = true
    )

    val organizacion = OrganizacionEntity(
        id = 5,
        nombre = "Empresa ficticia",
        tipoOrganizacion = "Empresa",
        rubro = "Servicios",
        comuna = "Santiago",
        region = "Región Metropolitana de Santiago"
    )

    "el supervisor elimina una organizacion" {
        coEvery { usuarioDao.buscarPorId(1) } returns usuario(1, Perfil.SUPERVISOR)
        coEvery { dao.obtenerPorId(5) } returns flowOf(organizacion)
        coEvery { dao.eliminar(any()) } just Runs

        val resultado = repositorio.eliminar(solicitanteId = 1, organizacion = organizacion)

        resultado shouldBe null // null = salio bien
        coVerify(exactly = 1) { dao.eliminar(organizacion) }
    }

    "un ejecutivo no puede eliminar una organizacion" {
        coEvery { usuarioDao.buscarPorId(2) } returns usuario(2, Perfil.EJECUTIVO)
        coEvery { dao.obtenerPorId(5) } returns flowOf(organizacion.copy(creadoPorId = 2))

        val resultado = repositorio.eliminar(solicitanteId = 2, organizacion = organizacion)

        resultado shouldBe MENSAJE_SIN_PERMISO
        coVerify(exactly = 0) { dao.eliminar(any()) }
    }

    // EN PAUSA: "no elimina una organizacion que tiene eventos y contactos, y explica por que".
    // Volvera cuando se reactive la comprobacion "en uso" del repositorio.

})
