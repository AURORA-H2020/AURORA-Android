package eu.inscico.aurora_app.ui.screens.home.consumptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.services.firebase.ConsumptionsService
import eu.inscico.aurora_app.utils.TypedResult

class ConsumptionDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val _consumptionService: ConsumptionsService
) : ViewModel(){

    private val selectedConsumptionId: String = savedStateHandle["id"] ?: ""

    val selectedConsumption = getConsumptionById(selectedConsumptionId)

    private fun getConsumptionById(id: String): LiveData<Consumption?>{

        val consumption = _consumptionService.userConsumptionsLive.value?.firstOrNull {
            when(it){
                is Consumption.ElectricityConsumption -> it.id == id
                is Consumption.HeatingConsumption -> it.id == id
                is Consumption.TransportationConsumption -> it.id == id
            }
        }
        val liveData = MutableLiveData<Consumption?>()
        liveData.postValue(consumption)
        return liveData as LiveData<Consumption?>
    }

    suspend fun deleteConsumption(consumption: Consumption): TypedResult<Any, Any> {
        return _consumptionService.deleteConsumption(consumption)
    }

}