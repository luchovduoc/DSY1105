package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.database.ActivacionEntity
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.EstadosOportunidad
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ActivacionRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import com.duoc.dsy1105.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ActivacionViewModelTest : StringSpec({

    val viewModels = mutableListOf<ActivacionViewModel>()

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        viewModels.forEach {
            it.viewModelScope.cancel()
        }
        viewModels.clear()
        Dispatchers.resetMain()
    }

    fun crearViewModel(
        repository: ActivacionRepository = mockk(relaxed = true),
        oportunidadRepository: OportunidadRepository = mockk(relaxed = true),
        usuarioRepository: UsuarioRepository = mockk(relaxed = true)
    ): ActivacionViewModel {

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        every {
            oportunidadRepository.obtenerTodas()
        } returns flowOf(emptyList())

        every {
            usuarioRepository.obtenerTodos()
        } returns flowOf(emptyList())

        val viewModel = ActivacionViewModel(
            app = mockk<Application>(relaxed = true),
            repository = repository,
            oportunidadRepository = oportunidadRepository,
            usuarioRepository = usuarioRepository
        )

        viewModels.add(viewModel)

        return viewModel
    }

    fun completarFormulario(viewModel: ActivacionViewModel) {
        viewModel.onOportunidadChange(10L)
        viewModel.onResponsableChange(20L)
        viewModel.onFechaChange("24/11/2026")
        viewModel.onLugarChange("Centro de Eventos")
        viewModel.onObservacionesChange("Activación de prueba")
    }

    "formulario vacio marca campos obligatorios" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario() shouldBe false

        val errores = viewModel.estado.value.errores

        errores.oportunidad.shouldNotBeNull()
        errores.responsable.shouldNotBeNull()
        errores.fecha.shouldNotBeNull()
        errores.lugar.shouldNotBeNull()
    }

    "formulario completo es valido" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.validarFormulario() shouldBe true

        val estado = viewModel.estado.value

        estado.oportunidadId shouldBe 10L
        estado.responsableId shouldBe 20L
        estado.fecha shouldBe "24/11/2026"
        estado.lugar shouldBe "Centro de Eventos"
        estado.observaciones shouldBe "Activación de prueba"
    }

    "editar campos limpia sus errores" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario()

        viewModel.estado.value.errores.oportunidad.shouldNotBeNull()
        viewModel.estado.value.errores.responsable.shouldNotBeNull()
        viewModel.estado.value.errores.fecha.shouldNotBeNull()
        viewModel.estado.value.errores.lugar.shouldNotBeNull()

        viewModel.onOportunidadChange(1L)
        viewModel.onResponsableChange(2L)
        viewModel.onFechaChange("25/11/2026")
        viewModel.onLugarChange("Santiago")

        val errores = viewModel.estado.value.errores

        errores.oportunidad.shouldBeNull()
        errores.responsable.shouldBeNull()
        errores.fecha.shouldBeNull()
        errores.lugar.shouldBeNull()
    }


    "guardar activacion correctamente marca exito" {
        val repository = mockk<ActivacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns null

        val viewModel = crearViewModel(
            repository = repository
        )

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 3

        viewModel.guardar(solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "guardar activacion muestra error del repository" {
        val repository = mockk<ActivacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns "No autorizado"

        val viewModel = crearViewModel(
            repository = repository
        )

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 4

        viewModel.guardar(solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error shouldBe "No autorizado"
    }

    "actualizar activacion correctamente marca exito" {
        val repository = mockk<ActivacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        coEvery {
            repository.actualizar(any(), any())
        } returns null

        val viewModel = crearViewModel(
            repository = repository
        )

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 3

        viewModel.actualizar(
            id = 15L,
            solicitante = solicitante
        )

        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "actualizar activacion muestra error" {
        val repository = mockk<ActivacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        coEvery {
            repository.actualizar(any(), any())
        } returns "Sin permisos"

        val viewModel = crearViewModel(
            repository = repository
        )

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 7

        viewModel.actualizar(
            id = 15L,
            solicitante = solicitante
        )

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error shouldBe "Sin permisos"
    }

    "eliminar activacion actualiza error entregado por repository" {
        val repository = mockk<ActivacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        val activacion = mockk<ActivacionEntity>()
        val solicitante = mockk<Usuario>()

        every {
            solicitante.id
        } returns 5

        coEvery {
            repository.eliminar(
                5L,
                activacion
            )
        } returns "No puede eliminar"

        val viewModel = crearViewModel(
            repository = repository
        )

        viewModel.eliminar(
            solicitante,
            activacion
        )

        viewModel.estado.value.error shouldBe
                "No puede eliminar"
    }

    "cargar activacion llena el formulario" {
        val repository = mockk<ActivacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        val activacion = mockk<ActivacionEntity>()

        every { activacion.oportunidadId } returns 11L
        every { activacion.responsableId } returns 22L
        every { activacion.fecha } returns "30/11/2026"
        every { activacion.lugar } returns "Puente Alto"
        every { activacion.observaciones } returns "Evento cargado"

        every {
            repository.obtenerPorId(50L)
        } returns flowOf(activacion)

        val viewModel = crearViewModel(
            repository = repository
        )

        viewModel.cargarActivacion(50L)

        val estado = viewModel.estado.value

        estado.oportunidadId shouldBe 11L
        estado.responsableId shouldBe 22L
        estado.fecha shouldBe "30/11/2026"
        estado.lugar shouldBe "Puente Alto"
        estado.observaciones shouldBe "Evento cargado"
    }

    "limpiarFormulario restaura estado inicial" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.estado.value.oportunidadId shouldBe 10L

        viewModel.limpiarFormulario()

        val estado = viewModel.estado.value

        estado.oportunidadId shouldBe 0L
        estado.responsableId shouldBe 0L
        estado.fecha shouldBe ""
        estado.lugar shouldBe ""
        estado.observaciones shouldBe ""
        estado.error.shouldBeNull()
        estado.guardadoExitosamente shouldBe false
    }
})