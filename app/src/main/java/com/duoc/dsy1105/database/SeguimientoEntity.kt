package com.duoc.dsy1105.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Acción de seguimiento de una oportunidad. Juntas forman el historial de gestiones.
@Entity(tableName = "seguimientos")
data class SeguimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val oportunidadId: Long,
    val usuarioId: Long, // quién la registró
    val usuarioNombre: String,
    val contactoId: Long = 0, // contacto involucrado (0 = ninguno)
    val contactoNombre: String = "",
    val tipoAccion: String,
    val fecha: String, // fecha de la acción (dd/MM/yyyy)
    val registradoEl: String, // cuándo se registró (dd/MM/yyyy HH:mm)
    val resultado: String = "",
    val proximaAccion: String = "",
    val fechaProximoSeguimiento: String = "", // dd/MM/yyyy (opcional)
    val observaciones: String = "",
    val automatico: Boolean = false // true = lo generó la app (registro, cambio de estado)
) // fin data class SeguimientoEntity
