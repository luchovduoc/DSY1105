package com.duoc.dsy1105.model

data class Organizacion(
    val id: Int,
    val nombre: String,
    val tipoOrganizacion: String,
    val rubro: String,
    val comuna: String,
    val region: String,
    val sitioWeb: String,
    val publicoAsociado: String,
    val observaciones: String
)