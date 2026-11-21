package com.duoc.dsy1105.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val perfil: Perfil,
    val area: String,
    val estado: Boolean // true = cuenta activa
)
