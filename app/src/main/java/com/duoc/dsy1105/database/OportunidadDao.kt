package com.duoc.dsy1105.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {

    @Insert
    suspend fun insertar(oportunidad: OportunidadEntity): Long

    @Update
    suspend fun actualizar(oportunidad: OportunidadEntity)

    @Delete
    suspend fun eliminar(oportunidad: OportunidadEntity)

    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<OportunidadEntity>>

    @Query("SELECT * FROM oportunidades WHERE id = :id")
    fun obtenerPorId(id: Long): Flow<OportunidadEntity?>

    // EN PAUSA: conteo para eliminar cuentas
    // // Para no eliminar una cuenta que tiene oportunidades a su cargo
    // @Query("SELECT COUNT(*) FROM oportunidades WHERE responsableId = :usuarioId")
    // suspend fun contarPorResponsable(usuarioId: Long): Int

    // EN PAUSA: conteo para la comprobacion 'en uso'
    // // Para no eliminar un evento que todavia tiene oportunidades
    // @Query("SELECT COUNT(*) FROM oportunidades WHERE eventoId = :eventoId")
    // suspend fun contarPorEvento(eventoId: Long): Int

    // Lectura puntual (no reactiva): la usa el repositorio para validar permisos
    @Query("SELECT * FROM oportunidades WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): OportunidadEntity?

}

