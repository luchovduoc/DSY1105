package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ContactoRepository
import com.duoc.dsy1105.repository.SeguimientoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SeguimientoViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun crearViewModel(
        repository: SeguimientoRepository = mockk(relaxed = true),
        contactoRepository: ContactoRepository = mockk(relaxed = true)
    ): SeguimientoViewModel {

        every {
            contactoRepository.obtenerTodos()
        } returns flowOf(emptyList())

        return SeguimientoViewModel(
            app = mockk<Application>(relaxed = true),
            repository = repository,
            contactoRepository = contactoRepository
        )
    }

    fun completarFormulario(viewModel: SeguimientoViewModel) {
        viewModel.onTipoAccionChange("Llamada")
        viewModel.onFechaChange("23/11/2026")
        viewModel.onContactoChange(10L)
        viewModel.onResultadoChange("Cliente interesado")
        viewModel.onProximaAccionChange("Enviar propuesta")
        viewModel.onFechaProximoChange("25/11/2026")
        viewModel.onObservacionesChange("Seguimiento de prueba")
    }

    "formulario vacio marca campos obligatorios" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario() shouldBe false

        val errores = viewModel.estado.value.errores

        errores.tipoAccion.shouldNotBeNull()
        errores.fecha.shouldBeNull()
        errores.fechaProximo.shouldBeNull()
    }

    "formulario completo es valido" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.validarFormulario() shouldBe true

        val estado = viewModel.estado.value

        estado.tipoAccion shouldBe "Llamada"
        estado.fecha shouldBe "23/11/2026"
        estado.contactoId shouldBe 10L
        estado.resultado shouldBe "Cliente interesado"
        estado.proximaAccion shouldBe "Enviar propuesta"
        estado.fechaProximoSeguimiento shouldBe "25/11/2026"
        estado.observaciones shouldBe "Seguimiento de prueba"
    }

    "editar campos limpia sus errores" {
        val viewModel = crearViewModel()

        // Forzamos la fecha vacía para generar ambos errores.
        viewModel.onFechaChange("")

        viewModel.validarFormulario()

        viewModel.estado.value.errores.tipoAccion.shouldNotBeNull()
        viewModel.estado.value.errores.fecha.shouldNotBeNull()

        viewModel.onTipoAccionChange("Correo")
        viewModel.onFechaChange("23/11/2026")

        viewModel.estado.value.errores.tipoAccion.shouldBeNull()
        viewModel.estado.value.errores.fecha.shouldBeNull()
    }

    "fecha de proximo seguimiento anterior genera error" {
        val viewModel = crearViewModel()

        viewModel.onTipoAccionChange("Reunión")
        viewModel.onFechaChange("23/11/2026")
        viewModel.onFechaProximoChange("20/11/2026")

        viewModel.validarFormulario() shouldBe false

        viewModel.estado.value.errores.fechaProximo shouldBe
                "No puede ser anterior a la fecha de la acción"
    }

    "fecha de proximo seguimiento posterior es valida" {
        val viewModel = crearViewModel()

        viewModel.onTipoAccionChange("Reunión")
        viewModel.onFechaChange("23/11/2026")
        viewModel.onFechaProximoChange("30/11/2026")

        viewModel.validarFormulario() shouldBe true
        viewModel.estado.value.errores.fechaProximo.shouldBeNull()
    }

    "fecha de proximo seguimiento vacia es valida" {
        val viewModel = crearViewModel()

        viewModel.onTipoAccionChange("Llamada")
        viewModel.onFechaChange("23/11/2026")
        viewModel.onFechaProximoChange("")

        viewModel.validarFormulario() shouldBe true
        viewModel.estado.value.errores.fechaProximo.shouldBeNull()
    }

    "campos opcionales se actualizan correctamente" {
        val viewModel = crearViewModel()

        viewModel.onContactoChange(15L)
        viewModel.onResultadoChange("Sin respuesta")
        viewModel.onProximaAccionChange("Volver a llamar")
        viewModel.onObservacionesChange("Intentar nuevamente mañana")

        val estado = viewModel.estado.value

        estado.contactoId shouldBe 15L
        estado.resultado shouldBe "Sin respuesta"
        estado.proximaAccion shouldBe "Volver a llamar"
        estado.observaciones shouldBe "Intentar nuevamente mañana"
    }

    "guardar seguimiento correctamente marca exito" {
        val repository = mockk<SeguimientoRepository>(relaxed = true)

        coEvery {
            repository.insertar(any(), any())
        } returns null

        val viewModel = crearViewModel(
            repository = repository
        )

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 2
        every { solicitante.nombre } returns "Ejecutivo Demo"

        viewModel.guardar(
            oportunidadId = 100L,
            solicitante = solicitante
        )

        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "guardar seguimiento muestra error del repository" {
        val repository = mockk<SeguimientoRepository>(relaxed = true)

        coEvery {
            repository.insertar(any(), any())
        } returns "No autorizado"

        val viewModel = crearViewModel(
            repository = repository
        )

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 3
        every { solicitante.nombre } returns "Supervisor Demo"

        viewModel.guardar(
            oportunidadId = 100L,
            solicitante = solicitante
        )

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error shouldBe "No autorizado"
    }

    "guardar no llama repository si formulario es invalido" {
        val repository = mockk<SeguimientoRepository>(relaxed = true)

        val viewModel = crearViewModel(
            repository = repository
        )

        val solicitante = mockk<Usuario>(relaxed = true)

        viewModel.onFechaChange("")

        viewModel.guardar(
            oportunidadId = 1L,
            solicitante = solicitante
        )

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.errores.tipoAccion.shouldNotBeNull()
        viewModel.estado.value.errores.fecha.shouldNotBeNull()
    }

    "limpiarFormulario restaura estado inicial" {
        val viewModel = crearViewModel()

        val fechaInicial = viewModel.estado.value.fecha

        completarFormulario(viewModel)

        viewModel.estado.value.tipoAccion shouldBe "Llamada"

        viewModel.limpiarFormulario()

        val estado = viewModel.estado.value

        estado.tipoAccion shouldBe ""
        estado.fecha shouldBe fechaInicial
        estado.contactoId shouldBe 0L
        estado.resultado shouldBe ""
        estado.proximaAccion shouldBe ""
        estado.fechaProximoSeguimiento shouldBe ""
        estado.observaciones shouldBe ""
        estado.error.shouldBeNull()
        estado.guardadoExitosamente shouldBe false
    }
})