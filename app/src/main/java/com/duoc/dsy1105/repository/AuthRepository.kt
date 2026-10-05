package com.duoc.dsy1105.repository

import com.duoc.dsy1105.model.Usuario

class AuthRepository {

    // Lista ficticia de usuarios para simular la base de datos
    private val usuarios = listOf(
        Usuario(
            id = 1,
            nombre = "Juan Pérez",
            email = "juan.perez@agencia.cl",
            perfil = "Ejecutivo Comercial",
            area = "Ventas",
            estado = true
        ),
        Usuario(
            id = 2,
            nombre = "María González",
            email = "maria.gonzalez@agencia.cl",
            perfil = "Supervisor Comercial",
            area = "Comercial",
            estado = true
        )
    )

    fun login(email: String, password: String): Usuario? {
        // Por ahora validamos solo el email
        // cuando agreguemos Room validaremos contra la base de datos
        return usuarios.find { it.email == email }
    }
}

