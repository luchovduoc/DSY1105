package com.duoc.dsy1105.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.duoc.dsy1105.model.Perfil

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
)
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
                    .addCallback(object : Callback() {

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)

                            insertarUsuarioSiNoExiste(
                                db = db,
                                nombre = DatosSemilla.SUPERVISOR_NOMBRE,
                                email = DatosSemilla.SUPERVISOR_EMAIL,
                                perfil = Perfil.SUPERVISOR.etiqueta
                            )

                            insertarUsuarioSiNoExiste(
                                db = db,
                                nombre = DatosSemilla.EJECUTIVO_NOMBRE,
                                email = DatosSemilla.EJECUTIVO_EMAIL,
                                perfil = Perfil.EJECUTIVO.etiqueta
                            )

                            insertarUsuarioSiNoExiste(
                                db = db,
                                nombre = DatosSemilla.ACTIVACIONES_NOMBRE,
                                email = DatosSemilla.ACTIVACIONES_EMAIL,
                                perfil = Perfil.ACTIVACIONES.etiqueta
                            )
                        }
                    })
                    // Los datos de esta aplicación son de demostración.
                    // Si existe una base de una versión superior, Room puede recrearla.
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }

        private fun insertarUsuarioSiNoExiste(
            db: SupportSQLiteDatabase,
            nombre: String,
            email: String,
            perfil: String
        ) {
            db.execSQL(
                """
                INSERT INTO usuarios (
                    nombre,
                    email,
                    clave,
                    perfil,
                    area,
                    activo
                )
                SELECT ?, ?, ?, ?, '', 1
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM usuarios
                    WHERE email = ?
                )
                """.trimIndent(),
                arrayOf(
                    nombre,
                    email,
                    DatosSemilla.CLAVE,
                    perfil,
                    email
                )
            )
        }

    }

}