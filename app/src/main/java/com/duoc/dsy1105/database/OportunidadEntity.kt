package com.duoc.dsy1105.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oportunidades")
data class OportunidadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipoOportunidad: String,
    val descripcion: String,
    val fuente: String,
    val fechaDeteccion: String,
    val nivelPrioridad: String,
    val potencialComercial: String,
    val estado: String,
    val observaciones: String,
    val fotoUri: String = ""
) // fin data class OportunidadEntity

