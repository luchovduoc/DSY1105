package com.duoc.dsy1105.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.dsy1105.viewmodel.OrganizacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionFormScreen(navController: NavController) {

    val viewModel: OrganizacionViewModel = viewModel()
    val nombre by viewModel.nombre.collectAsState()
    val tipoOrganizacion by viewModel.tipoOrganizacion.collectAsState()
    val rubro by viewModel.rubro.collectAsState()
    val comuna by viewModel.comuna.collectAsState()
    val region by viewModel.region.collectAsState()
    val observaciones by viewModel.observaciones.collectAsState()
    val guardadoExitosamente by viewModel.guardadoExitosamente.collectAsState()
    var regionExpandida by remember { mutableStateOf(false) }
    var comunaExpandida by remember { mutableStateOf(false) }

    val regiones = listOf(
        "Arica y Parinacota",
        "Tarapacá",
        "Antofagasta",
        "Atacama",
        "Coquimbo",
        "Valparaíso",
        "Región Metropolitana de Santiago",
        "Libertador General Bernardo O'Higgins",
        "Maule",
        "Ñuble",
        "Biobío",
        "La Araucanía",
        "Los Ríos",
        "Los Lagos",
        "Aysén del General Carlos Ibáñez del Campo",
        "Magallanes y de la Antártica Chilena"
    )

    val comunasMetropolitana = listOf(
        "Alhué",
        "Buin",
        "Calera de Tango",
        "Cerrillos",
        "Cerro Navia",
        "Colina",
        "Conchalí",
        "Curacaví",
        "El Bosque",
        "El Monte",
        "Estación Central",
        "Huechuraba",
        "Independencia",
        "Isla de Maipo",
        "La Cisterna",
        "La Florida",
        "La Granja",
        "La Pintana",
        "La Reina",
        "Lampa",
        "Las Condes",
        "Lo Barnechea",
        "Lo Espejo",
        "Lo Prado",
        "Macul",
        "Maipú",
        "María Pinto",
        "Melipilla",
        "Ñuñoa",
        "Padre Hurtado",
        "Paine",
        "Pedro Aguirre Cerda",
        "Peñaflor",
        "Peñalolén",
        "Pirque",
        "Providencia",
        "Pudahuel",
        "Puente Alto",
        "Quilicura",
        "Quinta Normal",
        "Recoleta",
        "Renca",
        "San Bernardo",
        "San Joaquín",
        "San José de Maipo",
        "San Miguel",
        "San Pedro",
        "San Ramón",
        "Santiago",
        "Talagante",
        "Tiltil",
        "Vitacura"
    )

    LaunchedEffect(guardadoExitosamente) {
        if (guardadoExitosamente) {
            viewModel.limpiarFormulario()
            navController.popBackStack()
        }
    } // fin LaunchedEffect

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Organización") },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tipoOrganizacion,
                onValueChange = { viewModel.onTipoChange(it) },
                label = { Text("Tipo (Empresa, Municipalidad, etc.)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rubro,
                onValueChange = { viewModel.onRubroChange(it) },
                label = { Text("Rubro") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = regionExpandida,
                onExpandedChange = { regionExpandida = !regionExpandida }
            ) {
                OutlinedTextField(
                    value = region,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Región") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = regionExpandida
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = regionExpandida,
                    onDismissRequest = { regionExpandida = false }
                ) {
                    regiones.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                viewModel.onRegionChange(opcion)
                                regionExpandida = false
                            }
                        )
                    }
                }
            }

            if (region == "Región Metropolitana de Santiago") {

                ExposedDropdownMenuBox(
                    expanded = comunaExpandida,
                    onExpandedChange = { comunaExpandida = !comunaExpandida }
                ) {
                    OutlinedTextField(
                        value = comuna,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Comuna") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = comunaExpandida
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = comunaExpandida,
                        onDismissRequest = { comunaExpandida = false }
                    ) {
                        comunasMetropolitana.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    viewModel.onComunaChange(opcion)
                                    comunaExpandida = false
                                }
                            )
                        }
                    }
                }

            } else if (region.isNotBlank()) {

                OutlinedTextField(
                    value = comuna,
                    onValueChange = { viewModel.onComunaChange(it) },
                    label = { Text("Comuna") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = observaciones,
                onValueChange = { viewModel.onObservacionesChange(it) },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.guardar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            } // fin Button

        } // fin Column

    } // fin Scaffold

} // fin OrganizacionFormScreen

