package eu.inscico.aurora_app.ui.screens.photovoltaic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.photovoltaics.PVGISInvestmentResponse
import eu.inscico.aurora_app.model.photovoltaics.PhotovoltaicInvestmentResult
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.pvgis.PVGISAPIService
import org.koin.androidx.compose.get
import retrofit2.Response

class PhotovoltaicCalculatorViewModel(
    private val countriesService: CountriesService,
    private val pvgisApiService: PVGISAPIService
): ViewModel() {

    val userCity = countriesService.userCityLive

    val investmentResultLive = pvgisApiService.investmentResultLive

    suspend fun calculateInvestment(
        lat: Double,
        lon: Double,
        peakpower: Double,
        angle: Double,
        aspect: Double
    ): Response<PVGISInvestmentResponse> {
        return pvgisApiService.calculatePVInvestment(lat, lon, peakpower, angle, aspect)
    }

    fun updateInvestmentResult(result: PhotovoltaicInvestmentResult?){
        pvgisApiService.updateInvestmentResult(result)
    }

}