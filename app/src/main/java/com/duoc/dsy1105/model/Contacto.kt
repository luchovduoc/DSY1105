package com.duoc.dsy1105.model

data class Contacto(
    val id: Int,
    val nombre: String,
    val organizacion: Organizacion,
    val cargo: String,
    val area: String,
    val email: String,
    val telefono: String,
    val medioContactoPreferente: String,
    val estado: String,
    val fechaUltimoContacto: String,
    val observaciones: String
)