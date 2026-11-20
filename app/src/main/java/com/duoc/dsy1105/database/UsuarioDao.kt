package com.duoc.dsy1105.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity)

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    // EN PAUSA: eliminar cuentas
    // @Delete
    // suspend fun eliminar(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE email = :email AND clave = :clave LIMIT 1")
    suspend fun login(email: String, clave: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios WHERE perfil = :perfil AND activo = 1")
    suspend fun contarActivosConPerfil(perfil: String): Int

    @Query("SELECT * FROM usuarios ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<UsuarioEntity>>

} // fin interface UsuarioDao
