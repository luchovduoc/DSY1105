package com.duoc.dsy1105.viewmodel

import com.duoc.dsy1105.model.FeriadoRemoto
import com.duoc.dsy1105.repository.FeriadoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class FeriadoViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "carga feriados correctamente al iniciar" {
        val repository = mockk<FeriadoRepository>()

        val feriado1 = mockk<FeriadoRemoto>()
        val feriado2 = mockk<FeriadoRemoto>()

        val lista = listOf(
            feriado1,
            feriado2
        )

        coEvery {
            repository.obtenerFeriados(2026, "CL")
        } returns lista

        val viewModel = FeriadoViewModel(repository)

        viewModel.feriados.value shouldBe lista
        viewModel.mensaje.value.shouldBeNull()
        viewModel.cargando.value shouldBe false

        coVerify(exactly = 1) {
            repository.obtenerFeriados(2026, "CL")
        }
    }

    "muestra mensaje cuando falla la api externa" {
        val repository = mockk<FeriadoRepository>()

        coEvery {
            repository.obtenerFeriados(2026, "CL")
        } throws RuntimeException("Error de red")

        val viewModel = FeriadoViewModel(repository)

        viewModel.feriados.value shouldBe emptyList()
        viewModel.mensaje.value shouldBe
                "No se pudieron cargar los feriados de Chile"

        viewModel.cargando.value shouldBe false
    }

    "puede volver a cargar los feriados manualmente" {
        val repository = mockk<FeriadoRepository>()

        val primeraLista = listOf(
            mockk<FeriadoRemoto>()
        )

        val segundaLista = listOf(
            mockk<FeriadoRemoto>(),
            mockk<FeriadoRemoto>()
        )

        coEvery {
            repository.obtenerFeriados(2026, "CL")
        } returnsMany listOf(
            primeraLista,
            segundaLista
        )

        val viewModel = FeriadoViewModel(repository)

        viewModel.feriados.value shouldBe primeraLista

        viewModel.cargarFeriados()

        viewModel.feriados.value shouldBe segundaLista
        viewModel.mensaje.value.shouldBeNull()
        viewModel.cargando.value shouldBe false

        coVerify(exactly = 2) {
            repository.obtenerFeriados(2026, "CL")
        }
    }
})