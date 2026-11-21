package com.duoc.dsy1105.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.duoc.dsy1105.ui.components.CampoTexto
import org.junit.Rule
import org.junit.Test

/**
 * Prueba de pantalla (Compose UI Test): el patrón de validación del profesor.
 * Un campo con error muestra su mensaje con supportingText. Necesita emulador o teléfono.
 */
class CampoTextoTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun un_campo_con_error_muestra_su_mensaje() {
        composeRule.setContent {
            MaterialTheme {
                CampoTexto(
                    etiqueta = "Nombre",
                    valor = "",
                    onValorChange = {},
                    error = "El nombre es obligatorio"
                )
            }
        }

        composeRule.onNodeWithText("El nombre es obligatorio").assertIsDisplayed()
    }
}
