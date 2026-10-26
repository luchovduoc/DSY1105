package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.OrganizacionEntity
import kotlinx.coroutines.flow.Flow

class OrganizacionRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).organizacionDao()

    fun obtenerTodas(): Flow<List<OrganizacionEntity>> {
        return dao.obtenerTodas()
    } // fin obtenerTodas

    fun obtenerPorId(id: Long): Flow<OrganizacionEntity?> {
        return dao.obtenerPorId(id)
    } // fin obtenerPorId

    suspend fun insertar(organizacion: OrganizacionEntity) {
        dao.insertar(organizacion)
    } // fin insertar

    suspend fun actualizar(organizacion: OrganizacionEntity) {
        dao.actualizar(organizacion)
    } // fin actualizar

    suspend fun eliminar(organizacion: OrganizacionEntity) {
        dao.eliminar(organizacion)
    } // fin eliminar

} // fin OrganizacionRepository

