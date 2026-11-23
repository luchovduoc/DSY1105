package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.model.LoginErrores
import com.duoc.dsy1105.model.LoginUiState
import com.duoc.dsy1105.model.RegistroErrores
import com.duoc.dsy1105.model.RegistroUiState
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel @JvmOverloads constructor(
    app: Application,
    private val authRepository: AuthRepository = AuthRepository(app),
    private val emailValido: (String) -> Boolean = {
        android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
    }
) : AndroidViewModel(app) {

    init {
        // Garantiza que existan los usuarios de demostración (datos ficticios)
        viewModelScope.launch {
            authRepository.asegurarUsuariosDemo()
        }
    }

    // Usuario con sesión iniciada (null = sin sesión)
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    // Estado de la pantalla de login (valores + errores)
    private val _loginEstado = MutableStateFlow(LoginUiState())
    val loginEstado: StateFlow<LoginUiState> = _loginEstado

    // Estado de la pantalla de registro (valores + errores)
    private val _registroEstado = MutableStateFlow(RegistroUiState())
    val registroEstado: StateFlow<RegistroUiState> = _registroEstado

    // ---------- Login ----------

    // Cada cambio actualiza el valor y limpia el error de ese campo
    fun onEmailChange(v: String) =
        _loginEstado.update {
            it.copy(email = v, errores = it.errores.copy(email = null), error = null)
        }

    fun onPasswordChange(v: String) =
        _loginEstado.update {
            it.copy(password = v, errores = it.errores.copy(password = null), error = null)
        }

    private fun validarLogin(): Boolean {
        val a = _loginEstado.value

        val errores = LoginErrores(
            email = when {
                a.email.isBlank() -> "El email es obligatorio"
                !emailValido(a.email) ->
                    "Ingresa un email válido"
                else -> null
            },
            password = if (a.password.isBlank()) "La contraseña es obligatoria" else null
        )
        _loginEstado.update { it.copy(errores = errores) }

        return errores == LoginErrores()
    } // fin validarLogin

    // Login contra Room
    fun login() {

        if (!validarLogin()) return

        viewModelScope.launch {
            val a = _loginEstado.value
            val resultado = authRepository.login(a.email, a.password)

            when {
                resultado == null -> {
                    _usuario.value = null
                    _loginEstado.update { it.copy(error = "Email o contraseña incorrectos") }
                }
                !resultado.estado -> {
                    _usuario.value = null
                    _loginEstado.update {
                        it.copy(error = "Tu cuenta está desactivada. Contacta a un supervisor.")
                    }
                }
                else -> {
                    _usuario.value = resultado
                    _loginEstado.update { it.copy(error = null) }
                }
            }
        } // fin launch
    } // fin login

    // Cerrar sesion
    fun cerrarSesion() {
        _usuario.value = null
        _loginEstado.value = LoginUiState()
        _registroEstado.value = RegistroUiState()
    } // fin cerrarSesion

    // ---------- Registro ----------

    fun onRegistroNombreChange(v: String) =
        _registroEstado.update {
            it.copy(nombre = v, errores = it.errores.copy(nombre = null), error = null)
        }

    fun onRegistroEmailChange(v: String) =
        _registroEstado.update {
            it.copy(email = v, errores = it.errores.copy(email = null), error = null)
        }

    fun onRegistroClaveChange(v: String) =
        _registroEstado.update {
            it.copy(
                clave = v,
                errores = it.errores.copy(clave = null, confirmarClave = null),
                error = null
            )
        }

    fun onRegistroConfirmarClaveChange(v: String) =
        _registroEstado.update {
            it.copy(
                confirmarClave = v,
                errores = it.errores.copy(confirmarClave = null),
                error = null
            )
        }

    private fun validarRegistro(): Boolean {
        val a = _registroEstado.value

        val errores = RegistroErrores(
            nombre = if (a.nombre.isBlank()) "El nombre es obligatorio" else null,
            email = when {
                a.email.isBlank() -> "El email es obligatorio"
                !emailValido(a.email) ->
                    "Ingresa un email válido"
                else -> null
            },
            clave = if (a.clave.isBlank()) "La contraseña es obligatoria" else null,
            confirmarClave = when {
                a.confirmarClave.isBlank() -> "Confirma la contraseña"
                a.clave != a.confirmarClave -> "Las contraseñas no coinciden"
                else -> null
            }
        )
        _registroEstado.update { it.copy(errores = errores) }

        return errores == RegistroErrores()
    } // fin validarRegistro

    // Limpiar estado de registro
    fun limpiarRegistro() {
        _registroEstado.value = RegistroUiState()
    } // fin limpiarRegistro

    fun registrarFormulario() {

        if (!validarRegistro()) return

        viewModelScope.launch {
            val a = _registroEstado.value

            // El perfil no se elige: toda cuenta nueva es Ejecutivo (ver AuthRepository)
            val exito = authRepository.registrar(
                nombre = a.nombre,
                email = a.email,
                clave = a.clave
            )

            if (exito) {
                _registroEstado.update { it.copy(registroExitoso = true, error = null) }
            } else {
                _registroEstado.update { it.copy(error = "Este email ya está registrado") }
            }
        }
    } // fin registrarFormulario

} // fin class LoginViewModel
