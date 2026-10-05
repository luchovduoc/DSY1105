package com.duoc.dsy1105.model

data class Evento(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val descripcion: String,
    val fechaInicio: String,
    val fechaTermino: String,
    val horario: String,
    val direccion: String,
    val comuna: String,
    val region: String,
    val organizacion: Organizacion,
    val publicoEsperado: String,
    val cantidadEstimadaAsistentes: Int,
    val condicionesParticipacion: String,
    val observaciones: String
)