package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.pvPlant.PVPlantData
import eu.inscico.aurora_app.model.pvProductionChart.ChartTimeRangeType
import eu.inscico.aurora_app.model.pvProductionChart.DisplayOption
import eu.inscico.aurora_app.model.user.PVInvestment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar

class PvProductionBarChartViewModel: ViewModel() {

    val state = MutableStateFlow(PvProductionBarChartState())

    fun updateState(pvPlant: PVPlant, pvPlantData: List<PVPlantData>, userInvestments: List<PVInvestment>,){
        viewModelScope.launch {
            val firstInvestmentDate =
                userInvestments.minBy { it.investmentDate.timeInMillis }.investmentDate.toInstant()
                    .atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)
            val lastPlantDataProductionDate =
                pvPlantData.maxBy { it.date.timeInMillis }.date.toInstant()
                    .atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)

            state.emit(
                state.value.copy(
                    pvPlant = pvPlant,
                    pvPlantData = pvPlantData,
                    allUserInvestments = userInvestments,
                    firstInvestmentDate = firstInvestmentDate,
                    lastPlantDataProductionDate = lastPlantDataProductionDate,
                )
            )
        }
    }

    fun getReferenceTimeForDateParsing(data: List<PVPlantData>){
        viewModelScope.launch {
            val allDates = data.map { it.date.timeInMillis }
            val referenceTime = allDates.minOrNull() ?: 0L
            state.emit(state.value.copy(referenceTime = referenceTime))
        }
    }

    fun getBarChartDataForProductionType(productionType: DisplayOption, timeRangeType: ChartTimeRangeType, pvPlantData: List<PVPlantData>): ChartEntryModel {

        val dataList = when(productionType){
            DisplayOption.YOUR_PRODUCTION -> {
                getUserProductionValues()
            }
            DisplayOption.TOTAL_PRODUCTION -> {
                pvPlantData.map {
                    Pair(first = it.date.timeInMillis, second = it.Ep.toFloat())
                }
            }
        }

        val dataListForTimeRange = when(timeRangeType){
            ChartTimeRangeType.PAST_30_DAYS -> {
                if(dataList.lastIndex > 30) {
                    dataList.subList(dataList.size - 30, dataList.size).reversed()
                } else {
                    fillListForConcreteEntryCount(dataList).reversed()
                }
            }
            ChartTimeRangeType.SINCE_INVESTMENT -> {
                getListSinceInvestmentDate(list = dataList)
            }
        }

        val normalizedList = dataListForTimeRange.map {
            val normalizedTimeStamp = (it.first - state.value.referenceTime).toFloat() / state.value.millisPerDay
            Pair(first = normalizedTimeStamp, second = it.second)
        }

        val i = normalizedList.toTypedArray()
        return entryModelOf(*i)
    }

    private fun fillListForConcreteEntryCount(list: List<Pair<Long,Float>>, neededListSize: Long = 30): List<Pair<Long,Float>>{

        val daysBefore = ZonedDateTime.now().minusDays(neededListSize).truncatedTo(ChronoUnit.DAYS)
        val firstDateInList = Calendar.getInstance().apply { timeInMillis = list.minBy { it.first }.first }.toInstant().atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)

        val filledList = mutableListOf<Pair<Long,Float>>()
        filledList.addAll(list)

        var current = daysBefore
        while(current.isBefore(firstDateInList) ||current.isEqual(firstDateInList)){
            val currentTimeInMillis = current.plusHours(10).toInstant().toEpochMilli()
            filledList.add(0, Pair(currentTimeInMillis, 0f))
            current = current.plusDays(1)
        }
        return filledList
    }

    private fun getListSinceInvestmentDate( list: List<Pair<Long,Float>>): List<Pair<Long,Float>>{
        val firstInvestmentDate = state.value.firstInvestmentDate
        val lastPlantDataProductionDate = state.value.lastPlantDataProductionDate

        var index = 0
        var current = firstInvestmentDate
        while(current.isBefore(lastPlantDataProductionDate) || current.isEqual(lastPlantDataProductionDate)){
            index += 1
            current = current.plusDays(1)
        }

        val finalList = if(list.size >= index){
            list.subList(list.size - index, list.size)
        } else {
            fillListForConcreteEntryCount(list = list, neededListSize = index.toLong())
        }
        return finalList
    }

    private fun getUserProductionValues(): List<Pair<Long, Float>>{

        val userProductionPerDay = mutableListOf<Pair<Long, Float>>()

        val lastPlanProductionDate = state.value.lastPlantDataProductionDate
        val firstInvestmentDate = state.value.firstInvestmentDate
        val pvPlant = state.value.pvPlant
        val plantData = state.value.pvPlantData

        var current = firstInvestmentDate
        while (current.isBefore(lastPlanProductionDate) || current.isEqual(lastPlanProductionDate)) {
            var summedCapacityFromUserForThisDay = 0.0
            getAllPvInvestmentsForDay(current).forEach {
                summedCapacityFromUserForThisDay += (it.investmentCapacity ?: 0.0)
            }

            val proportion = summedCapacityFromUserForThisDay / (pvPlant?.capacity ?: 0.0)
            val producedEnergyForDay = plantData?.find {
                it.date.toInstant().atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS) == current
            }?.Ep ?: 0.0
            val value = (proportion / 100) * producedEnergyForDay
            userProductionPerDay.add(Pair(first = current.plusHours(11).toInstant().toEpochMilli(), second = value.toFloat()))

            current = current.plusDays(1)
        }
        return userProductionPerDay
    }

    private fun getAllPvInvestmentsForDay(currentDay: ZonedDateTime): List<PVInvestment>{
        val allPvInvestments = state.value.allUserInvestments
        val allInvestmentsForDay = mutableListOf<PVInvestment>()

        allPvInvestments?.forEach {
            val parsedInvestmentDate = it.investmentDate.toInstant().atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)
            if(parsedInvestmentDate.isBefore(currentDay) || parsedInvestmentDate.isEqual(currentDay)){
                allInvestmentsForDay.add(it)
            }
        }
        return allInvestmentsForDay

    }
}