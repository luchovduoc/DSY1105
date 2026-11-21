package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.EventoEntity
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.model.EventoErrores
import com.duoc.dsy1105.model.EventoUiState
import com.duoc.dsy1105.model.FiltrosEvento
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.EventoRepository
import com.duoc.dsy1105.repository.OrganizacionRepository
import com.duoc.dsy1105.utils.Fechas
import com.duoc.dsy1105.utils.Filtros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @JvmOverloads conserva el constructor que solo recibe Application, que es el que usa Android
// para crear el ViewModel; en las pruebas se pasan repositorios falsos.
class EventoViewModel @JvmOverloads constructor(
    app: Application,
    private val repository: EventoRepository = EventoRepository(app),
    private val organizacionRepository: OrganizacionRepository = OrganizacionRepository(app)
) : AndroidViewModel(app) {

    // Filtros de la lista (texto, tipo, region y fecha). Se declaran antes de la lista que los usa.
    private val _filtros = MutableStateFlow(FiltrosEvento())
    val filtros: StateFlow<FiltrosEvento> = _filtros

    fun onFiltroTextoChange(v: String) = _filtros.update { it.copy(texto = v) }
    fun onFiltroTipoChange(v: String) = _filtros.update { it.copy(tipo = v) }
    fun onFiltroRegionChange(v: String) = _filtros.update { it.copy(region = v) }
    fun onFiltroFechaChange(v: String) = _filtros.update { it.copy(fecha = v) }

    // Eventos desde Room: primero los próximos (del más cercano al más lejano), luego los pasados
    val eventos: StateFlow<List<EventoEntity>> =
        combine(repository.obtenerTodos(), _filtros) { lista, filtros ->
            ordenarPorFecha(Filtros.eventos(lista, filtros))
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Organizaciones (para elegir la organización responsable y mostrar su nombre)
    val organizaciones: StateFlow<List<OrganizacionEntity>> =
        organizacionRepository.obtenerTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Estado del formulario (valores + errores en un único objeto)
    private val _estado = MutableStateFlow(EventoUiState())
    val estado: StateFlow<EventoUiState> = _estado

    // Cada cambio actualiza el valor y limpia el error de ese campo
    fun onNombreChange(v: String) =
        _estado.update { it.copy(nombre = v, errores = it.errores.copy(nombre = null)) }

    fun onTipoChange(v: String) =
        _estado.update { it.copy(tipo = v, errores = it.errores.copy(tipo = null)) }

    fun onDescripcionChange(v: String) =
        _estado.update { it.copy(descripcion = v) }

    fun onFechaInicioChange(v: String) =
        _estado.update {
            it.copy(
                fechaInicio = v,
                errores = it.errores.copy(fechaInicio = null, fechaTermino = null)
            )
        }

    fun onFechaTerminoChange(v: String) =
        _estado.update { it.copy(fechaTermino = v, errores = it.errores.copy(fechaTermino = null)) }

    fun onHorarioChange(v: String) =
        _estado.update { it.copy(horario = v) }

    // Al cambiar la región se reinicia la comuna, porque depende de ella
    fun onRegionChange(v: String) =
        _estado.update {
            it.copy(
                region = v,
                comuna = "",
                errores = it.errores.copy(region = null, comuna = null)
            )
        }

    fun onComunaChange(v: String) =
        _estado.update { it.copy(comuna = v, errores = it.errores.copy(comuna = null)) }

    fun onOrganizacionChange(id: Long) =
        _estado.update { it.copy(organizacionId = id) }

    fun onPublicoEsperadoChange(v: String) =
        _estado.update { it.copy(publicoEsperado = v) }

    fun onCantidadChange(v: String) =
        _estado.update { it.copy(cantidadEstimada = v, errores = it.errores.copy(cantidad = null)) }

    fun onCondicionesChange(v: String) =
        _estado.update { it.copy(condiciones = v) }

    fun onObservacionesChange(v: String) =
        _estado.update { it.copy(observaciones = v) }

    fun validarFormulario(): Boolean {
        val a = _estado.value

        val inicio = Fechas.parsear(a.fechaInicio)
        val termino = Fechas.parsear(a.fechaTermino)

        val errores = EventoErrores(
            nombre = if (a.nombre.isBlank()) "El nombre es obligatorio" else null,
            tipo = if (a.tipo.isBlank()) "El tipo es obligatorio" else null,
            fechaInicio = if (a.fechaInicio.isBlank()) "La fecha de inicio es obligatoria" else null,
            fechaTermino = if (inicio != null && termino != null && termino < inicio) {
                "No puede ser anterior a la fecha de inicio"
            } else null,
            region = if (a.region.isBlank()) "La región es obligatoria" else null,
            comuna = if (a.comuna.isBlank()) "La comuna es obligatoria" else null,
            cantidad = if (a.cantidadEstimada.isNotBlank() &&
                a.cantidadEstimada.toIntOrNull()?.takeIf { it >= 0 } == null
            ) "Ingresa solo números" else null
        )
        _estado.update { it.copy(errores = errores) }

        return errores == EventoErrores()
    } // fin validarFormulario

    // Convierte el estado del formulario en la entidad que se guarda
    private fun comoEntidad(id: Long = 0): EventoEntity {
        val a = _estado.value
        return EventoEntity(
            id = id,
            nombre = a.nombre,
            tipo = a.tipo,
            descripcion = a.descripcion,
            fechaInicio = a.fechaInicio,
            fechaTermino = a.fechaTermino,
            horario = a.horario,
            comuna = a.comuna,
            region = a.region,
            organizacionId = a.organizacionId,
            publicoEsperado = a.publicoEsperado,
            cantidadEstimada = a.cantidadEstimada.toIntOrNull() ?: 0,
            condiciones = a.condiciones,
            observaciones = a.observaciones
        )
    } // fin comoEntidad

    // Guardar en Room (solo el ejecutivo; el repositorio lo valida)
    fun guardar(solicitante: Usuario) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            val error = repository.insertar(solicitante.id.toLong(), comoEntidad())
            _estado.update { it.copy(error = error, guardadoExitosamente = error == null) }
        } // fin launch
    } // fin guardar

    // Actualizar un evento existente (ejecutivo o supervisor)
    fun actualizar(id: Long, solicitante: Usuario) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            val error = repository.actualizar(solicitante.id.toLong(), comoEntidad(id))
            _estado.update { it.copy(error = error, guardadoExitosamente = error == null) }
        } // fin launch
    } // fin actualizar

    // Eliminar un evento (su creador o el supervisor; el repositorio lo valida).
    // [onEliminado] solo se llama si se elimino.
    fun eliminar(solicitante: Usuario, evento: EventoEntity, onEliminado: () -> Unit = {}) {
        viewModelScope.launch {
            val error = repository.eliminar(solicitante.id.toLong(), evento)
            if (error != null) {
                _estado.update { it.copy(error = error) }
            } else {
                onEliminado()
            }
        } // fin launch
    } // fin eliminar

    // Cargar un evento para editarlo
    fun cargarEvento(id: Long) {
        viewModelScope.launch {
            repository.obtenerPorId(id).first()?.let { e ->
                _estado.update {
                    it.copy(
                        nombre = e.nombre,
                        tipo = e.tipo,
                        descripcion = e.descripcion,
                        fechaInicio = e.fechaInicio,
                        fechaTermino = e.fechaTermino,
                        horario = e.horario,
                        region = e.region,
                        comuna = e.comuna,
                        organizacionId = e.organizacionId,
                        publicoEsperado = e.publicoEsperado,
                        cantidadEstimada = if (e.cantidadEstimada > 0) e.cantidadEstimada.toString() else "",
                        condiciones = e.condiciones,
                        observaciones = e.observaciones
                    )
                }
            }
        } // fin launch
    } // fin cargarEvento

    // Cierra el aviso de error (por ejemplo, "no se puede eliminar")
    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }

    // Limpiar formulario
    fun limpiarFormulario() {
        _estado.update { EventoUiState() }
    } // fin limpiarFormulario

    private fun ordenarPorFecha(lista: List<EventoEntity>): List<EventoEntity> {
        val (proximos, pasados) = lista.partition {
            (Fechas.diasDesdeHoy(it.fechaInicio) ?: -1) >= 0
        }
        return proximos.sortedBy { Fechas.parsear(it.fechaInicio) ?: Long.MAX_VALUE } +
            pasados.sortedByDescending { Fechas.parsear(it.fechaInicio) ?: 0L }
    } // fin ordenarPorFecha

} // fin class EventoViewModel
