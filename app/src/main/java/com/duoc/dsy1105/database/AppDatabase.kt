package com.duoc.dsy1105.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        OportunidadEntity::class,
        UsuarioEntity::class,
        OrganizacionEntity::class,
        EventoEntity::class,
        ContactoEntity::class,
        SeguimientoEntity::class,
        ActivacionEntity::class,
        ResultadoEntity::class
    ],
    version = 1
) // fin @Database
abstract class AppDatabase : RoomDatabase() {

    abstract fun oportunidadDao(): OportunidadDao
    abstract fun usuarioDao(): UsuarioDao

    abstract fun organizacionDao(): OrganizacionDao

    abstract fun eventoDao(): EventoDao

    abstract fun contactoDao(): ContactoDao

    abstract fun seguimientoDao(): SeguimientoDao

    abstract fun activacionDao(): ActivacionDao

    abstract fun resultadoDao(): ResultadoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dsy1105_database"
                )
                    // Esquema desde cero (version 1). Si el telefono trae una version anterior
                    // (con un numero mayor), se recrea la base: son datos de prueba.
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build().also { INSTANCE = it }
            } // fin synchronized
        } // fin getDatabase

    } // fin companion object

} // fin AppDatabase
