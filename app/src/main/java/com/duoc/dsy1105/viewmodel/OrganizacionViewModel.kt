package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.model.OrganizacionErrores
import com.duoc.dsy1105.model.OrganizacionUiState
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.OrganizacionRepository
import com.duoc.dsy1105.utils.Filtros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrganizacionViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = OrganizacionRepository(app)

    // Texto de busqueda. Se declara antes de la lista que lo usa.
    private val _filtroTexto = MutableStateFlow("")
    val filtroTexto: StateFlow<String> = _filtroTexto

    fun onFiltroTextoChange(v: String) = _filtroTexto.update { v }

    // Lista de organizaciones desde Room
    val organizaciones: StateFlow<List<OrganizacionEntity>> =
        combine(repository.obtenerTodas(), _filtroTexto) { lista, texto ->
            Filtros.organizaciones(lista, texto)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Estado del formulario (valores + errores en un único objeto)
    private val _estado = MutableStateFlow(OrganizacionUiState())
    val estado: StateFlow<OrganizacionUiState> = _estado

    // Cada cambio actualiza el valor y limpia el error de ese campo
    fun onNombreChange(v: String) =
        _estado.update { it.copy(nombre = v, errores = it.errores.copy(nombre = null)) }

    fun onTipoChange(v: String) =
        _estado.update { it.copy(tipoOrganizacion = v, errores = it.errores.copy(tipo = null)) }

    fun onRubroChange(v: String) =
        _estado.update { it.copy(rubro = v, errores = it.errores.copy(rubro = null)) }

    fun onComunaChange(v: String) =
        _estado.update { it.copy(comuna = v, errores = it.errores.copy(comuna = null)) }

    // Al cambiar la región se reinicia la comuna, porque depende de ella
    fun onRegionChange(v: String) =
        _estado.update {
            it.copy(
                region = v,
                comuna = "",
                errores = it.errores.copy(region = null, comuna = null)
            )
        }

    fun onObservacionesChange(v: String) =
        _estado.update { it.copy(observaciones = v) }

    fun validarFormulario(): Boolean {
        val a = _estado.value

        val errores = OrganizacionErrores(
            nombre = if (a.nombre.isBlank()) "El nombre es obligatorio" else null,
            tipo = if (a.tipoOrganizacion.isBlank()) "El tipo de organización es obligatorio" else null,
            rubro = if (a.rubro.isBlank()) "El rubro es obligatorio" else null,
            region = if (a.region.isBlank()) "La región es obligatoria" else null,
            comuna = if (a.comuna.isBlank()) "La comuna es obligatoria" else null
        )
        _estado.update { it.copy(errores = errores) }

        return errores == OrganizacionErrores()
    } // fin validarFormulario

    // Guardar en Room (solo el ejecutivo; el repositorio lo valida)
    fun guardar(solicitante: Usuario) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            val a = _estado.value
            val error = repository.insertar(
                solicitante.id.toLong(),
                OrganizacionEntity(
                    nombre = a.nombre,
                    tipoOrganizacion = a.tipoOrganizacion,
                    rubro = a.rubro,
                    comuna = a.comuna,
                    region = a.region,
                    observaciones = a.observaciones
                )
            )
            _estado.update { it.copy(error = error, guardadoExitosamente = error == null) }
        } // fin launch
    } // fin guardar

    // Cierra el aviso de error (por ejemplo, "no se puede eliminar")
    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }

    // Limpiar formulario
    fun limpiarFormulario() {
        _estado.update { OrganizacionUiState() }
    } // fin limpiarFormulario

    // Eliminar organizacion (solo el supervisor; el repositorio lo valida)
    fun eliminar(solicitante: Usuario, organizacion: OrganizacionEntity) {
        viewModelScope.launch {
            val error = repository.eliminar(solicitante.id.toLong(), organizacion)
            if (error != null) {
                _estado.update { it.copy(error = error) }
            }
        } // fin launch
    } // fin eliminar


    // Cargar una organizacion para editarla
    fun cargarOrganizacion(id: Long) {
        viewModelScope.launch {
            repository.obtenerPorId(id).first()?.let { o ->
                _estado.update {
                    it.copy(
                        nombre = o.nombre,
                        tipoOrganizacion = o.tipoOrganizacion,
                        rubro = o.rubro,
                        region = o.region,
                        comuna = o.comuna,
                        observaciones = o.observaciones
                    )
                }
            }
        } // fin launch
    } // fin cargarOrganizacion

    // Actualizar una organizacion existente (ejecutivo o supervisor)
    fun actualizar(id: Long, solicitante: Usuario) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            val a = _estado.value
            val error = repository.actualizar(
                solicitante.id.toLong(),
                OrganizacionEntity(
                    id = id,
                    nombre = a.nombre,
                    tipoOrganizacion = a.tipoOrganizacion,
                    rubro = a.rubro,
                    comuna = a.comuna,
                    region = a.region,
                    observaciones = a.observaciones
                )
            )
            _estado.update { it.copy(error = error, guardadoExitosamente = error == null) }
        } // fin launch
    } // fin actualizar

} // fin class OrganizacionViewModel
