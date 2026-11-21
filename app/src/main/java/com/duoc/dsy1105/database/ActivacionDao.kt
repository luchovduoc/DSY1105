package com.duoc.dsy1105.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivacionDao {

    @Insert
    suspend fun insertar(activacion: ActivacionEntity)

    @Update
    suspend fun actualizar(activacion: ActivacionEntity)

    @Delete
    suspend fun eliminar(activacion: ActivacionEntity)

    @Query("SELECT * FROM activaciones ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<ActivacionEntity>>

    @Query("SELECT * FROM activaciones WHERE id = :id")
    fun obtenerPorId(id: Long): Flow<ActivacionEntity?>

} // fin interface ActivacionDao
