package com.duoc.dsy1105.model

// Evento del catálogo que entrega la API REST (backend Spring Boot + MongoDB).
// Los nombres de los campos son los del JSON del servidor.
data class EventoRemoto(
    val id: String? = null,
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "",
    val comuna: String = "",
    val region: String = ""
) // fin data class EventoRemoto
