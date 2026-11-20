package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.ContactoEntity
import com.duoc.dsy1105.database.SeguimientoEntity
import com.duoc.dsy1105.model.SeguimientoErrores
import com.duoc.dsy1105.model.SeguimientoUiState
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ContactoRepository
import com.duoc.dsy1105.repository.SeguimientoRepository
import com.duoc.dsy1105.utils.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeguimientoViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = SeguimientoRepository(app)
    private val contactoRepository = ContactoRepository(app)

    // Contactos que se pueden asociar a la acción (opcional)
    val contactos: StateFlow<List<ContactoEntity>> =
        contactoRepository.obtenerTodos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Estado del formulario (valores + errores en un único objeto)
    private val _estado = MutableStateFlow(SeguimientoUiState())
    val estado: StateFlow<SeguimientoUiState> = _estado

    // Cada cambio actualiza el valor y limpia el error de ese campo
    fun onTipoAccionChange(v: String) =
        _estado.update { it.copy(tipoAccion = v, errores = it.errores.copy(tipoAccion = null)) }

    fun onFechaChange(v: String) =
        _estado.update {
            it.copy(fecha = v, errores = it.errores.copy(fecha = null, fechaProximo = null))
        }

    fun onContactoChange(id: Long) =
        _estado.update { it.copy(contactoId = id) }

    fun onResultadoChange(v: String) =
        _estado.update { it.copy(resultado = v) }

    fun onProximaAccionChange(v: String) =
        _estado.update { it.copy(proximaAccion = v) }

    fun onFechaProximoChange(v: String) =
        _estado.update {
            it.copy(fechaProximoSeguimiento = v, errores = it.errores.copy(fechaProximo = null))
        }

    fun onObservacionesChange(v: String) =
        _estado.update { it.copy(observaciones = v) }

    fun validarFormulario(): Boolean {
        val a = _estado.value

        val fecha = Fechas.parsear(a.fecha)
        val proximo = Fechas.parsear(a.fechaProximoSeguimiento)

        val errores = SeguimientoErrores(
            tipoAccion = if (a.tipoAccion.isBlank()) "El tipo de acción es obligatorio" else null,
            fecha = if (a.fecha.isBlank()) "La fecha es obligatoria" else null,
            fechaProximo = if (fecha != null && proximo != null && proximo < fecha) {
                "No puede ser anterior a la fecha de la acción"
            } else null
        )
        _estado.update { it.copy(errores = errores) }

        return errores == SeguimientoErrores()
    } // fin validarFormulario

    // Registrar la acción (solo quien gestiona la oportunidad; el repositorio lo valida)
    fun guardar(oportunidadId: Long, solicitante: Usuario) {
        if (!validarFormulario()) return

        viewModelScope.launch {
            val a = _estado.value
            val error = repository.insertar(
                solicitante.id.toLong(),
                SeguimientoEntity(
                    oportunidadId = oportunidadId,
                    usuarioId = solicitante.id.toLong(),
                    usuarioNombre = solicitante.nombre,
                    contactoId = a.contactoId,
                    tipoAccion = a.tipoAccion,
                    fecha = a.fecha,
                    registradoEl = "",
                    resultado = a.resultado,
                    proximaAccion = a.proximaAccion,
                    fechaProximoSeguimiento = a.fechaProximoSeguimiento,
                    observaciones = a.observaciones
                )
            )
            _estado.update { it.copy(error = error, guardadoExitosamente = error == null) }
        } // fin launch
    } // fin guardar

    // Limpiar formulario
    fun limpiarFormulario() {
        _estado.update { SeguimientoUiState() }
    } // fin limpiarFormulario

} // fin class SeguimientoViewModel
