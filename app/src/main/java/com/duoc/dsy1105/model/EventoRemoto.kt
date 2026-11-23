package com.duoc.dsy1105.model

// Evento remoto utilizado para comunicarse con Spring Boot + MongoDB.
data class EventoRemoto(
    val id: String? = null,
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "",
    val fechaTermino: String = "",
    val horario: String = "",
    val comuna: String = "",
    val region: String = "",
    val organizacionId: String = "",
    val publicoEsperado: String = "",
    val cantidadEstimada: Int = 0,
    val condiciones: String = "",
    val observaciones: String = "",
    val creadoPorId: Long = 0
)