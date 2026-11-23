package com.duoc.dsy1105.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.dsy1105.model.FeriadoRemoto
import com.duoc.dsy1105.repository.FeriadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeriadoViewModel(
    private val repository: FeriadoRepository = FeriadoRepository()
) : ViewModel() {

    private val _feriados =
        MutableStateFlow<List<FeriadoRemoto>>(emptyList())

    val feriados: StateFlow<List<FeriadoRemoto>> = _feriados

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    init {
        cargarFeriados()
    }

    fun cargarFeriados() {
        viewModelScope.launch {
            _cargando.value = true

            try {
                _feriados.value =
                    repository.obtenerFeriados(2026, "CL")

                _mensaje.value = null
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudieron cargar los feriados de Chile"
            } finally {
                _cargando.value = false
            }
        }
    }
}