package com.duoc.dsy1105.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de la capa database: Room real, pero en memoria (no toca los datos de la app).
 * Necesitan un emulador o teléfono porque Room usa SQLite de Android.
 */
@RunWith(AndroidJUnit4::class)
class BaseDeDatosTest {

    private lateinit var baseDeDatos: AppDatabase

    @Before
    fun crearBaseDeDatos() {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        baseDeDatos = Room.inMemoryDatabaseBuilder(contexto, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun cerrarBaseDeDatos() {
        baseDeDatos.close()
    }

    private fun evento(nombre: String, organizacionId: Long = 0) = EventoEntity(
        nombre = nombre,
        tipo = "Feria empresarial",
        fechaInicio = "10/11/2026",
        comuna = "Santiago",
        region = "Región Metropolitana de Santiago",
        organizacionId = organizacionId
    )

    @Test
    fun insertar_un_evento_y_leerlo_de_vuelta() = runBlocking {
        baseDeDatos.eventoDao().insertar(evento("Feria de prueba"))

        val eventos = baseDeDatos.eventoDao().obtenerTodos().first()

        assertEquals(1, eventos.size)
        assertEquals("Feria de prueba", eventos[0].nombre)
        assertEquals("10/11/2026", eventos[0].fechaInicio)
    }

    // EN PAUSA: contarPorOrganizacion (comprobacion 'en uso')
    @Test
    fun leer_un_evento_por_su_id() = runBlocking {
        val dao = baseDeDatos.eventoDao()
        dao.insertar(evento("Evento A"))
        val id = dao.obtenerTodos().first()[0].id

        val leido = dao.obtenerPorId(id).first()

        assertEquals("Evento A", leido?.nombre)
    }

    @Test
    fun buscar_un_usuario_por_correo() = runBlocking {
        val dao = baseDeDatos.usuarioDao()
        dao.insertar(
            UsuarioEntity(
                nombre = "Usuario de prueba",
                email = "prueba@ficticio.cl",
                clave = "12345"
            )
        )

        val encontrado = dao.buscarPorEmail("prueba@ficticio.cl")

        assertNotNull(encontrado)
        assertEquals("Usuario de prueba", encontrado?.nombre)
        assertNull(dao.buscarPorEmail("otro@ficticio.cl"))
    }
}
