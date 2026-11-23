package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class UsuariosViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun crearViewModel(
        repository: UsuarioRepository = mockk(relaxed = true)
    ): UsuariosViewModel {

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        return UsuariosViewModel(
            app = mockk<Application>(relaxed = true),
            repository = repository
        )
    }

    "estado inicial no contiene error" {
        val viewModel = crearViewModel()

        viewModel.estado.value.error.shouldBeNull()
    }

    "cambiar perfil correctamente deja error nulo" {
        val repository = mockk<UsuarioRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val perfil = Perfil.values().first()

        coEvery {
            repository.cambiarPerfil(
                solicitanteId = 1L,
                usuarioId = 2L,
                nuevoPerfil = perfil
            )
        } returns null

        val viewModel = crearViewModel(repository)

        viewModel.onCambiarPerfil(
            solicitanteId = 1L,
            usuarioId = 2L,
            nuevoPerfil = perfil
        )

        viewModel.estado.value.error.shouldBeNull()

        coVerify(exactly = 1) {
            repository.cambiarPerfil(
                solicitanteId = 1L,
                usuarioId = 2L,
                nuevoPerfil = perfil
            )
        }
    }

    "cambiar perfil muestra error del repository" {
        val repository = mockk<UsuarioRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val perfil = Perfil.values().first()

        coEvery {
            repository.cambiarPerfil(
                solicitanteId = 3L,
                usuarioId = 4L,
                nuevoPerfil = perfil
            )
        } returns "No autorizado"

        val viewModel = crearViewModel(repository)

        viewModel.onCambiarPerfil(
            solicitanteId = 3L,
            usuarioId = 4L,
            nuevoPerfil = perfil
        )

        viewModel.estado.value.error shouldBe "No autorizado"
    }

    "activar usuario correctamente deja error nulo" {
        val repository = mockk<UsuarioRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.cambiarActivo(
                solicitanteId = 1L,
                usuarioId = 5L,
                activo = true
            )
        } returns null

        val viewModel = crearViewModel(repository)

        viewModel.onCambiarActivo(
            solicitanteId = 1L,
            usuarioId = 5L,
            activo = true
        )

        viewModel.estado.value.error.shouldBeNull()

        coVerify(exactly = 1) {
            repository.cambiarActivo(
                solicitanteId = 1L,
                usuarioId = 5L,
                activo = true
            )
        }
    }

    "desactivar usuario correctamente deja error nulo" {
        val repository = mockk<UsuarioRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.cambiarActivo(
                solicitanteId = 1L,
                usuarioId = 6L,
                activo = false
            )
        } returns null

        val viewModel = crearViewModel(repository)

        viewModel.onCambiarActivo(
            solicitanteId = 1L,
            usuarioId = 6L,
            activo = false
        )

        viewModel.estado.value.error.shouldBeNull()
    }

    "cambiar activo muestra error del repository" {
        val repository = mockk<UsuarioRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.cambiarActivo(
                solicitanteId = 2L,
                usuarioId = 8L,
                activo = false
            )
        } returns "No se puede modificar el usuario"

        val viewModel = crearViewModel(repository)

        viewModel.onCambiarActivo(
            solicitanteId = 2L,
            usuarioId = 8L,
            activo = false
        )

        viewModel.estado.value.error shouldBe
                "No se puede modificar el usuario"
    }

    "un error anterior se reemplaza al ejecutar otra accion exitosa" {
        val repository = mockk<UsuarioRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val perfil = Perfil.values().first()

        coEvery {
            repository.cambiarPerfil(any(), any(), any())
        } returns "Error inicial"

        coEvery {
            repository.cambiarActivo(any(), any(), any())
        } returns null

        val viewModel = crearViewModel(repository)

        viewModel.onCambiarPerfil(
            solicitanteId = 1L,
            usuarioId = 2L,
            nuevoPerfil = perfil
        )

        viewModel.estado.value.error shouldBe "Error inicial"

        viewModel.onCambiarActivo(
            solicitanteId = 1L,
            usuarioId = 2L,
            activo = true
        )

        viewModel.estado.value.error.shouldBeNull()
    }
})