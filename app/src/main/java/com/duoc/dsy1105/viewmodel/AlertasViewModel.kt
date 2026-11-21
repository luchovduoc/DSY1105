package com.duoc.dsy1105.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duoc.dsy1105.model.AlertasUiState
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.EventoRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import com.duoc.dsy1105.repository.SeguimientoRepository
import com.duoc.dsy1105.utils.GeneradorAlertas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AlertasViewModel(app: Application) : AndroidViewModel(app) {

    private val oportunidadRepository = OportunidadRepository(app)
    private val seguimientoRepository = SeguimientoRepository(app)
    private val eventoRepository = EventoRepository(app)

    // Se recalcula solo cuando cambian las oportunidades, los seguimientos o los eventos
    fun alertasPara(usuario: Usuario?): Flow<AlertasUiState> =
        combine(
            oportunidadRepository.obtenerTodas(),
            seguimientoRepository.obtenerTodos(),
            eventoRepository.obtenerTodos()
        ) { oportunidades, seguimientos, eventos ->
            if (usuario == null) {
                AlertasUiState()
            } else {
                GeneradorAlertas.generar(
                    perfil = usuario.perfil,
                    usuarioId = usuario.id.toLong(),
                    oportunidades = oportunidades,
                    seguimientos = seguimientos,
                    eventos = eventos
                )
            }
        }

} // fin class AlertasViewModel
