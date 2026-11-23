package com.duoc.dsy1105.repository

import com.duoc.dsy1105.model.FeriadoRemoto
import com.duoc.dsy1105.remote.FeriadoRetrofitInstance

class FeriadoRepository {

    suspend fun obtenerFeriados(
        year: Int = 2026,
        countryCode: String = "CL"
    ): List<FeriadoRemoto> {
        return FeriadoRetrofitInstance.api.getFeriados(
            year = year,
            countryCode = countryCode
        )
    }
}