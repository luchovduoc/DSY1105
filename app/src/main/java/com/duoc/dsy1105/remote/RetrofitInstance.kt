package com.duoc.dsy1105.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton que contiene la configuración de Retrofit
object RetrofitInstance {

    // Desde el emulador, 10.0.2.2 apunta al PC donde corre el backend
    val api: ApiService by lazy {
        Retrofit.Builder()
            // En dispositivo físico se usa 127.0.0.1 mediante adb reverse al puerto 8080
            .baseUrl("http://127.0.0.1:8080/")
            .addConverterFactory(GsonConverterFactory.create()) // Conversor JSON
            .build()
            .create(ApiService::class.java) // Implementa la interfaz ApiService
    }

} // fin object RetrofitInstance
