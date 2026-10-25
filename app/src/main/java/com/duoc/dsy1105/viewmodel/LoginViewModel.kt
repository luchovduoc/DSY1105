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

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    // Login contra Room
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val resultado = authRepository.login(email, password)

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
    } // fin cerrarSesion

    // Limpiar estado de registro
    fun limpiarRegistro() {
        _registroExitoso.value = false
        _error.value = null
    } // fin limpiarRegistro

} // fin class LoginViewModel