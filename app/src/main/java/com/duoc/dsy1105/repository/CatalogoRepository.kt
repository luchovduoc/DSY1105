package com.duoc.dsy1105.repository

import com.duoc.dsy1105.model.EventoRemoto
import com.duoc.dsy1105.remote.RetrofitInstance
import retrofit2.Response

// Este repositorio se encarga de acceder a los datos usando Retrofit
class CatalogoRepository {

    suspend fun getEventos(): List<EventoRemoto> {
        return RetrofitInstance.api.getEventos()
    }

    suspend fun getEvento(id: String): EventoRemoto {
        return RetrofitInstance.api.getEvento(id)
    }

    suspend fun crearEvento(
        perfil: String,
        usuarioId: Long,
        evento: EventoRemoto
    ): EventoRemoto {
        return RetrofitInstance.api.crearEvento(
            perfil = perfil,
            usuarioId = usuarioId,
            evento = evento
        )
    }

    suspend fun actualizarEvento(
        id: String,
        perfil: String,
        evento: EventoRemoto
    ): EventoRemoto {
        return RetrofitInstance.api.actualizarEvento(
            id = id,
            perfil = perfil,
            evento = evento
        )
    }

    suspend fun eliminarEvento(
        id: String,
        perfil: String,
        usuarioId: Long
    ): Response<Unit> {
        return RetrofitInstance.api.eliminarEvento(
            id = id,
            perfil = perfil,
            usuarioId = usuarioId
        )
    }

} // fin CatalogoRepository