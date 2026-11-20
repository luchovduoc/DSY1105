package com.duoc.dsy1105.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Activación en terreno de una oportunidad confirmada (datos ficticios)
@Entity(tableName = "activaciones")
data class ActivacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val oportunidadId: Long,
    val responsableId: Long, // usuario de Gestión de Activaciones a cargo
    val fecha: String, // dd/MM/yyyy
    val lugar: String,
    val observaciones: String = ""
) // fin data class ActivacionEntity
