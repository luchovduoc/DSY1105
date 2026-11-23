package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.database.ContactoEntity
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ContactoRepository
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
class ContactoViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun crearViewModel(
        repository: ContactoRepository = mockk(relaxed = true),
        organizacionRepository: OrganizacionRepository = mockk(relaxed = true),
        emailValido: (String) -> Boolean = { true }
    ): ContactoViewModel {

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        every {
            organizacionRepository.obtenerTodas()
        } returns flowOf(emptyList())

        return ContactoViewModel(
            app = mockk<Application>(relaxed = true),
            repository = repository,
            organizacionRepository = organizacionRepository,
            emailValido = emailValido
        )
    }

    fun completarFormulario(viewModel: ContactoViewModel) {
        viewModel.onNombreChange("Ana Pérez")
        viewModel.onOrganizacionChange(10L)
        viewModel.onCargoChange("Gerente")
        viewModel.onAreaChange("Comercial")
        viewModel.onEmailChange("ana@empresa.cl")
        viewModel.onTelefonoChange("+56912345678")
        viewModel.onMedioContactoChange("Email")
        viewModel.onEstadoChange("Activo")
        viewModel.onObservacionesChange("Contacto principal")
    }

    "formulario vacio marca campos obligatorios" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario() shouldBe false

        val errores = viewModel.estado.value.errores

        errores.nombre.shouldNotBeNull()
        errores.organizacion.shouldNotBeNull()
        errores.area.shouldNotBeNull()

        // Email y teléfono son opcionales.
        errores.email.shouldBeNull()
        errores.telefono.shouldBeNull()
    }

    "formulario completo es valido" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.validarFormulario() shouldBe true

        val estado = viewModel.estado.value

        estado.nombre shouldBe "Ana Pérez"
        estado.organizacionId shouldBe 10L
        estado.cargo shouldBe "Gerente"
        estado.area shouldBe "Comercial"
        estado.email shouldBe "ana@empresa.cl"
        estado.telefono shouldBe "+56912345678"
        estado.medioContacto shouldBe "Email"
        estado.estado shouldBe "Activo"
        estado.observaciones shouldBe "Contacto principal"
    }

    "editar campos limpia sus errores" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario()

        viewModel.estado.value.errores.nombre.shouldNotBeNull()
        viewModel.estado.value.errores.organizacion.shouldNotBeNull()
        viewModel.estado.value.errores.area.shouldNotBeNull()

        viewModel.onNombreChange("Luis Soto")
        viewModel.onOrganizacionChange(20L)
        viewModel.onAreaChange("Ventas")

        viewModel.estado.value.errores.nombre.shouldBeNull()
        viewModel.estado.value.errores.organizacion.shouldBeNull()
        viewModel.estado.value.errores.area.shouldBeNull()
    }

    "email invalido genera error" {
        val viewModel = crearViewModel(
            emailValido = { false }
        )

        viewModel.onNombreChange("Ana")
        viewModel.onOrganizacionChange(1L)
        viewModel.onAreaChange("Comercial")
        viewModel.onEmailChange("correo-malo")

        viewModel.validarFormulario() shouldBe false

        viewModel.estado.value.errores.email shouldBe
                "Ingresa un correo válido"
    }

    "email vacio no genera error porque es opcional" {
        val viewModel = crearViewModel(
            emailValido = { false }
        )

        viewModel.onNombreChange("Ana")
        viewModel.onOrganizacionChange(1L)
        viewModel.onAreaChange("Comercial")
        viewModel.onEmailChange("")

        viewModel.validarFormulario() shouldBe true
        viewModel.estado.value.errores.email.shouldBeNull()
    }

    "telefono invalido genera error" {
        val viewModel = crearViewModel()

        viewModel.onNombreChange("Ana")
        viewModel.onOrganizacionChange(1L)
        viewModel.onAreaChange("Comercial")
        viewModel.onTelefonoChange("123")

        viewModel.validarFormulario() shouldBe false

        viewModel.estado.value.errores.telefono shouldBe
                "Ingresa un teléfono válido"
    }

    "telefono valido es aceptado" {
        val viewModel = crearViewModel()

        viewModel.onNombreChange("Ana")
        viewModel.onOrganizacionChange(1L)
        viewModel.onAreaChange("Comercial")
        viewModel.onTelefonoChange("+56 9 1234 5678")

        viewModel.validarFormulario() shouldBe true
        viewModel.estado.value.errores.telefono.shouldBeNull()
    }

    "filtros se actualizan correctamente" {
        val viewModel = crearViewModel()

        viewModel.onFiltroTextoChange("ana")
        viewModel.onFiltroAreaChange("Comercial")
        viewModel.onFiltroEstadoChange("Activo")

        val filtros = viewModel.filtros.value

        filtros.texto shouldBe "ana"
        filtros.area shouldBe "Comercial"
        filtros.estado shouldBe "Activo"
    }

    "guardar contacto correctamente marca exito" {
        val repository = mockk<ContactoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns null

        val viewModel = crearViewModel(repository = repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 2

        viewModel.guardar(solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "guardar contacto muestra error entregado por repository" {
        val repository = mockk<ContactoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns "No autorizado"

        val viewModel = crearViewModel(repository = repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 3

        viewModel.guardar(solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error shouldBe "No autorizado"
    }

    "cargar contacto llena el formulario" {
        val repository = mockk<ContactoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val contacto = mockk<ContactoEntity>()

        every { contacto.nombre } returns "Pedro González"
        every { contacto.organizacionId } returns 8L
        every { contacto.cargo } returns "Jefe Comercial"
        every { contacto.area } returns "Ventas"
        every { contacto.email } returns "pedro@empresa.cl"
        every { contacto.telefono } returns "+56987654321"
        every { contacto.medioContacto } returns "Teléfono"
        every { contacto.estado } returns "Activo"
        every { contacto.observaciones } returns "Cliente prioritario"

        every {
            repository.obtenerPorId(15L)
        } returns flowOf(contacto)

        val viewModel = crearViewModel(repository = repository)

        viewModel.cargarContacto(15L)

        val estado = viewModel.estado.value

        estado.nombre shouldBe "Pedro González"
        estado.organizacionId shouldBe 8L
        estado.cargo shouldBe "Jefe Comercial"
        estado.area shouldBe "Ventas"
        estado.email shouldBe "pedro@empresa.cl"
        estado.telefono shouldBe "+56987654321"
        estado.medioContacto shouldBe "Teléfono"
        estado.estado shouldBe "Activo"
        estado.observaciones shouldBe "Cliente prioritario"
    }

    "actualizar contacto conserva fecha y marca exito" {
        val repository = mockk<ContactoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val contactoExistente = mockk<ContactoEntity>()
        every {
            contactoExistente.fechaUltimoContacto
        } returns "20/11/2026"

        every {
            repository.obtenerPorId(9L)
        } returns flowOf(contactoExistente)

        coEvery {
            repository.actualizar(any(), any())
        } returns null

        val viewModel = crearViewModel(repository = repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 2

        viewModel.actualizar(9L, solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "actualizar contacto muestra error de repository" {
        val repository = mockk<ContactoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val contactoExistente = mockk<ContactoEntity>()
        every {
            contactoExistente.fechaUltimoContacto
        } returns ""

        every {
            repository.obtenerPorId(9L)
        } returns flowOf(contactoExistente)

        coEvery {
            repository.actualizar(any(), any())
        } returns "Sin permisos"

        val viewModel = crearViewModel(repository = repository)

        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 5

        viewModel.actualizar(9L, solicitante)

        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error shouldBe "Sin permisos"
    }

    "eliminar contacto guarda error si repository rechaza" {
        val repository = mockk<ContactoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        val contacto = mockk<ContactoEntity>()
        val solicitante = mockk<Usuario>()

        every { solicitante.id } returns 7

        coEvery {
            repository.eliminar(7L, contacto)
        } returns "Solo el supervisor puede eliminar"

        val viewModel = crearViewModel(repository = repository)

        viewModel.eliminar(solicitante, contacto)

        viewModel.estado.value.error shouldBe
                "Solo el supervisor puede eliminar"
    }

    "limpiarError elimina mensaje de error" {
        val repository = mockk<ContactoRepository>(relaxed = true)

        every {
            repository.obtenerTodos()
        } returns flowOf(emptyList())

        coEvery {
            repository.insertar(any(), any())
        } returns "Error de prueba"

        val viewModel = crearViewModel(repository = repository)

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

        viewModel.estado.value.nombre shouldBe "Ana Pérez"

        viewModel.limpiarFormulario()

        val estado = viewModel.estado.value

        estado.nombre shouldBe ""
        estado.organizacionId shouldBe 0L
        estado.cargo shouldBe ""
        estado.area shouldBe ""
        estado.email shouldBe ""
        estado.telefono shouldBe ""
        estado.observaciones shouldBe ""
        estado.error.shouldBeNull()
        estado.guardadoExitosamente shouldBe false
    }
})