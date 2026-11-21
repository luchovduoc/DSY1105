package com.duoc.dsy1105.repository

import com.duoc.dsy1105.model.EventoRemoto
import com.duoc.dsy1105.remote.RetrofitInstance

// Este repositorio se encarga de acceder a los datos usando Retrofit
class CatalogoRepository {

    // Función que obtiene los eventos desde la API
    suspend fun getEventos(): List<EventoRemoto> {
        return RetrofitInstance.api.getEventos()
    }

} // fin CatalogoRepository
