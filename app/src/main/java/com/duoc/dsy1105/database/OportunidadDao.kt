package com.duoc.dsy1105.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {

    @Insert
    suspend fun insertar(oportunidad: OportunidadEntity)

    @Update
    suspend fun actualizar(oportunidad: OportunidadEntity)

    @Delete
    suspend fun eliminar(oportunidad: OportunidadEntity)

    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<OportunidadEntity>>

    @Query("SELECT * FROM oportunidades WHERE id = :id")
    fun obtenerPorId(id: Long): Flow<OportunidadEntity?>

    @Query("SELECT * FROM oportunidades WHERE estado = :estado ORDER BY id DESC")
    fun obtenerPorEstado(estado: String): Flow<List<OportunidadEntity>>
}

