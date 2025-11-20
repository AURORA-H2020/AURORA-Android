package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.pvPlant.PVPlantData
import eu.inscico.aurora_app.model.pvProductionChart.ChartTimeRangeType
import eu.inscico.aurora_app.model.pvProductionChart.DisplayOption
import eu.inscico.aurora_app.model.user.PVInvestment
import eu.inscico.aurora_app.services.firebase.PVPlantsService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar

class PvProductionChartViewModel(
    private val _userService: UserService,
    private val _pvPlantService: PVPlantsService
) : ViewModel() {

    val state = MutableStateFlow(PvProductionChartScreenState())

    fun updateChartDisplayOption(displayOption: DisplayOption) {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    selectedDisplayOption = displayOption
                )
            )
        }
    }

    fun updateChartTimeRange(timeRange: ChartTimeRangeType) {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    selectedChartTimeRange = timeRange
                )
            )
        }
    }

    fun fetchInvestmentsForUser() {

        viewModelScope.launch {

            val result = _userService.loadPVInvestmentsForUser()
            when (result) {
                is TypedResult.Failure -> {
                    state.emit(
                        state.value.copy(
                            isLoading = false
                        )
                    )
                }

                is TypedResult.Success -> {
                    state.emit(
                        state.value.copy(
                            isLoading = false,
                            userInvestments = result.value
                        )
                    )
                    getSummedTotalProductionValue()
                }
            }
        }
    }

    fun fetchPvPlantForUserCity() {
        viewModelScope.launch {

            _pvPlantService.pvPlantForUserCityFlow.collect {
                state.emit(
                    state.value.copy(
                        pvPlant = it
                    )
                )
                it?.let {
                    fetchPlantData(plantId = it.id)
                }
                getSummedTotalProductionValue()
            }
        }
    }

    private fun fetchPlantData(plantId: String) {

        viewModelScope.launch {

            val result = _pvPlantService.loadDataForPVPlant(plantId)
            when (result) {
                is TypedResult.Failure -> {
                    state.emit(
                        state.value.copy(
                            isLoading = false
                        )
                    )
                }

                is TypedResult.Success -> {
                    state.emit(
                        state.value.copy(
                            isLoading = false,
                            plantData = result.value
                        )
                    )
                    getSummedTotalProductionValue()
                }
            }
        }
    }

    fun getSummedTotalProductionValue() {
        viewModelScope.launch {
            val timeRange = state.value.selectedChartTimeRange
            val productionType = state.value.selectedDisplayOption
            val userInvestments = state.value.userInvestments
            val plantData = state.value.plantData
            val pvPlant = state.value.pvPlant

            val summedValue = when (productionType) {
                DisplayOption.YOUR_PRODUCTION -> {
                    if (userInvestments != null && pvPlant != null && plantData != null) {

                        val userProduction = getUserProductionValues(
                            allInvestments = userInvestments,
                            pvPlant = pvPlant,
                            plantData = plantData
                        )

                        val listInTimeRange = when(timeRange){
                            ChartTimeRangeType.PAST_30_DAYS -> {
                                if(userProduction.size > 30) {
                                    userProduction.subList(userProduction.size - 30, userProduction.size)
                                } else {
                                    userProduction
                                }
                            }
                            ChartTimeRangeType.SINCE_INVESTMENT -> {
                                getListSinceInvestmentDate(allInvestments = userInvestments, list = userProduction, plantData = plantData)
                            }
                        }

                        var summedValue = 0.0
                        listInTimeRange.forEach { summedValue += it.second }
                        summedValue
                    } else {
                        0.0
                    }
                }

                DisplayOption.TOTAL_PRODUCTION -> {

                    val listInTimeRange = when(timeRange){
                        ChartTimeRangeType.PAST_30_DAYS -> {
                            if((plantData?.size ?: 0) >= 30){
                                plantData?.subList(plantData.size - 29, plantData.size)
                            } else {
                                plantData
                            }
                        }
                        ChartTimeRangeType.SINCE_INVESTMENT -> {
                            val firstInvestmentDate = state.value.firstProductionValueDate

                            val lastPlantDataProductionDate = plantData?.maxBy { it.date.timeInMillis }?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.truncatedTo(ChronoUnit.DAYS)
                            val firstInvestDateParsed = firstInvestmentDate.toInstant().atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)

                            var index = 0
                            var current = lastPlantDataProductionDate ?: ZonedDateTime.now()
                            while(current >= firstInvestDateParsed ){
                                index += 1
                                current = current.minusDays(1)
                            }

                            val listSinceInvestment = if((plantData?.size ?: 0) >= index){
                                plantData?.subList(plantData.size - index, plantData.size)
                            } else {
                                plantData
                            }
                            listSinceInvestment
                        }
                    }

                    var summedValue = 0.0
                    listInTimeRange?.forEach { summedValue += it.Ep }
                    summedValue
                }
            }
            state.emit(
                state.value.copy(
                    summedProductionValue = summedValue
                )
            )
        }
    }

    private fun getUserProductionValues(
        allInvestments: List<PVInvestment>,
        pvPlant: PVPlant,
        plantData: List<PVPlantData>
    ): List<Pair<Long, Float>> {
        val userProductionPerDay = mutableListOf<Pair<Long, Float>>()

        plantData.forEach {
            val allPvInvestmentsForDay = getAllPvInvestmentsForDay(allPvInvestments = allInvestments, currentDay = it.date.toInstant().atZone(ZoneId.systemDefault()))

            var summedInvestmentCapacity = 0.0
            allPvInvestmentsForDay.forEach {
                summedInvestmentCapacity += it.investmentCapacity ?: 0.0
            }
            val capacityPercentage = (summedInvestmentCapacity / (pvPlant.capacity ?: 0.0)) * it.Ep

            userProductionPerDay.add(Pair(first = it.date.timeInMillis, second = capacityPercentage.toFloat()))
        }
        return userProductionPerDay
    }

    private fun getAllPvInvestmentsForDay(
        allPvInvestments: List<PVInvestment>,
        currentDay: ZonedDateTime
    ): List<PVInvestment> {
        val allInvestmentsForDay = mutableListOf<PVInvestment>()

        allPvInvestments.forEach {
            val parsedInvestmentDate =
                it.investmentDate.toInstant().atZone(ZoneId.systemDefault()).truncatedTo(
                    ChronoUnit.DAYS
                )
            if (parsedInvestmentDate.isBefore(currentDay) || parsedInvestmentDate.isEqual(currentDay)) {
                allInvestmentsForDay.add(it)
            }
        }
        return allInvestmentsForDay

    }

    fun getFirstProductionValueDate() {
        viewModelScope.launch {
            val timeRange = state.value.selectedChartTimeRange
            val userInvestments = state.value.userInvestments

            val firstProductionValueDate = when(timeRange){
                ChartTimeRangeType.PAST_30_DAYS -> {
                    val now = ZonedDateTime.now()
                    val oneMonthAgo = now.minusDays(30)
                    val nowDate = Calendar.getInstance()
                    nowDate.timeInMillis = oneMonthAgo.toInstant().toEpochMilli()
                    nowDate

                }
                ChartTimeRangeType.SINCE_INVESTMENT -> {
                    val firstInvestmentDate = userInvestments?.minByOrNull { it.investmentDate.timeInMillis }?.investmentDate
                    firstInvestmentDate ?: Calendar.getInstance()
                }
            }

            state.emit(
                state.value.copy(
                    firstProductionValueDate = firstProductionValueDate
                )
            )

        }
    }

    private fun getListSinceInvestmentDate(allInvestments: List<PVInvestment>, list: List<Pair<Long,Float>>, plantData: List<PVPlantData>): List<Pair<Long,Float>>{
        val firstInvestmentDate = allInvestments.minBy { it.investmentDate.timeInMillis }.investmentDate.toInstant().atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)
        val lastPlantDataProductionDate = plantData.maxBy { it.date.timeInMillis }.date.toInstant().atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)

        if(lastPlantDataProductionDate.isBefore(firstInvestmentDate)){
            return listOf(Pair(first = firstInvestmentDate.plusHours(10).toInstant().toEpochMilli(), second = 0f))
        }

        var index = 0
        var current = firstInvestmentDate
        while(current.isBefore(lastPlantDataProductionDate)){
            index += 1
            current = current.plusDays(1)
        }

        val finalList = if(list.size >= index){
            val listSinceInvestment = list.subList(list.lastIndex - index, list.lastIndex)
            listSinceInvestment.reversed()
        } else {
            list.reversed()
        }
        return finalList
    }
}