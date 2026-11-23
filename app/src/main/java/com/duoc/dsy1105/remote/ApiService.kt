package com.duoc.dsy1105.remote

import com.duoc.dsy1105.model.EventoRemoto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("/eventos")
    suspend fun getEventos(): List<EventoRemoto>

    @GET("/eventos/{id}")
    suspend fun getEvento(
        @Path("id") id: String
    ): EventoRemoto

    @POST("/eventos")
    suspend fun crearEvento(
        @Header("X-Perfil") perfil: String,
        @Header("X-Usuario-Id") usuarioId: Long,
        @Body evento: EventoRemoto
    ): EventoRemoto

    @PUT("/eventos/{id}")
    suspend fun actualizarEvento(
        @Path("id") id: String,
        @Header("X-Perfil") perfil: String,
        @Body evento: EventoRemoto
    ): EventoRemoto

    @DELETE("/eventos/{id}")
    suspend fun eliminarEvento(
        @Path("id") id: String,
        @Header("X-Perfil") perfil: String,
        @Header("X-Usuario-Id") usuarioId: Long
    ): Response<Unit>
}