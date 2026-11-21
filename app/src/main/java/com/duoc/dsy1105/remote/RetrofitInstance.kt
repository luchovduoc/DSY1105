package com.duoc.dsy1105.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton que contiene la configuración de Retrofit
object RetrofitInstance {

    // Se instancia el servicio de la API una sola vez
    val api: ApiService by lazy {
        Retrofit.Builder()
            // En dispositivo físico se usa 127.0.0.1 mediante adb reverse al puerto 8080
            .baseUrl("http://127.0.0.1:8080/")
            .addConverterFactory(GsonConverterFactory.create()) // Conversor JSON
            .build()
            .create(ApiService::class.java) // Implementa la interfaz ApiService
    }

} // fin object RetrofitInstance
