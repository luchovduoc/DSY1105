package com.duoc.dsy1105.repository

import android.content.Context
import com.duoc.dsy1105.database.AppDatabase
import com.duoc.dsy1105.database.OportunidadEntity
import kotlinx.coroutines.flow.Flow

class OportunidadRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).oportunidadDao()

    fun obtenerTodas(): Flow<List<OportunidadEntity>> {
        return dao.obtenerTodas()
    }

    fun obtenerPorEstado(estado: String): Flow<List<OportunidadEntity>> {
        return dao.obtenerPorEstado(estado)
    }

    fun obtenerPorId(id: Long): Flow<OportunidadEntity?> {
        return dao.obtenerPorId(id)
    } // fin obtenerPorId

    suspend fun insertar(oportunidad: OportunidadEntity) {
        dao.insertar(oportunidad)
    }

    suspend fun actualizar(oportunidad: OportunidadEntity) {
        dao.actualizar(oportunidad)
    }

    suspend fun eliminar(oportunidad: OportunidadEntity) {
        dao.eliminar(oportunidad)
    }
}

