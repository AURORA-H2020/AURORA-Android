package eu.inscico.aurora_app.ui.screens.home.consumptionSummary

import androidx.lifecycle.ViewModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import eu.inscico.aurora_app.model.consumptionSummary.ConsumptionSummary
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.services.ConsumptionSummaryService

class ConsumptionSummaryViewModel(
    private val _consumptionSummaryService: ConsumptionSummaryService
) : ViewModel() {

    val allConsumptionSummariesLive = _consumptionSummaryService.consumptionSummariesLive

    fun getBarChartData(consumptionSummary: ConsumptionSummary?, isCarbonEmission: Boolean): ChartEntryModel {

        val sortedMonths = consumptionSummary?.months?.sortedBy { it.number }

        val electricityPerMonth = mutableListOf<FloatEntry>()
        val heatingPerMonth = mutableListOf<FloatEntry>()
        val transportationPerMonth = mutableListOf<FloatEntry>()

        sortedMonths?.forEach {

            val monthElectricity = if(isCarbonEmission){
                it.categories.find { it.category == ConsumptionType.ELECTRICITY }?.carbonEmission?.total?.toFloat() ?: 0f
            } else {
                it.categories.find { it.category == ConsumptionType.ELECTRICITY }?.energyExpended?.total?.toFloat() ?: 0f
            }

            electricityPerMonth.add(FloatEntry(x = it.number.toFloat(), y = monthElectricity))

            val monthHeating = if(isCarbonEmission){
                it.categories.find { it.category == ConsumptionType.HEATING }?.carbonEmission?.total?.toFloat() ?: 0f
            } else {
                it.categories.find { it.category == ConsumptionType.HEATING }?.energyExpended?.total?.toFloat() ?: 0f
            }

            heatingPerMonth.add(FloatEntry(x = it.number.toFloat(), y = monthHeating))

            val monthTransportation = if(isCarbonEmission){
                it.categories.find { it.category == ConsumptionType.TRANSPORTATION }?.carbonEmission?.total?.toFloat() ?: 0f
            } else {
                it.categories.find { it.category == ConsumptionType.TRANSPORTATION }?.energyExpended?.total?.toFloat() ?: 0f
            }

            transportationPerMonth.add(FloatEntry(x = it.number.toFloat(), y = monthTransportation))
        }
        return entryModelOf(heatingPerMonth, electricityPerMonth, transportationPerMonth)
    }

}