package com.duoc.dsy1105.model

// Errores del formulario: null = campo válido, texto = mensaje que se muestra al usuario
data class OportunidadErrores(
    val nombre: String? = null,
    val tipo: String? = null,
    val descripcion: String? = null,
    val fuente: String? = null,
    val prioridad: String? = null,
    val potencial: String? = null,
    // Avisos de recursos nativos (permiso de cámara, ubicación): no impiden guardar el formulario
    val foto: String? = null,
    val ubicacion: String? = null
) // fin data class OportunidadErrores

// Estado completo del formulario: valores de los campos + sus errores
data class OportunidadUiState(
    val nombre: String = "",
    val tipoOportunidad: String = "",
    val descripcion: String = "",
    val fuente: String = "",
    val nivelPrioridad: String = "",
    val potencialComercial: String = "",
    val estado: String = EstadosOportunidad.PENDIENTE,
    val observaciones: String = "",

    val fotoUri: String = "",//agregada para camara
    val latitud: Double? = null,
    val longitud: Double? = null,
    val responsableId: Long = 0, // Quién es el responsable (0 = sin asignar)
    // EN PAUSA (perfil Marketing): val materiales: String = "", // Lo que registra Marketing
    val eventoId: Long = 0, // evento asociado (0 = sin evento)
    val organizacionId: Long = 0, // organización asociada (opcional, 0 = ninguna)
    val contactoId: Long = 0, // contacto asociado (opcional, 0 = ninguno)
    // Errores por campo
    val errores: OportunidadErrores = OportunidadErrores(),
    // Error general (por ejemplo, falta de permiso al guardar)
    val error: String? = null,
    // Estado del formulario
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
) // fin data class OportunidadUiState
