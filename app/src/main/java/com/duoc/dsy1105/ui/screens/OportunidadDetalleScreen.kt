package com.duoc.dsy1105.ui.screens

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.duoc.dsy1105.database.OportunidadEntity
import com.duoc.dsy1105.viewmodel.OportunidadViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadDetalleScreen(
    navController: NavController,
    oportunidadId: Long
) {
    val viewModel: OportunidadViewModel = viewModel()
    val oportunidades by viewModel.oportunidades.collectAsState()
    val oportunidad = oportunidades.find { it.id == oportunidadId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Oportunidad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        } // fin topBar
    ) { innerPadding ->

        if (oportunidad == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Oportunidad no encontrada")
            } // fin Box
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Datos principales
                Text(
                    text = oportunidad.nombre,
                    style = MaterialTheme.typography.headlineSmall
                )

                InfoRow("Tipo", oportunidad.tipoOportunidad)
                InfoRow("Prioridad", oportunidad.nivelPrioridad)
                InfoRow("Estado", oportunidad.estado)
                InfoRow("Fuente", oportunidad.fuente)
                InfoRow("Potencial", oportunidad.potencialComercial)

                if (oportunidad.observaciones.isNotEmpty()) {
                    InfoRow("Observaciones", oportunidad.observaciones)
                }

                HorizontalDivider()

                // Foto de evidencia
                if (oportunidad.fotoUri.isNotEmpty()) {
                    Text(
                        text = "Foto de evidencia",
                        style = MaterialTheme.typography.titleMedium
                    )
                    AsyncImage(
                        model = oportunidad.fotoUri,
                        contentDescription = "Foto de la oportunidad",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                } // fin if foto

                HorizontalDivider()

                // QR generado
                Text(
                    text = "Código QR",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Escanea este QR para abrir directamente esta oportunidad",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val qrBitmap = generarQr("oportunidad:${oportunidad.id}")
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR de la oportunidad",
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } // fin if qr

                Spacer(modifier = Modifier.height(8.dp))

                // Boton editar
                Button(
                    onClick = {
                        navController.navigate("oportunidad_form?id=${oportunidad.id}")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar oportunidad")
                } // fin Button editar

            } // fin Column
        } // fin else

    } // fin Scaffold

} // fin OportunidadDetalleScreen

@Composable
fun InfoRow(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium
        )
    } // fin Row
} // fin InfoRow

fun generarQr(contenido: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(contenido, BarcodeFormat.QR_CODE, 512, 512)
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
} // fin generarQr