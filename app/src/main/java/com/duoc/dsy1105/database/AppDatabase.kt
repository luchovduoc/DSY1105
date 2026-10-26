package com.duoc.dsy1105.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [OportunidadEntity::class, UsuarioEntity::class, OrganizacionEntity::class],
    version = 5
) // fin @Database
abstract class AppDatabase : RoomDatabase() {

    abstract fun oportunidadDao(): OportunidadDao
    abstract fun usuarioDao(): UsuarioDao

    abstract fun organizacionDao(): OrganizacionDao

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
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build().also { INSTANCE = it }
            } // fin synchronized
        } // fin getDatabase

    } // fin companion object

} // fin AppDatabase