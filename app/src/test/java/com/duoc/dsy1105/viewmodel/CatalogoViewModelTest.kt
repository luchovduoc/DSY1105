package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.model.EventoRemoto
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.CatalogoRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.slot
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    "eventos debe contener los datos esperados después de cargarEventos()" {

        runTest(dispatcher) {

            val fakeEventos = listOf(
                EventoRemoto(id = "1", nombre = "Feria ficticia", tipo = "Feria empresarial"),
                EventoRemoto(id = "2", nombre = "Congreso ficticio", tipo = "Congreso")
            )

            val fakeRepository = mockk<CatalogoRepository>()
            coEvery { fakeRepository.getEventos() } returns fakeEventos

            val testViewModel = CatalogoViewModel(
                mockk<Application>(relaxed = true),
                fakeRepository,
                mockk<OportunidadRepository>()
            )

            advanceUntilIdle()

            testViewModel.eventos.value shouldContainExactly fakeEventos
        }
    }

    "importar dos veces el mismo evento no crea otra oportunidad" {

        runTest(dispatcher) {

            val evento = EventoRemoto(id = "1", nombre = "Feria ficticia", tipo = "Feria empresarial")
            val yaImportada = OportunidadEntity(
                nombre = "Feria ficticia",
                tipoOportunidad = "Feria empresarial",
                descripcion = "",
                fuente = "Catálogo de eventos (API)",
                fechaDeteccion = "",
                nivelPrioridad = "Media",
                potencialComercial = "Medio",
                estado = "Pendiente",
                observaciones = ""
            )

            val fakeRepository = mockk<CatalogoRepository>()
            coEvery { fakeRepository.getEventos() } returns listOf(evento)
            val oportunidadRepository = mockk<OportunidadRepository>()
            coEvery { oportunidadRepository.obtenerTodas() } returns flowOf(listOf(yaImportada))

            val testViewModel = CatalogoViewModel(
                mockk<Application>(relaxed = true),
                fakeRepository,
                oportunidadRepository
            )
            advanceUntilIdle()

            testViewModel.importar(
                evento,
                Usuario(2, "Ejecutivo", "e@ficticio.cl", Perfil.EJECUTIVO, "", true)
            )
            advanceUntilIdle()

            testViewModel.mensaje.value.shouldNotBeNull()
            coVerify(exactly = 0) { oportunidadRepository.insertar(any(), any()) }
        }
    }

    "una oportunidad importada siempre tiene descripcion, aunque el evento no la traiga" {

        runTest(dispatcher) {

            val evento = EventoRemoto(id = "1", nombre = "Feria ficticia", tipo = "Feria empresarial")
            val fakeRepository = mockk<CatalogoRepository>()
            coEvery { fakeRepository.getEventos() } returns listOf(evento)
            val oportunidadRepository = mockk<OportunidadRepository>()
            coEvery { oportunidadRepository.obtenerTodas() } returns flowOf(emptyList())
            val guardada = slot<OportunidadEntity>()
            coEvery { oportunidadRepository.insertar(any(), capture(guardada)) } returns null

            val testViewModel = CatalogoViewModel(
                mockk<Application>(relaxed = true),
                fakeRepository,
                oportunidadRepository
            )
            advanceUntilIdle()

            testViewModel.importar(
                evento,
                Usuario(2, "Ejecutivo", "e@ficticio.cl", Perfil.EJECUTIVO, "", true)
            )
            advanceUntilIdle()

            guardada.captured.descripcion.shouldNotBeBlank()
        }
    }

    "si la API falla, se muestra un mensaje de error en vez de caerse" {

        runTest(dispatcher) {

            val fakeRepository = mockk<CatalogoRepository>()
            coEvery { fakeRepository.getEventos() } throws RuntimeException("sin conexión")

            val testViewModel = CatalogoViewModel(
                mockk<Application>(relaxed = true),
                fakeRepository,
                mockk<OportunidadRepository>()
            )

            advanceUntilIdle()

            testViewModel.mensaje.value.shouldNotBeNull()
        }
    }
})
