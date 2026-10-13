package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.repository.OportunidadRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OportunidadViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = OportunidadRepository(app)

    // Lista de todas las oportunidades desde Room
    val oportunidades: StateFlow<List<OportunidadEntity>> =
        repository.obtenerTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun insertar(oportunidad: OportunidadEntity) {
        viewModelScope.launch {
            repository.insertar(oportunidad)
        }
    }

    fun actualizar(oportunidad: OportunidadEntity) {
        viewModelScope.launch {
            repository.actualizar(oportunidad)
        }
    }

    fun eliminar(oportunidad: OportunidadEntity) {
        viewModelScope.launch {
            repository.eliminar(oportunidad)
        }
    }
}

