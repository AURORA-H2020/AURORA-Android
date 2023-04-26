package eu.inscico.aurora_app.services.pvgis

import eu.inscico.aurora_app.model.photovoltaics.PVGISInvestmentResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PVGISAPIDescription {

    @GET("api/v5_2/PVcalc")
    suspend fun calculatePVInvestment(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("peakpower") peakpower: Double,
        @Query("aspect") aspect: Double,
        @Query("angle") angle: Double,
        @Query("loss") loss: Double = 0.14,
        @Query("pvtechchoice") pvtechchoice: String = "crystSi",
        @Query("mountingplace") mountingplace: String = "free",
        @Query("raddatabase") raddatabase: String = "PVGIS-SARAH",
        @Query("outputformat") outputformat: String = "json",
    ): Response<PVGISInvestmentResponse>

}