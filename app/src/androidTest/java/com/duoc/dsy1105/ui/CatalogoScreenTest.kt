package com.duoc.dsy1105.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.duoc.dsy1105.model.EventoRemoto
import com.duoc.dsy1105.repository.CatalogoRepository
import com.duoc.dsy1105.repository.OportunidadRepository
import com.duoc.dsy1105.ui.screens.CatalogoScreen
import com.duoc.dsy1105.viewmodel.CatalogoViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class CatalogoScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_nombre_del_evento_debe_aparecer_en_pantalla() {

        // Datos simulados
        val fakeEventos = listOf(
            EventoRemoto(id = "1", nombre = "Feria ficticia", tipo = "Feria empresarial"),
            EventoRemoto(id = "2", nombre = "Congreso ficticio", tipo = "Congreso")
        )

        // Simulamos el repositorio
        val fakeRepository = mockk<CatalogoRepository>()
        coEvery { fakeRepository.getEventos() } returns fakeEventos

        // Creamos el ViewModel con el repositorio simulado
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            as android.app.Application
        val fakeViewModel = CatalogoViewModel(app, fakeRepository, mockk<OportunidadRepository>())

        // Renderizamos la interfaz
        composeRule.setContent {
            CatalogoScreen(viewModel = fakeViewModel, usuario = null, onVolver = {})
        }

        // Esperamos que aparezcan los nombres
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodes(hasText("Evento: Feria ficticia")).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Evento: Feria ficticia").assertIsDisplayed()
        composeRule.onNodeWithText("Evento: Congreso ficticio").assertIsDisplayed()
    }
}
