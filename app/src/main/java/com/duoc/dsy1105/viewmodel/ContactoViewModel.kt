package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.ContactoEntity
import com.duoc.dsy1105.database.OrganizacionEntity
import com.duoc.dsy1105.model.ContactoErrores
import com.duoc.dsy1105.model.ContactoUiState
import com.duoc.dsy1105.model.FiltrosContacto
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.ContactoRepository
import com.duoc.dsy1105.repository.OrganizacionRepository
import com.duoc.dsy1105.utils.Filtros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactoViewModel @JvmOverloads constructor(
    app: Application,
    private val repository: ContactoRepository = ContactoRepository(app),
    private val organizacionRepository: OrganizacionRepository = OrganizacionRepository(app),
    private val emailValido: (String) -> Boolean = {
        android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
    }
) : AndroidViewModel(app) {
    private val _filtros = MutableStateFlow(FiltrosContacto())
    val filtros: StateFlow<FiltrosContacto> = _filtros

    fun onFiltroTextoChange(v: String) = _filtros.update { it.copy(texto = v) }
    fun onFiltroAreaChange(v: String) = _filtros.update { it.copy(area = v) }
    fun onFiltroEstadoChange(v: String) = _filtros.update { it.copy(estado = v) }

    // Lista de contactos desde Room (orden alfabetico)
    val contactos: StateFlow<List<ContactoEntity>> =
        combine(repository.obtenerTodos(), _filtros) { lista, filtros ->
            Filtros.contactos(lista, filtros)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Organizaciones (cada contacto pertenece a una)
    val organizaciones: StateFlow<List<OrganizacionEntity>> =
        organizacionRepository.obtenerTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Estado del formulario (valores + errores en un único objeto)
    private val _estado = MutableStateFlow(ContactoUiState())
    val estado: StateFlow<ContactoUiState> = _estado

    // Cada cambio actualiza el valor y limpia el error de ese campo
    fun onNombreChange(v: String) =
        _estado.update { it.copy(nombre = v, errores = it.errores.copy(nombre = null)) }

    fun onOrganizacionChange(id: Long) =
        _estado.update { it.copy(organizacionId = id, errores = it.errores.copy(organizacion = null)) }

    fun onCargoChange(v: String) =
        _estado.update { it.copy(cargo = v) }

    fun onAreaChange(v: String) =
        _estado.update { it.copy(area = v, errores = it.errores.copy(area = null)) }

    fun onEmailChange(v: String) =
        _estado.update { it.copy(email = v, errores = it.errores.copy(email = null)) }

    fun onTelefonoChange(v: String) =
        _estado.update { it.copy(telefono = v, errores = it.errores.copy(telefono = null)) }

    fun onMedioContactoChange(v: String) =
        _estado.update { it.copy(medioContacto = v) }

    fun onEstadoChange(v: String) =
        _estado.update { it.copy(estado = v) }

    fun onObservacionesChange(v: String) =
        _estado.update { it.copy(observaciones = v) }

    fun validarFormulario(): Boolean {
        val a = _estado.value

        val errores = ContactoErrores(
            nombre = if (a.nombre.isBlank()) "El nombre es obligatorio" else null,
            organizacion = if (a.organizacionId == 0L) "La organización es obligatoria" else null,
            area = if (a.area.isBlank()) "El área es obligatoria" else null,
            email = if (a.email.isNotBlank() &&
                !emailValido(a.email)
            ) "Ingresa un correo válido" else null,
            telefono = if (a.telefono.isNotBlank() && !TELEFONO.matches(a.telefono)) {
                "Ingresa un teléfono válido"
            } else null
        )
        _estado.update { it.copy(errores = errores) }

        return errores == ContactoErrores()
    } // fin validarFormulario

    // Convierte el estado del formulario en la entidad que se guarda.
    // La fecha del ultimo contacto la actualizan los seguimientos, no el formulario.
    private fun comoEntidad(id: Long = 0, fechaUltimoContacto: String = ""): ContactoEntity {
        val a = _estado.value
        return ContactoEntity(
            id = id,
            nombre = a.nombre,
            organizacionId = a.organizacionId,
            cargo = a.cargo,
            area = a.area,
            email = a.email,
            telefono = a.telefono,
            medioContacto = a.medioContacto,
            estado = a.estado,
            fechaUltimoContacto = fechaUltimoContacto,
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

    // Actualizar un contacto existente (ejecutivo o supervisor)
    fun actualizar(id: Long, solicitante: Usuario) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            // Se conserva la fecha del ultimo contacto que ya tenia
            val fecha = repository.obtenerPorId(id).first()?.fechaUltimoContacto ?: ""
            val error = repository.actualizar(
                solicitante.id.toLong(),
                comoEntidad(id, fecha)
            )
            _estado.update { it.copy(error = error, guardadoExitosamente = error == null) }
        } // fin launch
    } // fin actualizar

    // Eliminar un contacto (solo el supervisor; el repositorio lo valida)
    fun eliminar(solicitante: Usuario, contacto: ContactoEntity) {
        viewModelScope.launch {
            val error = repository.eliminar(solicitante.id.toLong(), contacto)
            if (error != null) {
                _estado.update { it.copy(error = error) }
            }
        } // fin launch
    } // fin eliminar

    // Cargar un contacto para editarlo
    fun cargarContacto(id: Long) {
        viewModelScope.launch {
            repository.obtenerPorId(id).first()?.let { c ->
                _estado.update {
                    it.copy(
                        nombre = c.nombre,
                        organizacionId = c.organizacionId,
                        cargo = c.cargo,
                        area = c.area,
                        email = c.email,
                        telefono = c.telefono,
                        medioContacto = c.medioContacto,
                        estado = c.estado,
                        observaciones = c.observaciones
                    )
                }
            }
        } // fin launch
    } // fin cargarContacto

    // Cierra el aviso de error (por ejemplo, "no se puede eliminar")
    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }

    // Limpiar formulario
    fun limpiarFormulario() {
        _estado.update { ContactoUiState() }
    } // fin limpiarFormulario

    private companion object {
        // Digitos, espacios y los signos + ( ) -, entre 8 y 16 caracteres
        val TELEFONO = Regex("^[+0-9 ()-]{8,16}$")
    }

} // fin class ContactoViewModel
