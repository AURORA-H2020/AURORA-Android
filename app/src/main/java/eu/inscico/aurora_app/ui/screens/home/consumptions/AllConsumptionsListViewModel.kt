package eu.inscico.aurora_app.ui.screens.home.consumptions

import android.content.Context
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.ConsumptionType.Companion.getDisplayName
import eu.inscico.aurora_app.services.ConsumptionsService
import eu.inscico.aurora_app.utils.CalendarUtils

class AllConsumptionsListViewModel(
    private val _consumptionService: ConsumptionsService
): ViewModel() {

    val userConsumptions = _consumptionService.userConsumptionsLive

    val searchResults = MutableLiveData<List<Consumption>?>()

    fun searchForResults(context: Context, query: String = ""){
        val searchQuery = query.toLowerCase()

        val results = mutableListOf<Consumption>()
        if(searchQuery.isEmpty()){
            searchResults.postValue(userConsumptions.value)
            return
        }

        userConsumptions.value?.let { userConsumptions ->
            for(consumption in userConsumptions){

                when(consumption){
                    is Consumption.ElectricityConsumption -> {
                        if(consumption.category.getDisplayName(context).toLowerCase().contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                        if(consumption.value.toString().contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                        if(consumption.description?.toLowerCase()?.contains(searchQuery) == true){
                            results.add(consumption)
                            continue
                        }
                        if(consumption.carbonEmissions.toString().contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                        if(getConsumptionTimeString(consumption).contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }

                    }
                    is Consumption.HeatingConsumption -> {
                        if(consumption.category.getDisplayName(context).toLowerCase().contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                        if(consumption.value.toString().contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                        if(consumption.description?.toLowerCase()?.contains(searchQuery) == true){
                            results.add(consumption)
                            continue
                        }
                        if(consumption.carbonEmissions.toString().contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                        if(getConsumptionTimeString(consumption).contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                    }
                    is Consumption.TransportationConsumption -> {
                        if(consumption.category.getDisplayName(context).toLowerCase().contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                        if(consumption.value.toString().contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                        if(consumption.description?.toLowerCase()?.contains(searchQuery) == true){
                            results.add(consumption)
                            continue
                        }
                        if(consumption.carbonEmissions.toString().contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                        if(getConsumptionTimeString(consumption).contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                    }
                }
            }
        }
        searchResults.postValue(results)
    }

    private fun getConsumptionTimeString(consumption: Consumption): String {
        return when (consumption) {
            is Consumption.ElectricityConsumption -> "${CalendarUtils.toDateString(consumption.electricity.startDate)} - ${CalendarUtils.toDateString(consumption.electricity.endDate)}"
            is Consumption.HeatingConsumption -> "${CalendarUtils.toDateString(consumption.heating.startDate, "dd.MM.yy")} - ${CalendarUtils.toDateString(consumption.heating.endDate, "dd.MM.yy")}"
            is Consumption.TransportationConsumption -> {
                CalendarUtils.toDateString(consumption.transportation.dateOfTravel, "dd.MM.yyyy, HH:mm")
            }
        }
    }
}