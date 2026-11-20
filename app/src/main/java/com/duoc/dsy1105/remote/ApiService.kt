package com.duoc.dsy1105.remote

import com.duoc.dsy1105.model.EventoRemoto
import retrofit2.http.GET

// Esta interfaz define los endpoints HTTP
interface ApiService {

    // Define una solicitud GET al endpoint /eventos
    @GET("/eventos")
    suspend fun getEventos(): List<EventoRemoto>

} // fin interface ApiService
