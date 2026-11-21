package com.duoc.dsy1105.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Campo de búsqueda por texto, con botón para limpiarlo. */
@Composable
fun BarraBusqueda(
    texto: String,
    onTextoChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Buscar"
) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        label = { Text(etiqueta) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onTextoChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda")
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
} // fin BarraBusqueda

/** Fila de filtros que se desplaza horizontalmente si no caben todos. */
@Composable
fun FilaFiltros(contenido: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = contenido
    )
} // fin FilaFiltros

/**
 * Filtro desplegable. [seleccion] vacío = sin filtrar; al elegir una opción el chip
 * queda marcado y muestra el valor elegido.
 */
@Composable
fun FiltroChip(
    etiqueta: String,
    seleccion: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = seleccion.isNotBlank(),
            onClick = { expandido = true },
            label = { Text(if (seleccion.isBlank()) etiqueta else "$etiqueta: $seleccion") },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todos") },
                onClick = {
                    onSeleccion("")
                    expandido = false
                }
            )
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
} // fin FiltroChip
