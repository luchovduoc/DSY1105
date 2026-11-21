package com.duoc.dsy1105.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Las fechas se guardan como texto "dd/MM/yyyy" (como el resto de los datos de texto del proyecto).
 * Se usa SimpleDateFormat porque minSdk = 24 y java.time requiere API 26.
 */
object Fechas {

    private val LOCALE = Locale("es", "CL")

    private fun formato(utc: Boolean = false) =
        SimpleDateFormat("dd/MM/yyyy", LOCALE).apply {
            isLenient = false
            if (utc) timeZone = TimeZone.getTimeZone("UTC")
        }

    fun hoy(): String = formato().format(Date())

    fun ahora(): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", LOCALE).format(Date())

    // Medianoche local de la fecha, o null si el texto no es una fecha valida
    fun parsear(texto: String): Long? =
        try {
            formato().parse(texto)?.time
        } catch (e: ParseException) {
            null
        }

    /** Dias desde hoy hasta la fecha (negativo = ya pasó). Null si el texto no es una fecha. */
    fun diasDesdeHoy(texto: String): Int? {
        val fecha = parsear(texto) ?: return null
        val hoy = parsear(hoy()) ?: return null
        return Math.round((fecha - hoy) / 86_400_000.0).toInt()
    }

    // El DatePicker de Material 3 trabaja con medianoche UTC
    fun aMillisUtc(texto: String): Long? =
        try {
            formato(utc = true).parse(texto)?.time
        } catch (e: ParseException) {
            null
        }

    fun desdeMillisUtc(millis: Long): String =
        formato(utc = true).format(Date(millis))

} // fin object Fechas
