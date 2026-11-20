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
    val fotoUri: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val responsableId: Long = 0, // Id del usuario responsable (0 = sin asignar)
    // EN PAUSA (perfil Marketing): val materiales: String = "", // Lo que registra Marketing
    val modificadoPor: String = "", // quién hizo el último cambio (nombre del usuario)
    val fechaModificacion: String = "", // cuándo (dd/MM/yyyy HH:mm)
    val eventoId: Long = 0, // evento asociado (0 = sin evento)
    val organizacionId: Long = 0, // organización asociada (0 = sin organización)
    val contactoId: Long = 0 // contacto asociado (0 = sin contacto)
) // fin data class OportunidadEntity

