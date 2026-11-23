package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.model.AlertasUiState
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.EventoRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import com.duoc.dsy1105.repository.SeguimientoRepository
import com.duoc.dsy1105.utils.GeneradorAlertas
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class AlertasViewModelTest : StringSpec({

    fun crearViewModel(
        oportunidadRepository: OportunidadRepository = mockk(relaxed = true),
        seguimientoRepository: SeguimientoRepository = mockk(relaxed = true),
        eventoRepository: EventoRepository = mockk(relaxed = true)
    ): AlertasViewModel {

        every {
            oportunidadRepository.obtenerTodas()
        } returns flowOf(emptyList())

        every {
            seguimientoRepository.obtenerTodos()
        } returns flowOf(emptyList())

        every {
            eventoRepository.obtenerTodos()
        } returns flowOf(emptyList())

        return AlertasViewModel(
            app = mockk<Application>(relaxed = true),
            oportunidadRepository = oportunidadRepository,
            seguimientoRepository = seguimientoRepository,
            eventoRepository = eventoRepository
        )
    }

    "usuario nulo devuelve estado de alertas vacio" {
        val viewModel = crearViewModel()

        val resultado = viewModel
            .alertasPara(null)
            .first()

        resultado shouldBe AlertasUiState()
    }

    "usuario valido genera alertas usando sus datos" {
        val oportunidadRepository =
            mockk<OportunidadRepository>(relaxed = true)

        val seguimientoRepository =
            mockk<SeguimientoRepository>(relaxed = true)

        val eventoRepository =
            mockk<EventoRepository>(relaxed = true)

        every {
            oportunidadRepository.obtenerTodas()
        } returns flowOf(emptyList())

        every {
            seguimientoRepository.obtenerTodos()
        } returns flowOf(emptyList())

        every {
            eventoRepository.obtenerTodos()
        } returns flowOf(emptyList())

        val usuario = mockk<Usuario>()

        val perfil = Perfil.values().first()

        every { usuario.id } returns 1
        every { usuario.perfil } returns perfil

        val viewModel = AlertasViewModel(
            app = mockk<Application>(relaxed = true),
            oportunidadRepository = oportunidadRepository,
            seguimientoRepository = seguimientoRepository,
            eventoRepository = eventoRepository
        )

        val resultado = viewModel
            .alertasPara(usuario)
            .first()

        val esperado = GeneradorAlertas.generar(
            perfil = perfil,
            usuarioId = 1L,
            oportunidades = emptyList(),
            seguimientos = emptyList(),
            eventos = emptyList()
        )

        resultado shouldBe esperado
    }
})