package com.duoc.dsy1105.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Insert
    suspend fun insertar(contacto: ContactoEntity)

    @Update
    suspend fun actualizar(contacto: ContactoEntity)

    @Delete
    suspend fun eliminar(contacto: ContactoEntity)

    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ContactoEntity>>

    @Query("SELECT * FROM contactos WHERE id = :id")
    fun obtenerPorId(id: Long): Flow<ContactoEntity?>

    // EN PAUSA: conteo para la comprobacion 'en uso'
    // // Para no eliminar una organizacion que todavia tiene contactos
    // @Query("SELECT COUNT(*) FROM contactos WHERE organizacionId = :organizacionId")
    // suspend fun contarPorOrganizacion(organizacionId: Long): Int

    // Lo usan los seguimientos para dejar registrada la fecha del último contacto
    @Query("UPDATE contactos SET fechaUltimoContacto = :fecha WHERE id = :id")
    suspend fun actualizarUltimoContacto(id: Long, fecha: String)

} // fin interface ContactoDao
