package eu.inscico.aurora_app.ui.screens.home.consumptionSummary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.himanshoe.charty.bar.model.StackedBarData
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import eu.inscico.aurora_app.model.consumptionSummary.ConsumptionSummary
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.services.ConsumptionSummaryService
import eu.inscico.aurora_app.utils.CalendarUtils
import java.util.Calendar

class ConsumptionSummaryViewModel(
    private val _consumptionSummaryService: ConsumptionSummaryService
) : ViewModel() {

    val allConsumptionSummariesLive = _consumptionSummaryService.consumptionSummariesLive
    val selectedSummaryBarChartData = MutableLiveData<List<StackedBarData>?>()

    fun getBarChartData(consumptionSummary: ConsumptionSummary?, isCarbonEmission: Boolean): List<StackedBarData> {

        val barChartData = mutableListOf<StackedBarData>()

        val sortedMonths = consumptionSummary?.months?.sortedBy { it.number }

        sortedMonths?.forEach {
            val month = Calendar.getInstance()
            month.set(Calendar.MONTH, it.number - 1)

            val monthElectricity = if(isCarbonEmission){
                it.categories.find { it.category == ConsumptionType.ELECTRICITY }?.carbonEmission?.total?.toFloat() ?: 0f
            } else {
                it.categories.find { it.category == ConsumptionType.ELECTRICITY }?.energyExpended?.total?.toFloat() ?: 0f
            }

            val monthHeating = if(isCarbonEmission){
                it.categories.find { it.category == ConsumptionType.HEATING }?.carbonEmission?.total?.toFloat() ?: 0f
            } else {
                it.categories.find { it.category == ConsumptionType.HEATING }?.energyExpended?.total?.toFloat() ?: 0f
            }

            val monthTransportation = if(isCarbonEmission){
                it.categories.find { it.category == ConsumptionType.TRANSPORTATION }?.carbonEmission?.total?.toFloat() ?: 0f
            } else {
                it.categories.find { it.category == ConsumptionType.TRANSPORTATION }?.energyExpended?.total?.toFloat() ?: 0f
            }

            val yValueList = listOf<Float>(monthHeating, monthElectricity, monthTransportation)

            val data = StackedBarData(
                xValue = CalendarUtils.toDateString(month, "MMM"),
                yValue = yValueList
            )
            barChartData.add(data)
        }
        selectedSummaryBarChartData.postValue(barChartData.toList())
        return barChartData
    }

    fun getBarChartDataNewChart(consumptionSummary: ConsumptionSummary?, isCarbonEmission: Boolean): ChartEntryModel {

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