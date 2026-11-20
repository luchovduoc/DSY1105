package com.duoc.dsy1105.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {

    @Insert
    suspend fun insertar(evento: EventoEntity)

    @Update
    suspend fun actualizar(evento: EventoEntity)

    @Delete
    suspend fun eliminar(evento: EventoEntity)

    @Query("SELECT * FROM eventos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<EventoEntity>>

    @Query("SELECT * FROM eventos WHERE id = :id")
    fun obtenerPorId(id: Long): Flow<EventoEntity?>

    // EN PAUSA: conteo para la comprobacion 'en uso'
    // // Para no eliminar una organizacion que todavia tiene eventos
    // @Query("SELECT COUNT(*) FROM eventos WHERE organizacionId = :organizacionId")
    // suspend fun contarPorOrganizacion(organizacionId: Long): Int

} // fin interface EventoDao
