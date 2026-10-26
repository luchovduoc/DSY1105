package com.duoc.dsy1105.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [OportunidadEntity::class, UsuarioEntity::class, OrganizacionEntity::class],
    version = 6
) // fin @Database
abstract class AppDatabase : RoomDatabase() {

    abstract fun oportunidadDao(): OportunidadDao
    abstract fun usuarioDao(): UsuarioDao

    abstract fun organizacionDao(): OrganizacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS organizaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre TEXT NOT NULL,
                tipoOrganizacion TEXT NOT NULL,
                rubro TEXT NOT NULL,
                comuna TEXT NOT NULL,
                region TEXT NOT NULL,
                sitioWeb TEXT NOT NULL,
                publicoAsociado TEXT NOT NULL,
                observaciones TEXT NOT NULL
            )
            """.trimIndent()
                )
            }
        }
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE oportunidades ADD COLUMN latitud REAL"
                )
                database.execSQL(
                    "ALTER TABLE oportunidades ADD COLUMN longitud REAL"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dsy1105_database"
                )
                    .addMigrations(
                        MIGRATION_4_5,
                        MIGRATION_5_6
                    )
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build().also { INSTANCE = it }
            } // fin synchronized
        } // fin getDatabase

    } // fin companion object

} // fin AppDatabase