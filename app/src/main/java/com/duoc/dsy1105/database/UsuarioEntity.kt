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
    val area: String = "",
    val activo: Boolean = true // estado de la cuenta (caso DSY1105); lo cambia un supervisor
) // fin data class UsuarioEntity

