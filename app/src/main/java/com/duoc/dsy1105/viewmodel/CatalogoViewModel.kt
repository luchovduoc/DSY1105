package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.model.EstadosOportunidad
import com.duoc.dsy1105.model.EventoRemoto
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.CatalogoRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

// Catálogo de eventos que viene de la API. Solo se consulta; "Importar" crea una oportunidad en Room.
// @JvmOverloads conserva el constructor que solo recibe Application (el que usa Android);
// en las pruebas se pasan repositorios falsos.
class CatalogoViewModel @JvmOverloads constructor(
    app: Application,
    private val repository: CatalogoRepository = CatalogoRepository(),
    private val oportunidadRepository: OportunidadRepository = OportunidadRepository(app)
) : AndroidViewModel(app) {

    // Flujo mutable que contiene la lista de eventos
    private val _eventos = MutableStateFlow<List<EventoRemoto>>(emptyList())

    // Flujo público de solo lectura
    val eventos: StateFlow<List<EventoRemoto>> = _eventos

    // Mensaje de error para la pantalla (red o permiso). null = sin error
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    // Aviso de que la importación salió bien. null = nada que mostrar
    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso

    // Se llama automáticamente al iniciar
    init {
        cargarEventos()
    }

    // Obtiene los datos en segundo plano
    fun cargarEventos() {
        viewModelScope.launch {
            try {
                _eventos.value = repository.getEventos()
                _mensaje.value = null
            } catch (e: Exception) {
                _mensaje.value = "No se pudo cargar el catálogo. Revisa que el servidor esté encendido."
            }
        }
    } // fin cargarEventos

    // Crea una oportunidad Pendiente a partir del evento (solo el ejecutivo; el repositorio lo valida)
    fun importar(evento: EventoRemoto, solicitante: Usuario) {
        viewModelScope.launch {
            // No se importa dos veces el mismo evento
            val yaImportado = oportunidadRepository.obtenerTodas().first()
                .any { it.fuente == FUENTE_CATALOGO && it.nombre == evento.nombre }
            if (yaImportado) {
                _aviso.value = null
                _mensaje.value = "Este evento ya fue importado como oportunidad"
                return@launch
            }
            val oportunidad = OportunidadEntity(
                nombre = evento.nombre,
                tipoOportunidad = evento.tipo,
                // La descripción es obligatoria al editar una oportunidad: si el evento no trae una, se pone una por defecto
                descripcion = evento.descripcion.ifBlank { "Importada del catálogo de eventos" },
                fuente = FUENTE_CATALOGO,
                fechaDeteccion = "", // el repositorio pone la fecha de hoy
                nivelPrioridad = "Media",
                potencialComercial = "Medio",
                estado = EstadosOportunidad.PENDIENTE,
                observaciones = ""
            )
            val error = oportunidadRepository.insertar(solicitante.id.toLong(), oportunidad)
            _mensaje.value = error
            _aviso.value = if (error == null) "Oportunidad creada a partir de \"${evento.nombre}\"" else null
        }
    } // fin importar

    // Crea un evento directamente en MongoDB mediante Spring Boot
    fun crearEvento(evento: EventoRemoto, solicitante: Usuario) {
        viewModelScope.launch {
            try {
                repository.crearEvento(
                    perfil = solicitante.perfil.etiqueta,
                    usuarioId = solicitante.id.toLong(),
                    evento = evento
                )

                cargarEventos()
                _mensaje.value = null
                _aviso.value = "Evento creado correctamente"
            } catch (e: HttpException) {
            _aviso.value = null
            _mensaje.value = "Error HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}"
            } catch (e: Exception) {
            _aviso.value = null
            _mensaje.value = "Error: ${e.message}"
            }
        }
    }

    // Actualiza un evento existente en MongoDB
    fun actualizarEvento(evento: EventoRemoto, solicitante: Usuario) {
        viewModelScope.launch {
            val id = evento.id

            if (id.isNullOrBlank()) {
                _mensaje.value = "El evento no tiene identificador"
                return@launch
            }

            try {
                repository.actualizarEvento(
                    id = id,
                    perfil = solicitante.perfil.etiqueta,
                    evento = evento
                )

                cargarEventos()
                _mensaje.value = null
                _aviso.value = "Evento actualizado correctamente"
            } catch (e: Exception) {
                _aviso.value = null
                _mensaje.value = "No se pudo actualizar el evento"
            }
        }
    }

    // Elimina un evento remoto
    fun eliminarEvento(evento: EventoRemoto, solicitante: Usuario) {
        viewModelScope.launch {
            val id = evento.id

            if (id.isNullOrBlank()) {
                _mensaje.value = "El evento no tiene identificador"
                return@launch
            }

            try {
                val respuesta = repository.eliminarEvento(
                    id = id,
                    perfil = solicitante.perfil.etiqueta,
                    usuarioId = solicitante.id.toLong()
                )

                if (respuesta.isSuccessful) {
                    cargarEventos()
                    _mensaje.value = null
                    _aviso.value = "Evento eliminado correctamente"
                } else {
                    _aviso.value = null
                    _mensaje.value = "No tienes permiso para eliminar este evento"
                }
            } catch (e: Exception) {
                _aviso.value = null
                _mensaje.value = "No se pudo eliminar el evento"
            }
        }
    }

    private companion object {
        const val FUENTE_CATALOGO = "Catálogo de eventos (API)"
    }

} // fin class CatalogoViewModel
