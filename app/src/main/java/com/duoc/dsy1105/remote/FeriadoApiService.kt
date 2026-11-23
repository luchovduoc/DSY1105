package com.duoc.dsy1105.remote

import com.duoc.dsy1105.model.FeriadoRemoto
import retrofit2.http.GET
import retrofit2.http.Path

interface FeriadoApiService {

    @GET("api/v3/PublicHolidays/{year}/{countryCode}")
    suspend fun getFeriados(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<FeriadoRemoto>
}