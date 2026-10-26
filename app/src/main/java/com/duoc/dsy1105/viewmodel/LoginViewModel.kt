package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val authRepository = AuthRepository(app)

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    private val _registroNombre = MutableStateFlow("")
    val registroNombre: StateFlow<String> = _registroNombre

    private val _registroEmail = MutableStateFlow("")
    val registroEmail: StateFlow<String> = _registroEmail

    private val _registroClave = MutableStateFlow("")
    val registroClave: StateFlow<String> = _registroClave

    private val _registroConfirmarClave = MutableStateFlow("")
    val registroConfirmarClave: StateFlow<String> = _registroConfirmarClave

    private val _registroNombreError = MutableStateFlow<String?>(null)
    val registroNombreError: StateFlow<String?> = _registroNombreError

    private val _registroEmailError = MutableStateFlow<String?>(null)
    val registroEmailError: StateFlow<String?> = _registroEmailError

    private val _registroClaveError = MutableStateFlow<String?>(null)
    val registroClaveError: StateFlow<String?> = _registroClaveError

    private val _registroConfirmarError = MutableStateFlow<String?>(null)
    val registroConfirmarError: StateFlow<String?> = _registroConfirmarError

    fun onEmailChange(v: String) {
        _email.value = v
        _emailError.value = null
        _error.value = null
    }

    fun onPasswordChange(v: String) {
        _password.value = v
        _passwordError.value = null
        _error.value = null
    }

    fun onRegistroNombreChange(v: String) {
        _registroNombre.value = v
        _registroNombreError.value = null
        _error.value = null
    }

    fun onRegistroEmailChange(v: String) {
        _registroEmail.value = v
        _registroEmailError.value = null
        _error.value = null
    }

    fun onRegistroClaveChange(v: String) {
        _registroClave.value = v
        _registroClaveError.value = null
        _registroConfirmarError.value = null
        _error.value = null
    }

    fun onRegistroConfirmarClaveChange(v: String) {
        _registroConfirmarClave.value = v
        _registroConfirmarError.value = null
        _error.value = null
    }

    private fun validarLogin(): Boolean {

        var valido = true

        if (_email.value.isBlank()) {
            _emailError.value = "El email es obligatorio"
            valido = false
        } else if (
            !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()
        ) {
            _emailError.value = "Ingresa un email válido"
            valido = false
        }

        if (_password.value.isBlank()) {
            _passwordError.value = "La contraseña es obligatoria"
            valido = false
        }

        return valido
    }

    private fun validarRegistro(): Boolean {

        var valido = true

        if (_registroNombre.value.isBlank()) {
            _registroNombreError.value = "El nombre es obligatorio"
            valido = false
        }

        if (_registroEmail.value.isBlank()) {
            _registroEmailError.value = "El email es obligatorio"
            valido = false
        } else if (
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(_registroEmail.value)
                .matches()
        ) {
            _registroEmailError.value = "Ingresa un email válido"
            valido = false
        }

        if (_registroClave.value.isBlank()) {
            _registroClaveError.value = "La contraseña es obligatoria"
            valido = false
        }

        if (_registroConfirmarClave.value.isBlank()) {
            _registroConfirmarError.value = "Confirma la contraseña"
            valido = false
        } else if (
            _registroClave.value != _registroConfirmarClave.value
        ) {
            _registroConfirmarError.value = "Las contraseñas no coinciden"
            valido = false
        }

        return valido
    }

    // Login contra Room
    fun login() {

        if (!validarLogin()) return

        viewModelScope.launch {
            val resultado = authRepository.login(
                _email.value,
                _password.value
            )

            if (resultado != null) {
                _usuario.value = resultado
                _error.value = null
            } else {
                _usuario.value = null
                _error.value = "Email o contraseña incorrectos"
            }
        } // fin launch
    } // fin login

    // Registrar nuevo usuario
    fun registrar(
        nombre: String,
        email: String,
        clave: String
    ) {
        viewModelScope.launch {
            val exito = authRepository.registrar(
                nombre = nombre,
                email = email,
                clave = clave
            )

            if (exito) {
                _registroExitoso.value = true
                _error.value = null
            } else {
                _error.value = "Este email ya está registrado"
            }
        } // fin launch
    } // fin registrar

    // Cerrar sesion
    fun cerrarSesion() {
        _usuario.value = null
        _error.value = null
        _registroExitoso.value = false
        _email.value = ""
        _password.value = ""
        _emailError.value = null
        _passwordError.value = null
    } // fin cerrarSesion

    // Limpiar estado de registro
    fun limpiarRegistro() {
        _registroExitoso.value = false
        _error.value = null

        _registroNombre.value = ""
        _registroEmail.value = ""
        _registroClave.value = ""
        _registroConfirmarClave.value = ""

        _registroNombreError.value = null
        _registroEmailError.value = null
        _registroClaveError.value = null
        _registroConfirmarError.value = null
    } // fin limpiarRegistro

    fun registrarFormulario() {

        if (!validarRegistro()) return

        viewModelScope.launch {

            val exito = authRepository.registrar(
                nombre = _registroNombre.value,
                email = _registroEmail.value,
                clave = _registroClave.value
            )

            if (exito) {
                _registroExitoso.value = true
                _error.value = null
            } else {
                _error.value = "Este email ya está registrado"
            }
        }
    } // fin registrarFormulario

} // fin class LoginViewModel