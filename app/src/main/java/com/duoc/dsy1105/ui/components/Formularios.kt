package com.duoc.dsy1105.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.duoc.dsy1105.utils.DatosChile
import com.duoc.dsy1105.utils.Fechas

/** Campo de texto con el patron del profesor: isError + supportingText + icono de error. */
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    minLines: Int = 1,
    teclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        trailingIcon = {
            if (error != null) {
                Icon(Icons.Filled.ErrorOutline, contentDescription = "Error")
            }
        },
        enabled = enabled,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = modifier.fillMaxWidth()
    )
} // fin CampoTexto

/**
 * Selector desplegable generico. El error se muestra con isError + supportingText,
 * igual que los campos de texto del profesor.
 *
 * - [seleccionado] null = sin selección (se muestra [textoVacio]).
 * - [permiteVacio] agrega la opcion para volver a "sin selección".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectorEntidad(
    etiqueta: String,
    seleccionado: T?,
    opciones: List<T>,
    textoDe: (T) -> String,
    onSeleccion: (T?) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    textoVacio: String = "",
    permiteVacio: Boolean = false
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido && enabled,
        onExpandedChange = { if (enabled) expandido = !expandido },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = seleccionado?.let(textoDe) ?: textoVacio,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(etiqueta) },
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expandido && enabled,
            onDismissRequest = { expandido = false }
        ) {
            if (permiteVacio) {
                DropdownMenuItem(
                    text = { Text(if (textoVacio.isBlank()) "Ninguno" else textoVacio) },
                    onClick = {
                        onSeleccion(null)
                        expandido = false
                    }
                )
            }
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(textoDe(opcion)) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
} // fin SelectorEntidad

/** Selector de texto: el valor vacio ("") significa sin selección. */
@Composable
fun SelectorDesplegable(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    permiteVacio: Boolean = false,
    textoVacio: String = ""
) {
    SelectorEntidad(
        etiqueta = etiqueta,
        seleccionado = valor.ifBlank { null },
        opciones = opciones,
        textoDe = { it },
        onSeleccion = { onSeleccion(it ?: "") },
        modifier = modifier,
        error = error,
        enabled = enabled,
        textoVacio = textoVacio,
        permiteVacio = permiteVacio
    )
} // fin SelectorDesplegable

/** Campo de fecha con calendario (DatePicker). Guarda el texto "dd/MM/yyyy". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
    etiqueta: String,
    valor: String,
    onFechaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true
) {
    var mostrarCalendario by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = {},
        readOnly = true,
        enabled = enabled,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        trailingIcon = {
            IconButton(onClick = { mostrarCalendario = true }, enabled = enabled) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Elegir fecha")
            }
        },
        modifier = modifier.fillMaxWidth()
    )

    if (mostrarCalendario) {
        val estadoCalendario = rememberDatePickerState(
            initialSelectedDateMillis = Fechas.aMillisUtc(valor)
        )
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoCalendario.selectedDateMillis?.let {
                        onFechaSeleccionada(Fechas.desdeMillisUtc(it))
                    }
                    mostrarCalendario = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = estadoCalendario)
        }
    }
} // fin CampoFecha

/**
 * Region y comuna. Para la Región Metropolitana se ofrece la lista de comunas;
 * para las demás regiones la comuna se escribe a mano.
 */
@Composable
fun SelectorRegionComuna(
    region: String,
    comuna: String,
    onRegionChange: (String) -> Unit,
    onComunaChange: (String) -> Unit,
    errorRegion: String? = null,
    errorComuna: String? = null,
    enabled: Boolean = true
) {
    SelectorDesplegable(
        etiqueta = "Región",
        valor = region,
        opciones = DatosChile.regiones,
        onSeleccion = onRegionChange,
        error = errorRegion,
        enabled = enabled
    )

    if (region == DatosChile.REGION_METROPOLITANA) {
        SelectorDesplegable(
            etiqueta = "Comuna",
            valor = comuna,
            opciones = DatosChile.comunasMetropolitana,
            onSeleccion = onComunaChange,
            error = errorComuna,
            enabled = enabled
        )
    } else if (region.isNotBlank()) {
        OutlinedTextField(
            value = comuna,
            onValueChange = onComunaChange,
            label = { Text("Comuna") },
            isError = errorComuna != null,
            supportingText = { errorComuna?.let { Text(it) } },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
} // fin SelectorRegionComuna
