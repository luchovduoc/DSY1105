package com.duoc.dsy1105.backend

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

// Mismos campos que el evento de la app. Se guarda en la colección "eventos".
@Document("eventos")
data class Evento(
    @Id val id: String? = null,
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "", // dd/MM/yyyy
    val fechaTermino: String = "",
    val horario: String = "",
    val comuna: String = "",
    val region: String = "",
    val organizacionId: String = "",
    val publicoEsperado: String = "",
    val cantidadEstimada: Int = 0,
    val condiciones: String = "",
    val observaciones: String = "",
    val creadoPorId: Long = 0
)
