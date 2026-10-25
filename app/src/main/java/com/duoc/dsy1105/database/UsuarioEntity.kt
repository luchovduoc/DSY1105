package com.duoc.dsy1105.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val email: String,
    val clave: String,
    val perfil: String = "Ejecutivo Comercial",
    val area: String = ""
) // fin data class UsuarioEntity

