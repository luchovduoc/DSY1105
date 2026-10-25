package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.UsuarioEntity
import com.duoc.dsy1105.model.Usuario

class AuthRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).usuarioDao()

    // Login contra Room
    suspend fun login(email: String, clave: String): Usuario? {
        val entity = dao.login(email, clave)
        return entity?.let {
            Usuario(
                id = it.id.toInt(),
                nombre = it.nombre,
                email = it.email,
                perfil = it.perfil,
                area = it.area,
                estado = true
            )
        }
    } // fin login

    // Registrar nuevo usuario
    suspend fun registrar(
        nombre: String,
        email: String,
        clave: String,
        perfil: String = "Ejecutivo Comercial",
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
                perfil = perfil,
                area = area
            )
        )
        return true
    } // fin registrar

} // fin AuthRepository