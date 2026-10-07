package com.duoc.dsy1105.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, password: String) {
        val resultado = authRepository.login(email, password)

        if (resultado != null) {
            _usuario.value = resultado
            _error.value = null
        } else {
            _usuario.value = null
            _error.value = "Email o contraseña incorrectos"
        }
    }

    fun cerrarSesion() {
        _usuario.value = null
        _error.value = null
    }
}

