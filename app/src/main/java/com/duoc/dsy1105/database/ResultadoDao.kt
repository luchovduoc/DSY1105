package com.duoc.dsy1105.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultadoDao {

    @Insert
    suspend fun insertar(resultado: ResultadoEntity)

    @Update
    suspend fun actualizar(resultado: ResultadoEntity)

    @Delete
    suspend fun eliminar(resultado: ResultadoEntity)

    @Query("SELECT * FROM resultados ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ResultadoEntity>>

    @Query("SELECT * FROM resultados WHERE id = :id")
    fun obtenerPorId(id: Long): Flow<ResultadoEntity?>

} // fin interface ResultadoDao
