package com.duoc.dsy1105.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Resultado de una activación realizada (datos ficticios)
@Entity(tableName = "resultados")
data class ResultadoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activacionId: Long,
    val resultado: String, // ver TiposResultado
    val contactosGenerados: Int = 0,
    val evidencia: String = "", // nombre o referencia de la foto o documento
    val observaciones: String = ""
) // fin data class ResultadoEntity
