package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.ActivacionEntity
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.ActivacionErrores
import com.duoc.dsy1105.model.ActivacionUiState
import com.duoc.dsy1105.model.EstadosOportunidad
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ActivacionRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import com.duoc.dsy1105.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActivacionViewModel @JvmOverloads constructor(
    app: Application,
    private val repository: ActivacionRepository = ActivacionRepository(app),
    oportunidadRepository: OportunidadRepository = OportunidadRepository(app),
    usuarioRepository: UsuarioRepository = UsuarioRepository(app)
) : AndroidViewModel(app) {

    val activaciones: StateFlow<List<ActivacionEntity>> =
        repository.obtenerTodas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Solo las oportunidades confirmadas se pueden activar
    val oportunidades: StateFlow<List<OportunidadEntity>> =
        oportunidadRepository.obtenerTodas()
            .map { lista -> lista.filter { it.estado == EstadosOportunidad.CONFIRMADA } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Responsables posibles: cuentas activas de Gestión de Activaciones
    val responsables: StateFlow<List<UsuarioEntity>> =
        usuarioRepository.obtenerTodos()
            .map { lista ->
                lista.filter { it.activo && it.perfil == Perfil.ACTIVACIONES.etiqueta }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario (valores + errores en un único objeto)
    private val _estado = MutableStateFlow(ActivacionUiState())
    val estado: StateFlow<ActivacionUiState> = _estado

    // Cada cambio actualiza el valor y limpia el error de ese campo
    fun onOportunidadChange(id: Long) =
        _estado.update { it.copy(oportunidadId = id, errores = it.errores.copy(oportunidad = null)) }

    fun onResponsableChange(id: Long) =
        _estado.update { it.copy(responsableId = id, errores = it.errores.copy(responsable = null)) }

    fun onFechaChange(v: String) =
        _estado.update { it.copy(fecha = v, errores = it.errores.copy(fecha = null)) }

    fun onLugarChange(v: String) =
        _estado.update { it.copy(lugar = v, errores = it.errores.copy(lugar = null)) }

    fun onObservacionesChange(v: String) =
        _estado.update { it.copy(observaciones = v) }

    fun validarFormulario(): Boolean {
        val a = _estado.value
        val errores = ActivacionErrores(
            oportunidad = if (a.oportunidadId == 0L) "Elige una oportunidad confirmada" else null,
            responsable = if (a.responsableId == 0L) "Elige un responsable" else null,
            fecha = if (a.fecha.isBlank()) "La fecha es obligatoria" else null,
            lugar = if (a.lugar.isBlank()) "El lugar es obligatorio" else null
        )
        _estado.update { it.copy(errores = errores) }
        return errores == ActivacionErrores()
    } // fin validarFormulario

    private fun comoEntidad(id: Long = 0): ActivacionEntity {
        val a = _estado.value
        return ActivacionEntity(
            id = id,
            oportunidadId = a.oportunidadId,
            responsableId = a.responsableId,
            fecha = a.fecha,
            lugar = a.lugar,
            observaciones = a.observaciones
        )
    } // fin comoEntidad

    fun guardar(solicitante: Usuario) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            val error = repository.insertar(solicitante.id.toLong(), comoEntidad())
            _estado.update { it.copy(error = error, guardadoExitosamente = error == null) }
        } // fin launch
    } // fin guardar

    fun actualizar(id: Long, solicitante: Usuario) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            val error = repository.actualizar(solicitante.id.toLong(), comoEntidad(id))
            _estado.update { it.copy(error = error, guardadoExitosamente = error == null) }
        } // fin launch
    } // fin actualizar

    fun eliminar(solicitante: Usuario, activacion: ActivacionEntity) {
        viewModelScope.launch {
            val error = repository.eliminar(solicitante.id.toLong(), activacion)
            _estado.update { it.copy(error = error) }
        } // fin launch
    } // fin eliminar

    fun cargarActivacion(id: Long) {
        viewModelScope.launch {
            repository.obtenerPorId(id).first()?.let { a ->
                _estado.update {
                    it.copy(
                        oportunidadId = a.oportunidadId,
                        responsableId = a.responsableId,
                        fecha = a.fecha,
                        lugar = a.lugar,
                        observaciones = a.observaciones
                    )
                }
            }
        } // fin launch
    } // fin cargarActivacion

    fun limpiarFormulario() {
        _estado.update { ActivacionUiState() }
    }

} // fin class ActivacionViewModel
