package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.model.OportunidadUiState
import com.duoc.dsy1105.repository.OportunidadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OportunidadViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = OportunidadRepository(app)

    // Lista de oportunidades desde Room
    val oportunidades: StateFlow<List<OportunidadEntity>> =
        repository.obtenerTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Estado del formulario
    private val _estado = MutableStateFlow(OportunidadUiState())
    val estado: StateFlow<OportunidadUiState> = _estado

    // Funciones para actualizar campos
    fun onNombreChange(v: String) =
        _estado.update { it.copy(nombre = v, nombreError = null) }

    fun onTipoChange(v: String) =
        _estado.update { it.copy(tipoOportunidad = v, tipoError = null) }

    fun onDescripcionChange(v: String) =
        _estado.update { it.copy(descripcion = v, descripcionError = null) }

    fun onFuenteChange(v: String) =
        _estado.update { it.copy(fuente = v, fuenteError = null) }

    fun onPrioridadChange(v: String) =
        _estado.update { it.copy(nivelPrioridad = v, prioridadError = null) }

    fun onPotencialChange(v: String) =
        _estado.update { it.copy(potencialComercial = v, potencialError = null) }

    fun onObservacionesChange(v: String) =
        _estado.update { it.copy(observaciones = v) }

    // AGREGADO: funcion para actualizar la foto
    fun onFotoUriChange(uri: String) =
        _estado.update { it.copy(fotoUri = uri) }

    // Validacion del formulario
    fun validar(): Boolean {
        val a = _estado.value
        var valido = true

        if (a.nombre.isBlank()) {
            _estado.update { it.copy(nombreError = "Obligatorio") }
            valido = false
        }
        if (a.tipoOportunidad.isBlank()) {
            _estado.update { it.copy(tipoError = "Obligatorio") }
            valido = false
        }
        if (a.descripcion.isBlank()) {
            _estado.update { it.copy(descripcionError = "Obligatorio") }
            valido = false
        }
        if (a.fuente.isBlank()) {
            _estado.update { it.copy(fuenteError = "Obligatorio") }
            valido = false
        }
        if (a.nivelPrioridad.isBlank()) {
            _estado.update { it.copy(prioridadError = "Obligatorio") }
            valido = false
        }
        if (a.potencialComercial.isBlank()) {
            _estado.update { it.copy(potencialError = "Obligatorio") }
            valido = false
        }

        return valido
    } // fin validar

    // Guardar en Room
    fun guardar(onListo: () -> Unit) {
        if (!validar()) return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }

            val a = _estado.value
            repository.insertar(
                OportunidadEntity(
                    nombre = a.nombre,
                    tipoOportunidad = a.tipoOportunidad,
                    descripcion = a.descripcion,
                    fuente = a.fuente,
                    fechaDeteccion = "",
                    nivelPrioridad = a.nivelPrioridad,
                    potencialComercial = a.potencialComercial,
                    estado = a.estado,
                    observaciones = a.observaciones,
                    fotoUri = a.fotoUri  // AGREGADO: guarda la foto
                )
            )

            _estado.update { it.copy(guardando = false, guardadoExitosamente = true) }
            onListo()
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
                            fotoUri = entity.fotoUri  // AGREGADO: carga la foto
                        )
                    } // fin update
                } // fin let
            } // fin collect
        } // fin launch
    } // fin cargarOportunidad

    // Actualizar oportunidad existente
    fun actualizar(id: Long, onListo: () -> Unit) {
        if (!validar()) return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }

            val a = _estado.value
            repository.actualizar(
                OportunidadEntity(
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
                    fotoUri = a.fotoUri  // AGREGADO: guarda la foto al actualizar
                )
            )

            _estado.update { it.copy(guardando = false, guardadoExitosamente = true) }
            onListo()
        } // fin launch
    } // fin actualizar

} // fin class OportunidadViewModel