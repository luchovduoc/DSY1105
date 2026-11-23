package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ContactoRepository
import com.duoc.dsy1105.repository.EventoRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import com.duoc.dsy1105.repository.OrganizacionRepository
import com.duoc.dsy1105.repository.SeguimientoRepository
import com.duoc.dsy1105.repository.UsuarioRepository
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
class OportunidadViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun crearViewModel(
        oportunidadRepository: OportunidadRepository = mockk(relaxed = true)
    ): OportunidadViewModel {

        val usuarioRepository = mockk<UsuarioRepository>()
        val eventoRepository = mockk<EventoRepository>()
        val organizacionRepository = mockk<OrganizacionRepository>()
        val contactoRepository = mockk<ContactoRepository>()
        val seguimientoRepository = mockk<SeguimientoRepository>()

        every { oportunidadRepository.obtenerTodas() } returns flowOf(emptyList())
        every { usuarioRepository.obtenerTodos() } returns flowOf(emptyList())
        every { eventoRepository.obtenerTodos() } returns flowOf(emptyList())
        every { organizacionRepository.obtenerTodas() } returns flowOf(emptyList())
        every { contactoRepository.obtenerTodos() } returns flowOf(emptyList())

        return OportunidadViewModel(
            app = mockk<Application>(relaxed = true),
            repository = oportunidadRepository,
            usuarioRepository = usuarioRepository,
            eventoRepository = eventoRepository,
            organizacionRepository = organizacionRepository,
            contactoRepository = contactoRepository,
            seguimientoRepository = seguimientoRepository
        )
    }

    fun completarFormulario(viewModel: OportunidadViewModel) {
        viewModel.onNombreChange("Convenio Empresa ABC")
        viewModel.onTipoChange("Convenio")
        viewModel.onDescripcionChange("Nueva oportunidad comercial")
        viewModel.onFuenteChange("Contacto directo")
        viewModel.onPrioridadChange("Alta")
        viewModel.onPotencialChange("Alto")
    }

    "validarFormulario marca todos los campos obligatorios vacios" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario() shouldBe false

        val errores = viewModel.estado.value.errores

        errores.nombre.shouldNotBeNull()
        errores.tipo.shouldNotBeNull()
        errores.descripcion.shouldNotBeNull()
        errores.fuente.shouldNotBeNull()
        errores.prioridad.shouldNotBeNull()
        errores.potencial.shouldNotBeNull()
    }

    "formulario completo es valido y actualiza sus campos" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.onEstadoChange("En gestión")
        viewModel.onObservacionesChange("Cliente interesado")
        viewModel.onEventoChange(10L)
        viewModel.onOrganizacionChange(20L)
        viewModel.onContactoChange(30L)
        viewModel.onResponsableChange(40L)

        viewModel.validarFormulario() shouldBe true

        val estado = viewModel.estado.value

        estado.nombre shouldBe "Convenio Empresa ABC"
        estado.tipoOportunidad shouldBe "Convenio"
        estado.descripcion shouldBe "Nueva oportunidad comercial"
        estado.fuente shouldBe "Contacto directo"
        estado.nivelPrioridad shouldBe "Alta"
        estado.potencialComercial shouldBe "Alto"
        estado.estado shouldBe "En gestión"
        estado.observaciones shouldBe "Cliente interesado"
        estado.eventoId shouldBe 10L
        estado.organizacionId shouldBe 20L
        estado.contactoId shouldBe 30L
        estado.responsableId shouldBe 40L
    }

    "editar campos limpia los errores correspondientes" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario()

        viewModel.estado.value.errores.nombre.shouldNotBeNull()
        viewModel.estado.value.errores.tipo.shouldNotBeNull()

        viewModel.onNombreChange("Nueva oportunidad")
        viewModel.onTipoChange("Venta")

        viewModel.estado.value.errores.nombre.shouldBeNull()
        viewModel.estado.value.errores.tipo.shouldBeNull()
        viewModel.estado.value.errores.descripcion.shouldNotBeNull()
    }

    "cambiar organizacion reinicia contacto y actualiza recursos nativos" {
        val viewModel = crearViewModel()

        viewModel.onContactoChange(99L)
        viewModel.onOrganizacionChange(5L)

        viewModel.estado.value.organizacionId shouldBe 5L
        viewModel.estado.value.contactoId shouldBe 0L

        viewModel.onFotoError("Sin permiso de cámara")
        viewModel.estado.value.errores.foto shouldBe "Sin permiso de cámara"

        viewModel.onFotoUriChange("content://foto/prueba")
        viewModel.estado.value.fotoUri shouldBe "content://foto/prueba"
        viewModel.estado.value.errores.foto.shouldBeNull()

        viewModel.onUbicacionError("Sin permiso de ubicación")
        viewModel.estado.value.errores.ubicacion shouldBe "Sin permiso de ubicación"

        viewModel.onUbicacionChange(-33.45, -70.66)
        viewModel.estado.value.latitud shouldBe -33.45
        viewModel.estado.value.longitud shouldBe -70.66
        viewModel.estado.value.errores.ubicacion.shouldBeNull()
    }

    "filtros se actualizan correctamente" {
        val viewModel = crearViewModel()

        viewModel.onFiltroTextoChange("empresa")
        viewModel.onFiltroEstadoChange("Confirmada")
        viewModel.onFiltroTipoChange("Convenio")
        viewModel.onFiltroPrioridadChange("Alta")
        viewModel.onFiltroRegionChange("Metropolitana")
        viewModel.onFiltroResponsableChange(8L)

        val filtros = viewModel.filtros.value

        filtros.texto shouldBe "empresa"
        filtros.estado shouldBe "Confirmada"
        filtros.tipo shouldBe "Convenio"
        filtros.prioridad shouldBe "Alta"
        filtros.region shouldBe "Metropolitana"
        filtros.responsableId shouldBe 8L
    }

    "limpiarFormulario restaura el estado inicial" {
        val viewModel = crearViewModel()

        completarFormulario(viewModel)

        viewModel.estado.value.nombre shouldBe "Convenio Empresa ABC"

        viewModel.limpiarFormulario()

        viewModel.estado.value.nombre shouldBe ""
        viewModel.estado.value.descripcion shouldBe ""
        viewModel.estado.value.guardadoExitosamente shouldBe false
        viewModel.estado.value.error.shouldBeNull()
    }

    "cargarOportunidad carga la entidad en el formulario" {
        val repository = mockk<OportunidadRepository>(relaxed = true)

        every { repository.obtenerTodas() } returns flowOf(emptyList())

        val entity = OportunidadEntity(
            id = 15L,
            nombre = "Oportunidad existente",
            tipoOportunidad = "Convenio",
            descripcion = "Descripción cargada",
            fuente = "Referido",
            fechaDeteccion = "",
            nivelPrioridad = "Media",
            potencialComercial = "Alto",
            estado = "Pendiente",
            observaciones = "Observación",
            fotoUri = "foto.jpg",
            latitud = -33.4,
            longitud = -70.6,
            responsableId = 2L,
            eventoId = 3L,
            organizacionId = 4L,
            contactoId = 5L
        )

        every { repository.obtenerPorId(15L) } returns flowOf(entity)

        val viewModel = crearViewModel(repository)

        viewModel.cargarOportunidad(15L)

        val estado = viewModel.estado.value

        estado.nombre shouldBe "Oportunidad existente"
        estado.tipoOportunidad shouldBe "Convenio"
        estado.descripcion shouldBe "Descripción cargada"
        estado.fuente shouldBe "Referido"
        estado.nivelPrioridad shouldBe "Media"
        estado.potencialComercial shouldBe "Alto"
        estado.estado shouldBe "Pendiente"
        estado.responsableId shouldBe 2L
        estado.eventoId shouldBe 3L
        estado.organizacionId shouldBe 4L
        estado.contactoId shouldBe 5L
    }

    "guardar oportunidad valida ejecuta callback cuando no hay error" {
        val repository = mockk<OportunidadRepository>(relaxed = true)

        every { repository.obtenerTodas() } returns flowOf(emptyList())
        coEvery { repository.insertar(any(), any()) } returns null

        val viewModel = crearViewModel(repository)
        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 2

        var callbackEjecutado = false

        viewModel.guardar(solicitante) {
            callbackEjecutado = true
        }

        callbackEjecutado shouldBe true
        viewModel.estado.value.guardando shouldBe false
        viewModel.estado.value.guardadoExitosamente shouldBe true
        viewModel.estado.value.error.shouldBeNull()
    }

    "actualizar muestra error cuando repositorio rechaza operacion" {
        val repository = mockk<OportunidadRepository>(relaxed = true)

        every { repository.obtenerTodas() } returns flowOf(emptyList())
        coEvery {
            repository.actualizar(any(), any())
        } returns "No tiene permisos para actualizar"

        val viewModel = crearViewModel(repository)
        completarFormulario(viewModel)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 5

        var callbackEjecutado = false

        viewModel.actualizar(10L, solicitante) {
            callbackEjecutado = true
        }

        callbackEjecutado shouldBe false
        viewModel.estado.value.guardando shouldBe false
        viewModel.estado.value.error shouldBe "No tiene permisos para actualizar"
    }

    "eliminar guarda el error entregado por repositorio" {
        val repository = mockk<OportunidadRepository>(relaxed = true)

        every { repository.obtenerTodas() } returns flowOf(emptyList())

        val oportunidad = mockk<OportunidadEntity>()

        coEvery {
            repository.eliminar(7L, oportunidad)
        } returns "No autorizado"

        val viewModel = crearViewModel(repository)

        val solicitante = mockk<Usuario>()
        every { solicitante.id } returns 7

        viewModel.eliminar(solicitante, oportunidad)

        viewModel.estado.value.error shouldBe "No autorizado"
    }
})