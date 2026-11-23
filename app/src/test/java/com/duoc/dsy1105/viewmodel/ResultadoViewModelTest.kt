package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.database.ResultadoEntity
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ActivacionRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import com.duoc.dsy1105.repository.ResultadoRepository
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
class ResultadoViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun crearViewModel(
        repository: ResultadoRepository = mockk(relaxed = true),
        activacionRepository: ActivacionRepository = mockk(relaxed = true),
        oportunidadRepository: OportunidadRepository = mockk(relaxed = true)
    ): ResultadoViewModel {

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        every {
            activacionRepository.obtenerTodas()
        } returns flowOf(emptyList())

        every {
            oportunidadRepository.obtenerTodas()
        } returns flowOf(emptyList())

        return ResultadoViewModel(
            app = mockk<Application>(relaxed = true),
            repository = repository,
            activacionRepository = activacionRepository,
            oportunidadRepository = oportunidadRepository
        )
    }

    fun completarFormulario(viewModel: ResultadoViewModel) {
        viewModel.onActivacionChange(10L)
        viewModel.onResultadoChange("Activación exitosa")
        viewModel.onContactosChange("25")
        viewModel.onEvidenciaChange("foto_resultado.jpg")
        viewModel.onObservacionesChange("Buena participación")
    }

    "formulario vacio marca campos obligatorios" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario() shouldBe false

        val errores = viewModel.estado.value.errores

        errores.activacion.shouldNotBeNull()
        errores.resultado.shouldNotBeNull()

        // Contactos es opcional si queda vacío.
        errores.contactos.shouldBeNull()
    }

    "formulario completo es valido" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.validarFormulario() shouldBe true

        val estado = viewModel.estado.value

        estado.activacionId shouldBe 10L
        estado.resultado shouldBe "Activación exitosa"
        estado.contactosGenerados shouldBe "25"
        estado.evidencia shouldBe "foto_resultado.jpg"
        estado.observaciones shouldBe "Buena participación"
    }

    "editar campos limpia sus errores" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario()

        viewModel.estado.value.errores.activacion.shouldNotBeNull()
        viewModel.estado.value.errores.resultado.shouldNotBeNull()

        viewModel.onActivacionChange(8L)
        viewModel.onResultadoChange("Resultado registrado")

        viewModel.estado.value.errores.activacion.shouldBeNull()
        viewModel.estado.value.errores.resultado.shouldBeNull()
    }

    "contactos vacio es valido" {
        val viewModel = crearViewModel()

        viewModel.onActivacionChange(1L)
        viewModel.onResultadoChange("Correcto")
        viewModel.onContactosChange("")

        viewModel.validarFormulario() shouldBe true
        viewModel.estado.value.errores.contactos.shouldBeNull()
    }

    "contactos con letras genera error" {
        val viewModel = crearViewModel()

        viewModel.onActivacionChange(1L)
        viewModel.onResultadoChange("Correcto")
        viewModel.onContactosChange("abc")

        viewModel.validarFormulario() shouldBe false

        viewModel.estado.value.errores.contactos shouldBe
                "Ingresa solo números"
    }

    "contactos negativos generan error" {
        val viewModel = crearViewModel()

        viewModel.onActivacionChange(1L)
        viewModel.onResultadoChange("Correcto")
        viewModel.onContactosChange("-5")

        viewModel.validarFormulario() shouldBe false

        viewModel.estado.value.errores.contactos shouldBe
                "Ingresa solo números"
    }

    "contactos cero es valido" {
        val viewModel = crearViewModel()

        viewModel.onActivacionChange(1L)
        viewModel.onResultadoChange("Correcto")
        viewModel.onContactosChange("0")

        viewModel.validarFormulario() shouldBe true
        viewModel.estado.value.errores.contactos.shouldBeNull()
    }

    "guardar resultado correctamente marca exito" {
        val repository = mockk<ResultadoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns null

        val viewModel = crearViewModel(repository = repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 3

        viewModel.guardar(solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "guardar resultado muestra error del repository" {
        val repository = mockk<ResultadoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns "No autorizado"

        val viewModel = crearViewModel(repository = repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 4

        viewModel.guardar(solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error shouldBe "No autorizado"
    }

    "actualizar resultado correctamente marca exito" {
        val repository = mockk<ResultadoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.actualizar(any(), any())
        } returns null

        val viewModel = crearViewModel(repository = repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 3

        viewModel.actualizar(
            id = 20L,
            solicitante = solicitante
        )

        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "actualizar resultado muestra error" {
        val repository = mockk<ResultadoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.actualizar(any(), any())
        } returns "Sin permisos"

        val viewModel = crearViewModel(repository = repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 7

        viewModel.actualizar(
            id = 20L,
            solicitante = solicitante
        )

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error shouldBe "Sin permisos"
    }

    "eliminar resultado guarda error entregado por repository" {
        val repository = mockk<ResultadoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val resultado = mockk<ResultadoEntity>()
        val solicitante = mockk<Usuario>()

        every { solicitante.id } returns 5

        coEvery {
            repository.eliminar(5L, resultado)
        } returns "No puede eliminar"

        val viewModel = crearViewModel(repository = repository)

        viewModel.eliminar(solicitante, resultado)

        viewModel.estado.value.error shouldBe
                "No puede eliminar"
    }

    "cargar resultado llena el formulario" {
        val repository = mockk<ResultadoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val resultado = mockk<ResultadoEntity>()

        every { resultado.activacionId } returns 11L
        every { resultado.resultado } returns "Resultado cargado"
        every { resultado.contactosGenerados } returns 30
        every { resultado.evidencia } returns "evidencia.jpg"
        every { resultado.observaciones } returns "Observación cargada"

        every {
            repository.obtenerPorId(50L)
        } returns flowOf(resultado)

        val viewModel = crearViewModel(repository = repository)

        viewModel.cargarResultado(50L)

        val estado = viewModel.estado.value

        estado.activacionId shouldBe 11L
        estado.resultado shouldBe "Resultado cargado"
        estado.contactosGenerados shouldBe "30"
        estado.evidencia shouldBe "evidencia.jpg"
        estado.observaciones shouldBe "Observación cargada"
    }

    "cargar resultado con cero contactos deja campo vacio" {
        val repository = mockk<ResultadoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val resultado = mockk<ResultadoEntity>()

        every { resultado.activacionId } returns 5L
        every { resultado.resultado } returns "Sin contactos"
        every { resultado.contactosGenerados } returns 0
        every { resultado.evidencia } returns ""
        every { resultado.observaciones } returns ""

        every {
            repository.obtenerPorId(60L)
        } returns flowOf(resultado)

        val viewModel = crearViewModel(repository = repository)

        viewModel.cargarResultado(60L)

        viewModel.estado.value.contactosGenerados shouldBe ""
    }

    "limpiarFormulario restaura estado inicial" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.estado.value.activacionId shouldBe 10L

        viewModel.limpiarFormulario()

        val estado = viewModel.estado.value

        estado.activacionId shouldBe 0L
        estado.resultado shouldBe ""
        estado.contactosGenerados shouldBe ""
        estado.evidencia shouldBe ""
        estado.observaciones shouldBe ""
        estado.error.shouldBeNull()
        estado.guardadoExitosamente shouldBe false
    }
})