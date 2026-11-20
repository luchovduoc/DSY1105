package com.duoc.dsy1105.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Contacto corporativo FICTICIO de una organización (el caso exige datos ficticios)
@Entity(tableName = "contactos")
data class ContactoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val organizacionId: Long, // organización a la que pertenece
    val cargo: String = "",
    val area: String,
    val email: String = "",
    val telefono: String = "",
    val medioContacto: String = "", // medio de contacto preferente
    val estado: String = "Activo",
    val fechaUltimoContacto: String = "", // dd/MM/yyyy (se actualiza con los seguimientos)
    val observaciones: String = "",
    val creadoPorId: Long = 0 // quien lo creo (0 = desconocido: solo el supervisor puede eliminarlo)
) // fin data class ContactoEntity
