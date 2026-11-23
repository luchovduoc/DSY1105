package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.OrganizacionRepository
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
class OrganizacionViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun crearViewModel(
        repository: OrganizacionRepository = mockk(relaxed = true)
    ): OrganizacionViewModel {

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        return OrganizacionViewModel(
            app = mockk<Application>(relaxed = true),
            repository = repository
        )
    }

    fun completarFormulario(viewModel: OrganizacionViewModel) {
        viewModel.onNombreChange("Empresa Demo")
        viewModel.onTipoChange("Empresa")
        viewModel.onRubroChange("Tecnología")
        viewModel.onRegionChange("Región Metropolitana")
        viewModel.onComunaChange("Santiago")
        viewModel.onObservacionesChange("Organización de prueba")
    }

    "formulario vacio marca campos obligatorios" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario() shouldBe false

        val errores = viewModel.estado.value.errores

        errores.nombre.shouldNotBeNull()
        errores.tipo.shouldNotBeNull()
        errores.rubro.shouldNotBeNull()
        errores.region.shouldNotBeNull()
        errores.comuna.shouldNotBeNull()
    }

    "formulario completo es valido" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.validarFormulario() shouldBe true

        val estado = viewModel.estado.value

        estado.nombre shouldBe "Empresa Demo"
        estado.tipoOrganizacion shouldBe "Empresa"
        estado.rubro shouldBe "Tecnología"
        estado.region shouldBe "Región Metropolitana"
        estado.comuna shouldBe "Santiago"
        estado.observaciones shouldBe "Organización de prueba"
    }

    "editar campos limpia sus errores" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario()

        viewModel.estado.value.errores.nombre.shouldNotBeNull()
        viewModel.estado.value.errores.tipo.shouldNotBeNull()
        viewModel.estado.value.errores.rubro.shouldNotBeNull()

        viewModel.onNombreChange("Nueva empresa")
        viewModel.onTipoChange("Fundación")
        viewModel.onRubroChange("Servicios")

        viewModel.estado.value.errores.nombre.shouldBeNull()
        viewModel.estado.value.errores.tipo.shouldBeNull()
        viewModel.estado.value.errores.rubro.shouldBeNull()
    }

    "cambiar region reinicia comuna" {
        val viewModel = crearViewModel()

        viewModel.onRegionChange("Región Metropolitana")
        viewModel.onComunaChange("Santiago")

        viewModel.estado.value.comuna shouldBe "Santiago"

        viewModel.onRegionChange("Región de Valparaíso")

        viewModel.estado.value.region shouldBe "Región de Valparaíso"
        viewModel.estado.value.comuna shouldBe ""
        viewModel.estado.value.errores.region.shouldBeNull()
        viewModel.estado.value.errores.comuna.shouldBeNull()
    }

    "filtro de texto se actualiza correctamente" {
        val viewModel = crearViewModel()

        viewModel.onFiltroTextoChange("empresa")

        viewModel.filtroTexto.value shouldBe "empresa"
    }

    "guardar organizacion correctamente marca exito" {
        val repository = mockk<OrganizacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns null

        val viewModel = crearViewModel(repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 2

        viewModel.guardar(solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "guardar organizacion muestra error de repository" {
        val repository = mockk<OrganizacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns "No autorizado"

        val viewModel = crearViewModel(repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 4

        viewModel.guardar(solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error shouldBe "No autorizado"
    }

    "cargar organizacion llena el formulario" {
        val repository = mockk<OrganizacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        val organizacion = mockk<OrganizacionEntity>()

        every { organizacion.nombre } returns "Fundación Demo"
        every { organizacion.tipoOrganizacion } returns "Fundación"
        every { organizacion.rubro } returns "Social"
        every { organizacion.region } returns "Región Metropolitana"
        every { organizacion.comuna } returns "Puente Alto"
        every { organizacion.observaciones } returns "Organización cargada"

        every {
            repository.obtenerPorId(10L)
        } returns flowOf(organizacion)

        val viewModel = crearViewModel(repository)

        viewModel.cargarOrganizacion(10L)

        val estado = viewModel.estado.value

        estado.nombre shouldBe "Fundación Demo"
        estado.tipoOrganizacion shouldBe "Fundación"
        estado.rubro shouldBe "Social"
        estado.region shouldBe "Región Metropolitana"
        estado.comuna shouldBe "Puente Alto"
        estado.observaciones shouldBe "Organización cargada"
    }

    "actualizar organizacion correctamente marca exito" {
        val repository = mockk<OrganizacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        coEvery {
            repository.actualizar(any(), any())
        } returns null

        val viewModel = crearViewModel(repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 2

        viewModel.actualizar(15L, solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "actualizar organizacion muestra error" {
        val repository = mockk<OrganizacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        coEvery {
            repository.actualizar(any(), any())
        } returns "Sin permisos"

        val viewModel = crearViewModel(repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 7

        viewModel.actualizar(20L, solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error shouldBe "Sin permisos"
    }

    "eliminar organizacion guarda error si repository rechaza" {
        val repository = mockk<OrganizacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        val organizacion = mockk<OrganizacionEntity>()
        val solicitante = mockk<Usuario>()

        every { solicitante.id } returns 9

        coEvery {
            repository.eliminar(9L, organizacion)
        } returns "Solo supervisor puede eliminar"

        val viewModel = crearViewModel(repository)

        viewModel.eliminar(solicitante, organizacion)

        viewModel.estado.value.error shouldBe
                "Solo supervisor puede eliminar"
    }

    "limpiarError elimina mensaje de error" {
        val repository = mockk<OrganizacionRepository>(relaxed = true)

        every {
            repository.obtenerTodas()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns "Error de prueba"

        val viewModel = crearViewModel(repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 2

        viewModel.guardar(solicitante)

        viewModel.estado.value.error shouldBe "Error de prueba"

        viewModel.limpiarError()

        viewModel.estado.value.error.shouldBeNull()
    }

    "limpiarFormulario restaura estado inicial" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.estado.value.nombre shouldBe "Empresa Demo"

        viewModel.limpiarFormulario()

        val estado = viewModel.estado.value

        estado.nombre shouldBe ""
        estado.tipoOrganizacion shouldBe ""
        estado.rubro shouldBe ""
        estado.region shouldBe ""
        estado.comuna shouldBe ""
        estado.observaciones shouldBe ""
        estado.error.shouldBeNull()
        estado.guardadoExitosamente shouldBe false
    }
})