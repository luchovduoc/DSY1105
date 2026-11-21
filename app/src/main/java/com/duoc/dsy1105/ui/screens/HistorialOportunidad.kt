package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.dsy1105.database.SeguimientoEntity

/**
 * Historial de gestiones de una oportunidad: las acciones que registró el equipo
 * y los registros automáticos (creación y cambios de estado), del más reciente al más antiguo.
 */
@Composable
fun HistorialOportunidad(seguimientos: List<SeguimientoEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Historial de gestiones",
            style = MaterialTheme.typography.titleMedium
        )

        if (seguimientos.isEmpty()) {
            Text(
                text = "Aún no hay gestiones registradas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            seguimientos.forEach { seguimiento ->
                SeguimientoItem(seguimiento)
            }
        }
    }
} // fin HistorialOportunidad

@Composable
private fun SeguimientoItem(seguimiento: SeguimientoEntity) {
    // Los registros automaticos se ven mas discretos que las acciones del equipo
    val colores = if (seguimiento.automatico) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    } else {
        CardDefaults.cardColors()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = colores
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = seguimiento.tipoAccion,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = seguimiento.fecha,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "Registrado por ${seguimiento.usuarioNombre} · ${seguimiento.registradoEl}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (seguimiento.contactoNombre.isNotBlank()) {
                Text(
                    text = "Contacto: ${seguimiento.contactoNombre}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (seguimiento.resultado.isNotBlank()) {
                Text(
                    text = "Resultado: ${seguimiento.resultado}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (seguimiento.proximaAccion.isNotBlank() ||
                seguimiento.fechaProximoSeguimiento.isNotBlank()
            ) {
                Text(
                    text = "Próxima acción: " + listOf(
                        seguimiento.proximaAccion,
                        seguimiento.fechaProximoSeguimiento
                    ).filter { it.isNotBlank() }.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (seguimiento.observaciones.isNotBlank()) {
                Text(
                    text = seguimiento.observaciones,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
} // fin SeguimientoItem
