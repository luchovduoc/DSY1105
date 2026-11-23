package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado de la pantalla: el error de la última acción (null = sin error). */
data class UsuariosUiState(
    val error: String? = null
)

class UsuariosViewModel @JvmOverloads constructor(
    app: Application,
    private val repository: UsuarioRepository = UsuarioRepository(app)
) : AndroidViewModel(app) {

    // Lista de usuarios desde Room
    val usuarios: StateFlow<List<UsuarioEntity>> =
        repository.obtenerTodos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _estado = MutableStateFlow(UsuariosUiState())
    val estado: StateFlow<UsuariosUiState> = _estado

    fun onCambiarPerfil(solicitanteId: Long, usuarioId: Long, nuevoPerfil: Perfil) {
        viewModelScope.launch {
            val error = repository.cambiarPerfil(solicitanteId, usuarioId, nuevoPerfil)
            _estado.update { it.copy(error = error) }
        }
    } // fin onCambiarPerfil

    // EN PAUSA: eliminar cuentas
    // fun onEliminar(solicitanteId: Long, usuarioId: Long) {
    //     viewModelScope.launch {
    //         val error = repository.eliminar(solicitanteId, usuarioId)
    //         _estado.update { it.copy(error = error) }
    //     }
    // } // fin onEliminar

    fun onCambiarActivo(solicitanteId: Long, usuarioId: Long, activo: Boolean) {
        viewModelScope.launch {
            val error = repository.cambiarActivo(solicitanteId, usuarioId, activo)
            _estado.update { it.copy(error = error) }
        }
    } // fin onCambiarActivo

} // fin class UsuariosViewModel
