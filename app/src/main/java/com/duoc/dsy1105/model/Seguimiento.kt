package com.duoc.dsy1105.model

data class Seguimiento(
    val id: Int,
    val oportunidad: Oportunidad,
    val usuarioResponsable: Usuario,
    val tipoAccion: String,
    val fecha: String,
    val resultado: String,
    val proximaAccion: String,
    val fechaProximoSeguimiento: String,
    val observaciones: String
)