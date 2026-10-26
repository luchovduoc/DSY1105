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

    // Funciones para actualizar campos
    fun onNombreChange(v: String) = _nombre.update { v }
    fun onTipoChange(v: String) = _tipoOrganizacion.update { v }
    fun onRubroChange(v: String) = _rubro.update { v }
    fun onComunaChange(v: String) = _comuna.update { v }
    fun onRegionChange(v: String) = _region.update { v }
    fun onObservacionesChange(v: String) = _observaciones.update { v }

    // Guardar en Room
    fun guardar() {
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
        _nombre.value = ""
        _tipoOrganizacion.value = ""
        _rubro.value = ""
        _comuna.value = ""
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