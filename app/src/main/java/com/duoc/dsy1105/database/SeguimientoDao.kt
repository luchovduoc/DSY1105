package com.duoc.dsy1105.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguimientoDao {

    @Insert
    suspend fun insertar(seguimiento: SeguimientoEntity)

    // Mas reciente primero
    @Query("SELECT * FROM seguimientos WHERE oportunidadId = :oportunidadId ORDER BY id DESC")
    fun obtenerPorOportunidad(oportunidadId: Long): Flow<List<SeguimientoEntity>>

    @Query("SELECT * FROM seguimientos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<SeguimientoEntity>>

    // EN PAUSA: conteo para eliminar cuentas
    // // Para no eliminar una cuenta que registro acciones en el historial
    // @Query("SELECT COUNT(*) FROM seguimientos WHERE usuarioId = :usuarioId")
    // suspend fun contarPorUsuario(usuarioId: Long): Int

    // EN PAUSA: conteo para la comprobacion 'en uso'
    // // Para no eliminar un contacto que figura en el historial
    // @Query("SELECT COUNT(*) FROM seguimientos WHERE contactoId = :contactoId")
    // suspend fun contarPorContacto(contactoId: Long): Int

    // Al eliminar una oportunidad se elimina tambien su historial
    @Query("DELETE FROM seguimientos WHERE oportunidadId = :oportunidadId")
    suspend fun eliminarDeOportunidad(oportunidadId: Long)

} // fin interface SeguimientoDao
