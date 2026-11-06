package com.duoc.dsy1105.model

data class OportunidadUiState(
    val nombre: String = "",
    val tipoOportunidad: String = "",
    val descripcion: String = "",
    val fuente: String = "",
    val nivelPrioridad: String = "",
    val potencialComercial: String = "",
    val estado: String = "Evento identificado",
    val observaciones: String = "",

    val fotoUri: String = "",//agregada para camara
    val latitud: Double? = null,
    val longitud: Double? = null,
    val responsableId: Long = 0, // AGREGADO: quién es el responsable (0 = sin asignar)
    val materiales: String = "", // AGREGADO: lo que registra Marketing
    // Errores
    val nombreError: String? = null,
    val tipoError: String? = null,
    val descripcionError: String? = null,
    val fuenteError: String? = null,
    val prioridadError: String? = null,
    val potencialError: String? = null,
    // Estado del formulario
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
) // fin data class OportunidadUiState