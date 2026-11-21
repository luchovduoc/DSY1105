package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.ActivacionEntity
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.database.ResultadoEntity
import com.duoc.dsy1105.model.ResultadoErrores
import com.duoc.dsy1105.model.ResultadoUiState
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ActivacionRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import com.duoc.dsy1105.repository.ResultadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultadoViewModel @JvmOverloads constructor(
    app: Application,
    private val repository: ResultadoRepository = ResultadoRepository(app),
    activacionRepository: ActivacionRepository = ActivacionRepository(app),
    oportunidadRepository: OportunidadRepository = OportunidadRepository(app)
) : AndroidViewModel(app) {

    val resultados: StateFlow<List<ResultadoEntity>> =
        repository.obtenerTodos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Activaciones (para elegir a cuál pertenece el resultado) y oportunidades (para mostrar su nombre)
    val activaciones: StateFlow<List<ActivacionEntity>> =
        activacionRepository.obtenerTodas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val oportunidades: StateFlow<List<OportunidadEntity>> =
        oportunidadRepository.obtenerTodas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario (valores + errores en un único objeto)
    private val _estado = MutableStateFlow(ResultadoUiState())
    val estado: StateFlow<ResultadoUiState> = _estado

    // Cada cambio actualiza el valor y limpia el error de ese campo
    fun onActivacionChange(id: Long) =
        _estado.update { it.copy(activacionId = id, errores = it.errores.copy(activacion = null)) }

    fun onResultadoChange(v: String) =
        _estado.update { it.copy(resultado = v, errores = it.errores.copy(resultado = null)) }

    fun onContactosChange(v: String) =
        _estado.update { it.copy(contactosGenerados = v, errores = it.errores.copy(contactos = null)) }

    fun onEvidenciaChange(v: String) =
        _estado.update { it.copy(evidencia = v) }

    fun onObservacionesChange(v: String) =
        _estado.update { it.copy(observaciones = v) }

    fun validarFormulario(): Boolean {
        val a = _estado.value
        val errores = ResultadoErrores(
            activacion = if (a.activacionId == 0L) "Elige una activación" else null,
            resultado = if (a.resultado.isBlank()) "El resultado es obligatorio" else null,
            contactos = if (a.contactosGenerados.isNotBlank() &&
                a.contactosGenerados.toIntOrNull()?.takeIf { it >= 0 } == null
            ) "Ingresa solo números" else null
        )
        _estado.update { it.copy(errores = errores) }
        return errores == ResultadoErrores()
    } // fin validarFormulario

    private fun comoEntidad(id: Long = 0): ResultadoEntity {
        val a = _estado.value
        return ResultadoEntity(
            id = id,
            activacionId = a.activacionId,
            resultado = a.resultado,
            contactosGenerados = a.contactosGenerados.toIntOrNull() ?: 0,
            evidencia = a.evidencia,
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

    fun eliminar(solicitante: Usuario, resultado: ResultadoEntity) {
        viewModelScope.launch {
            val error = repository.eliminar(solicitante.id.toLong(), resultado)
            _estado.update { it.copy(error = error) }
        } // fin launch
    } // fin eliminar

    fun cargarResultado(id: Long) {
        viewModelScope.launch {
            repository.obtenerPorId(id).first()?.let { r ->
                _estado.update {
                    it.copy(
                        activacionId = r.activacionId,
                        resultado = r.resultado,
                        contactosGenerados = if (r.contactosGenerados > 0) r.contactosGenerados.toString() else "",
                        evidencia = r.evidencia,
                        observaciones = r.observaciones
                    )
                }
            }
        } // fin launch
    } // fin cargarResultado

    fun limpiarFormulario() {
        _estado.update { ResultadoUiState() }
    }

} // fin class ResultadoViewModel
