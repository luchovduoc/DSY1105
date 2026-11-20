package com.duoc.dsy1105.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Evento corporativo, feria o activación (datos ficticios). Alcance mínimo del caso DSY1105.
@Entity(tableName = "eventos")
data class EventoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: String,
    val descripcion: String = "",
    val fechaInicio: String, // dd/MM/yyyy
    val fechaTermino: String = "", // dd/MM/yyyy (opcional)
    val horario: String = "",
    val comuna: String,
    val region: String,
    val organizacionId: Long = 0, // organización responsable (0 = sin organización)
    val publicoEsperado: String = "",
    val cantidadEstimada: Int = 0,
    val condiciones: String = "", // condiciones de participación
    val observaciones: String = "",
    val creadoPorId: Long = 0 // quien lo creo (0 = desconocido: solo el supervisor puede eliminarlo)
) // fin data class EventoEntity
