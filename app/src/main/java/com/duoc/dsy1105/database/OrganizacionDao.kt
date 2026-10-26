package com.duoc.dsy1105.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizacionDao {

    @Insert
    suspend fun insertar(organizacion: OrganizacionEntity)

    @Update
    suspend fun actualizar(organizacion: OrganizacionEntity)

    @Delete
    suspend fun eliminar(organizacion: OrganizacionEntity)

    @Query("SELECT * FROM organizaciones ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<OrganizacionEntity>>

    @Query("SELECT * FROM organizaciones WHERE id = :id")
    fun obtenerPorId(id: Long): Flow<OrganizacionEntity?>

} // fin interface OrganizacionDao

