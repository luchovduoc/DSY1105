package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.repository.EventoRepository
import com.duoc.dsy1105.repository.OrganizacionRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Pruebas de la capa viewmodel: las validaciones del formulario de eventos.
 * Los repositorios son falsos (MockK), así no se abre ninguna base de datos.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EventoViewModelTest : StringSpec({

    // El ViewModel usa el hilo principal de Android, que no existe en la JVM: se reemplaza
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun crearViewModel(): EventoViewModel {
        val repositorio = mockk<EventoRepository>()
        val organizaciones = mockk<OrganizacionRepository>()
        every { repositorio.obtenerTodos() } returns flowOf(emptyList())
        every { organizaciones.obtenerTodas() } returns flowOf(emptyList())
        return EventoViewModel(mockk<Application>(relaxed = true), repositorio, organizaciones)
    }

    "validarFormulario marca los campos obligatorios vacios" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario() shouldBe false

        val errores = viewModel.estado.value.errores
        errores.nombre.shouldNotBeNull()
        errores.tipo.shouldNotBeNull()
        errores.fechaInicio.shouldNotBeNull()
        errores.region.shouldNotBeNull()
        errores.comuna.shouldNotBeNull()
        // Los opcionales no generan error
        errores.fechaTermino.shouldBeNull()
        errores.cantidad.shouldBeNull()
    }

    "una fecha de termino anterior al inicio genera error y una valida no" {
        val viewModel = crearViewModel()
        viewModel.onNombreChange("Feria de beneficios")
        viewModel.onTipoChange("Feria empresarial")
        viewModel.onRegionChange("Región Metropolitana de Santiago")
        viewModel.onComunaChange("Santiago")
        viewModel.onFechaInicioChange("10/11/2026")

        viewModel.onFechaTerminoChange("05/11/2026") // antes del inicio
        viewModel.validarFormulario() shouldBe false
        viewModel.estado.value.errores.fechaTermino.shouldNotBeNull()

        viewModel.onFechaTerminoChange("12/11/2026") // despues del inicio
        viewModel.validarFormulario() shouldBe true
        viewModel.estado.value.errores.fechaTermino.shouldBeNull()
    }

    "editar un campo limpia solo el error de ese campo" {
        val viewModel = crearViewModel()
        viewModel.validarFormulario() // deja errores en varios campos

        viewModel.onNombreChange("Jornada corporativa")

        val errores = viewModel.estado.value.errores
        errores.nombre.shouldBeNull() // el campo editado ya no tiene error
        errores.tipo.shouldNotBeNull() // los demas siguen marcados
        errores.fechaInicio.shouldNotBeNull()
    }

})
