package com.duoc.dsy1105.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FeriadoRetrofitInstance {

    val api: FeriadoApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://date.nager.at/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeriadoApiService::class.java)
    }
}
