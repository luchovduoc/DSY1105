package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.ContactoEntity
import com.duoc.dsy1105.database.EventoEntity
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.database.SeguimientoEntity
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.OportunidadUiState
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ContactoRepository
import com.duoc.dsy1105.repository.EventoRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import com.duoc.dsy1105.repository.OrganizacionRepository
import com.duoc.dsy1105.repository.SeguimientoRepository
import com.duoc.dsy1105.repository.UsuarioRepository
import com.duoc.dsy1105.model.FiltrosOportunidad
import com.duoc.dsy1105.utils.Filtros
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OportunidadViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = OportunidadRepository(app)
    private val usuarioRepository = UsuarioRepository(app)
    private val eventoRepository = EventoRepository(app)
    private val organizacionRepository = OrganizacionRepository(app)
    private val contactoRepository = ContactoRepository(app)
    private val seguimientoRepository = SeguimientoRepository(app)

    // Historial de gestiones de una oportunidad (mas reciente primero)
    fun seguimientosDe(oportunidadId: Long): Flow<List<SeguimientoEntity>> =
        seguimientoRepository.obtenerPorOportunidad(oportunidadId)

    // Eventos y organizaciones (para asociar un evento y mostrar su organización)
    val eventos: StateFlow<List<EventoEntity>> =
        eventoRepository.obtenerTodos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val organizaciones: StateFlow<List<OrganizacionEntity>> =
        organizacionRepository.obtenerTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Contactos (para asociar uno a la oportunidad)
    val contactos: StateFlow<List<ContactoEntity>> =
        contactoRepository.obtenerTodos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Usuarios (para mostrar el nombre del responsable y elegirlo al asignar)
    val usuarios: StateFlow<List<UsuarioEntity>> =
        usuarioRepository.obtenerTodos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Oportunidades que el usuario puede ver según su perfil:
    // ejecutivo = las suyas, supervisor = todas, marketing = solo las confirmadas
    // Sobre lo visible se aplican los filtros de la lista (texto, estado, tipo, etc.)
    fun oportunidadesVisibles(usuario: Usuario?): Flow<List<OportunidadEntity>> =
        combine(
            repository.obtenerTodas(),
            _filtros,
            eventoRepository.obtenerTodos()
        ) { lista, filtros, eventos ->
            if (usuario == null) {
                emptyList()
            } else {
                val visibles = lista.filter {
                    usuario.perfil.puedeVerOportunidad(
                        estado = it.estado,
                        responsableId = it.responsableId,
                        usuarioId = usuario.id.toLong()
                    )
                }
                Filtros.oportunidades(visibles, filtros, eventos)
            }
        }

    // Filtros de la lista (busqueda por texto, estado, tipo, prioridad, region y responsable)
    private val _filtros = MutableStateFlow(FiltrosOportunidad())
    val filtros: StateFlow<FiltrosOportunidad> = _filtros

    fun onFiltroTextoChange(v: String) = _filtros.update { it.copy(texto = v) }
    fun onFiltroEstadoChange(v: String) = _filtros.update { it.copy(estado = v) }
    fun onFiltroTipoChange(v: String) = _filtros.update { it.copy(tipo = v) }
    fun onFiltroPrioridadChange(v: String) = _filtros.update { it.copy(prioridad = v) }
    fun onFiltroRegionChange(v: String) = _filtros.update { it.copy(region = v) }
    fun onFiltroResponsableChange(id: Long) = _filtros.update { it.copy(responsableId = id) }

    // Estado del formulario
    private val _estado = MutableStateFlow(OportunidadUiState())
    val estado: StateFlow<OportunidadUiState> = _estado

    // Cada cambio actualiza el valor y limpia el error de ese campo
    fun onNombreChange(v: String) =
        _estado.update { it.copy(nombre = v, errores = it.errores.copy(nombre = null)) }

    fun onTipoChange(v: String) =
        _estado.update { it.copy(tipoOportunidad = v, errores = it.errores.copy(tipo = null)) }

    fun onDescripcionChange(v: String) =
        _estado.update { it.copy(descripcion = v, errores = it.errores.copy(descripcion = null)) }

    fun onFuenteChange(v: String) =
        _estado.update { it.copy(fuente = v, errores = it.errores.copy(fuente = null)) }

    fun onPrioridadChange(v: String) =
        _estado.update { it.copy(nivelPrioridad = v, errores = it.errores.copy(prioridad = null)) }

    fun onEstadoChange(v: String) =
        _estado.update { it.copy(estado = v) }

    fun onPotencialChange(v: String) =
        _estado.update { it.copy(potencialComercial = v, errores = it.errores.copy(potencial = null)) }

    fun onObservacionesChange(v: String) =
        _estado.update { it.copy(observaciones = v) }

    // EN PAUSA (perfil Marketing): materiales
    // fun onMaterialesChange(v: String) =
    //     _estado.update { it.copy(materiales = v) }

    fun onEventoChange(id: Long) =
        _estado.update { it.copy(eventoId = id) }

    // Al cambiar la organización se reinicia el contacto, porque depende de ella
    fun onOrganizacionChange(id: Long) =
        _estado.update { it.copy(organizacionId = id, contactoId = 0) }

    fun onContactoChange(id: Long) =
        _estado.update { it.copy(contactoId = id) }

    // Solo el supervisor asigna responsable (el repositorio lo vuelve a validar)
    fun onResponsableChange(id: Long) =
        _estado.update { it.copy(responsableId = id) }

    // Funcion para actualizar la foto
    fun onFotoUriChange(uri: String) =
        _estado.update { it.copy(fotoUri = uri, errores = it.errores.copy(foto = null)) }

    fun onUbicacionChange(latitud: Double, longitud: Double) =
        _estado.update {
            it.copy(
                latitud = latitud,
                longitud = longitud,
                errores = it.errores.copy(ubicacion = null)
            )
        }

    // Avisos de permisos y recursos nativos (null = sin aviso)
    fun onFotoError(mensaje: String?) =
        _estado.update { it.copy(errores = it.errores.copy(foto = mensaje)) }

    fun onUbicacionError(mensaje: String?) =
        _estado.update { it.copy(errores = it.errores.copy(ubicacion = mensaje)) }

    // Validacion del formulario
    fun validarFormulario(): Boolean {
        val a = _estado.value

        val errores = a.errores.copy(
            nombre = if (a.nombre.isBlank()) "Obligatorio" else null,
            tipo = if (a.tipoOportunidad.isBlank()) "Obligatorio" else null,
            descripcion = if (a.descripcion.isBlank()) "Obligatorio" else null,
            fuente = if (a.fuente.isBlank()) "Obligatorio" else null,
            prioridad = if (a.nivelPrioridad.isBlank()) "Obligatorio" else null,
            potencial = if (a.potencialComercial.isBlank()) "Obligatorio" else null
        )
        _estado.update { it.copy(errores = errores) }

        return errores.nombre == null &&
            errores.tipo == null &&
            errores.descripcion == null &&
            errores.fuente == null &&
            errores.prioridad == null &&
            errores.potencial == null
    } // fin validarFormulario

    // Convierte el estado del formulario en la entidad que se guarda
    private fun comoEntidad(id: Long = 0): OportunidadEntity {
        val a = _estado.value
        return OportunidadEntity(
            id = id,
            nombre = a.nombre,
            tipoOportunidad = a.tipoOportunidad,
            descripcion = a.descripcion,
            fuente = a.fuente,
            fechaDeteccion = "",
            nivelPrioridad = a.nivelPrioridad,
            potencialComercial = a.potencialComercial,
            estado = a.estado,
            observaciones = a.observaciones,
            fotoUri = a.fotoUri,
            latitud = a.latitud,
            longitud = a.longitud,
            responsableId = a.responsableId,
            // EN PAUSA (perfil Marketing): materiales = a.materiales,
            eventoId = a.eventoId,
            organizacionId = a.organizacionId,
            contactoId = a.contactoId
        )
    } // fin comoEntidad

    // Guardar en Room (solo el ejecutivo crea; el repositorio lo valida)
    fun guardar(solicitante: Usuario, onListo: () -> Unit) {
        if (!validarFormulario()) return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, error = null) }

            val error = repository.insertar(solicitante.id.toLong(), comoEntidad())

            if (error != null) {
                _estado.update { it.copy(guardando = false, error = error) }
            } else {
                _estado.update { it.copy(guardando = false, guardadoExitosamente = true) }
                onListo()
            }
        } // fin launch
    } // fin guardar

    // Limpiar formulario
    fun limpiarFormulario() {
        _estado.update { OportunidadUiState() }
    } // fin limpiarFormulario

    // Cargar oportunidad para editar
    fun cargarOportunidad(id: Long) {
        viewModelScope.launch {
            repository.obtenerPorId(id).collect { entity ->
                entity?.let {
                    _estado.update { _ ->
                        OportunidadUiState(
                            nombre = entity.nombre,
                            tipoOportunidad = entity.tipoOportunidad,
                            descripcion = entity.descripcion,
                            fuente = entity.fuente,
                            nivelPrioridad = entity.nivelPrioridad,
                            potencialComercial = entity.potencialComercial,
                            estado = entity.estado,
                            observaciones = entity.observaciones,
                            fotoUri = entity.fotoUri,
                            latitud = entity.latitud,
                            longitud = entity.longitud,
                            responsableId = entity.responsableId,
                            // EN PAUSA (perfil Marketing): materiales = entity.materiales,
                            eventoId = entity.eventoId,
                            organizacionId = entity.organizacionId,
                            contactoId = entity.contactoId
                        )
                    } // fin update
                } // fin let
            } // fin collect
        } // fin launch
    } // fin cargarOportunidad

    // Actualizar oportunidad existente (el repositorio decide que campos puede cambiar cada perfil)
    fun actualizar(id: Long, solicitante: Usuario, onListo: () -> Unit) {
        if (!validarFormulario()) return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, error = null) }

            val error = repository.actualizar(solicitante.id.toLong(), comoEntidad(id))

            if (error != null) {
                _estado.update { it.copy(guardando = false, error = error) }
            } else {
                _estado.update { it.copy(guardando = false, guardadoExitosamente = true) }
                onListo()
            }
        } // fin launch
    } // fin actualizar

    // Eliminar oportunidad existente (solo el ejecutivo responsable)
    fun eliminar(solicitante: Usuario, oportunidad: OportunidadEntity) {
        viewModelScope.launch {
            val error = repository.eliminar(solicitante.id.toLong(), oportunidad)
            if (error != null) {
                _estado.update { it.copy(error = error) }
            }
        } // fin launch
    } // fin eliminar


} // fin class OportunidadViewModel
