package com.duoc.dsy1105.model

data class Oportunidad(
    val id: Int,
    val nombre: String,
    val tipoOportunidad: String,
    val descripcion: String,
    val fechaDeteccion: String,
    val responsable: Usuario,
    val nivelPrioridad: String,
    val potencialComercial: String,
    val estado: String,
    val observaciones: String
)