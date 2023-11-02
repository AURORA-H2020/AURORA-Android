package eu.inscico.aurora_app.ui.screens.home.consumptions

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ConsumptionType.Companion.getDisplayName
import eu.inscico.aurora_app.services.firebase.ConsumptionsService
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.utils.CalendarUtils

class AllConsumptionsListViewModel(
    private val _consumptionService: ConsumptionsService,
    private val _unitService: UnitService
): ViewModel() {

    val userConsumptions = _consumptionService.userConsumptionsLive.map {
        it?.sortedByDescending {
           when(it){
               is Consumption.ElectricityConsumption -> it.updatedAt
               is Consumption.HeatingConsumption -> it.updatedAt
               is Consumption.TransportationConsumption -> it.updatedAt
           }
        }
    }

    val searchResults = MutableLiveData<List<Consumption>?>()

    fun searchForResults(config: Configuration, allConsumptions: List<Consumption>?, context: Context, query: String = ""){
        val searchQuery = query.toLowerCase()

        val results = mutableListOf<Consumption>()
        if(searchQuery.isEmpty()){
            searchResults.postValue(allConsumptions)
            return
        }

        allConsumptions?.let { userConsumptions ->
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
                        if(getConsumptionTimeString(config, consumption).contains(searchQuery)){
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
                        if(getConsumptionTimeString(config, consumption).contains(searchQuery)){
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
                        if(getConsumptionTimeString(config, consumption).contains(searchQuery)){
                            results.add(consumption)
                            continue
                        }
                    }
                }
            }
        }
        val sortedResults = results.sortedByDescending {
            when (it) {
                is Consumption.ElectricityConsumption -> it.updatedAt
                is Consumption.HeatingConsumption -> it.updatedAt
                is Consumption.TransportationConsumption -> it.updatedAt
            }
        }
        searchResults.postValue(sortedResults)
    }

    private fun getConsumptionTimeString(config: Configuration, consumption: Consumption): String {
        return when (consumption) {
            is Consumption.ElectricityConsumption -> "${CalendarUtils.toDateString(consumption.electricity.startDate, _unitService.getDateFormat(config))} - ${CalendarUtils.toDateString(consumption.electricity.endDate, _unitService.getDateFormat(config))}"
            is Consumption.HeatingConsumption -> "${CalendarUtils.toDateString(consumption.heating.startDate, _unitService.getDateFormat(config))} - ${CalendarUtils.toDateString(consumption.heating.endDate, _unitService.getDateFormat(config))}"
            is Consumption.TransportationConsumption -> {
                CalendarUtils.toDateString(consumption.transportation.dateOfTravel, _unitService.getDateFormat(config, true))
            }
        }
    }
}