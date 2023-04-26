package eu.inscico.aurora_app.services.pvgis

import androidx.lifecycle.MutableLiveData
import eu.inscico.aurora_app.BuildConfig
import eu.inscico.aurora_app.model.photovoltaics.PVGISInvestmentResponse
import eu.inscico.aurora_app.model.photovoltaics.PhotovoltaicInvestmentResult
import eu.inscico.aurora_app.services.jsonParsing.JsonParsingService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class PVGISAPIService(
    private val _jsonParsingService: JsonParsingService
) {

    val host = "https://re.jrc.ec.europa.eu/"

    private val httpClient: OkHttpClient
        get() {
            val okHttpClientBuilder = OkHttpClient.Builder()

            // Timeouts
            okHttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS)
            okHttpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
            okHttpClientBuilder.writeTimeout(30, TimeUnit.SECONDS)

            // add logger for debug builds
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                okHttpClientBuilder.addInterceptor(interceptor)
            }

            return okHttpClientBuilder.build()
        }

    // Retrofit
    private val retrofitApi = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(host)
        .addConverterFactory(_jsonParsingService.converterFactory)
        .addCallAdapterFactory(MyCallAdapterFactory())
        .build()
        .create(PVGISAPIDescription::class.java)


// region: API - Authentication
// ---------------------------------------------------------------------------------------------

    suspend fun calculatePVInvestment(
        lat: Double,
        lon: Double,
        peakpower: Double,
        angle: Double,
        aspect: Double
    ): Response<PVGISInvestmentResponse> {

        val response = retrofitApi.calculatePVInvestment(lat, lon, peakpower, aspect, angle)
        return response
    }
// endregion

    val investmentResultLive = MutableLiveData<PhotovoltaicInvestmentResult?>()

    fun updateInvestmentResult(result: PhotovoltaicInvestmentResult?){
        investmentResultLive.postValue(result)
    }
}
