package com.duoc.dsy1105.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organizaciones")
data class OrganizacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipoOrganizacion: String,
    val rubro: String,
    val comuna: String,
    val region: String,
    val sitioWeb: String = "",
    val publicoAsociado: String = "",
    val observaciones: String = ""
) // fin data class OrganizacionEntity

