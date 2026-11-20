package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.DatosSemilla
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.Perfil
import com.duoc.dsy1105.model.Usuario

class AuthRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).usuarioDao()

    // Login contra Room. Si la cuenta está desactivada, Usuario.estado viene en false.
    suspend fun login(email: String, clave: String): Usuario? {
        val entity = dao.login(email, clave)
        return entity?.let {
            Usuario(
                id = it.id.toInt(),
                nombre = it.nombre,
                email = it.email,
                perfil = Perfil.desdeEtiqueta(it.perfil),
                area = it.area,
                estado = it.activo
            )
        }
    } // fin login

    // Registrar nuevo usuario: SIEMPRE como Ejecutivo.
    // El perfil lo cambia después un supervisor desde "Gestión de usuarios".
    suspend fun registrar(
        nombre: String,
        email: String,
        clave: String,
        area: String = ""
    ): Boolean {
        // Verificar si el email ya existe
        val existente = dao.buscarPorEmail(email)
        if (existente != null) return false

        dao.insertar(
            UsuarioEntity(
                nombre = nombre,
                email = email,
                clave = clave,
                perfil = Perfil.EJECUTIVO.etiqueta,
                area = area
            )
        )
        return true
    } // fin registrar

    // Crea los usuarios de demostración (datos ficticios, uno por perfil) si aún no existen
    suspend fun asegurarUsuariosDemo() {
        crearSiNoExiste(DatosSemilla.SUPERVISOR_NOMBRE, DatosSemilla.SUPERVISOR_EMAIL, Perfil.SUPERVISOR)
        crearSiNoExiste(DatosSemilla.EJECUTIVO_NOMBRE, DatosSemilla.EJECUTIVO_EMAIL, Perfil.EJECUTIVO)
        crearSiNoExiste(DatosSemilla.ACTIVACIONES_NOMBRE, DatosSemilla.ACTIVACIONES_EMAIL, Perfil.ACTIVACIONES)
    } // fin asegurarUsuariosDemo

    private suspend fun crearSiNoExiste(nombre: String, email: String, perfil: Perfil) {
        if (dao.buscarPorEmail(email) != null) return

        dao.insertar(
            UsuarioEntity(
                nombre = nombre,
                email = email,
                clave = DatosSemilla.CLAVE,
                perfil = perfil.etiqueta
            )
        )
    } // fin crearSiNoExiste

} // fin AuthRepository
