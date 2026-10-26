package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.repository.OrganizacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrganizacionViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = OrganizacionRepository(app)

    // Lista de organizaciones desde Room
    val organizaciones: StateFlow<List<OrganizacionEntity>> =
        repository.obtenerTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Estado del formulario
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre

    private val _tipoOrganizacion = MutableStateFlow("")
    val tipoOrganizacion: StateFlow<String> = _tipoOrganizacion

    private val _rubro = MutableStateFlow("")
    val rubro: StateFlow<String> = _rubro

    private val _comuna = MutableStateFlow("")
    val comuna: StateFlow<String> = _comuna

    private val _region = MutableStateFlow("")
    val region: StateFlow<String> = _region

    private val _observaciones = MutableStateFlow("")
    val observaciones: StateFlow<String> = _observaciones

    private val _guardadoExitosamente = MutableStateFlow(false)
    val guardadoExitosamente: StateFlow<Boolean> = _guardadoExitosamente

    private val _nombreError = MutableStateFlow<String?>(null)
    val nombreError: StateFlow<String?> = _nombreError

    private val _tipoError = MutableStateFlow<String?>(null)
    val tipoError: StateFlow<String?> = _tipoError

    private val _rubroError = MutableStateFlow<String?>(null)
    val rubroError: StateFlow<String?> = _rubroError

    private val _regionError = MutableStateFlow<String?>(null)
    val regionError: StateFlow<String?> = _regionError

    private val _comunaError = MutableStateFlow<String?>(null)
    val comunaError: StateFlow<String?> = _comunaError

    // Funciones para actualizar campos
    fun onNombreChange(v: String) {
        _nombre.value = v
        _nombreError.value = null
    }

    fun onTipoChange(v: String) {
        _tipoOrganizacion.value = v
        _tipoError.value = null
    }

    fun onRubroChange(v: String) {
        _rubro.value = v
        _rubroError.value = null
    }

    fun onComunaChange(v: String) {
        _comuna.value = v
        _comunaError.value = null
    }
    fun onRegionChange(v: String) {
        _region.value = v
        _comuna.value = ""
        _regionError.value = null
        _comunaError.value = null
    }
    fun onObservacionesChange(v: String) = _observaciones.update { v }

    fun validar(): Boolean {

        var valido = true

        if (_nombre.value.isBlank()) {
            _nombreError.value = "El nombre es obligatorio"
            valido = false
        }

        if (_tipoOrganizacion.value.isBlank()) {
            _tipoError.value = "El tipo de organización es obligatorio"
            valido = false
        }

        if (_rubro.value.isBlank()) {
            _rubroError.value = "El rubro es obligatorio"
            valido = false
        }

        if (_region.value.isBlank()) {
            _regionError.value = "La región es obligatoria"
            valido = false
        }

        if (_comuna.value.isBlank()) {
            _comunaError.value = "La comuna es obligatoria"
            valido = false
        }

        return valido
    }

    // Guardar en Room
    fun guardar() {
        if (!validar()) return
        viewModelScope.launch {
            repository.insertar(
                OrganizacionEntity(
                    nombre = _nombre.value,
                    tipoOrganizacion = _tipoOrganizacion.value,
                    rubro = _rubro.value,
                    comuna = _comuna.value,
                    region = _region.value,
                    observaciones = _observaciones.value
                )
            )
            _guardadoExitosamente.value = true
        } // fin launch
    } // fin guardar

    // Limpiar formulario
    fun limpiarFormulario() {
        _nombreError.value = null
        _tipoError.value = null
        _rubroError.value = null
        _regionError.value = null
        _comunaError.value = null
        _region.value = ""
        _observaciones.value = ""
        _guardadoExitosamente.value = false
    } // fin limpiarFormulario

    // Eliminar organizacion
    fun eliminar(organizacion: OrganizacionEntity) {
        viewModelScope.launch {
            repository.eliminar(organizacion)
        } // fin launch
    } // fin eliminar

} // fin class OrganizacionViewModel