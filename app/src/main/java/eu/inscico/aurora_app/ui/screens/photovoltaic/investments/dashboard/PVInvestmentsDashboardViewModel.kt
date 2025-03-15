package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.pvPlant.PVPlantData
import eu.inscico.aurora_app.model.pvProductionChart.ChartTimeRangeType
import eu.inscico.aurora_app.model.pvProductionChart.DisplayOption
import eu.inscico.aurora_app.model.user.PVInvestment
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.firebase.PVPlantsService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class PVInvestmentsDashboardViewModel(
    private val _pvPlantService: PVPlantsService,
    private val _countriesService: CountriesService,
    private val _userService: UserService
) : ViewModel() {

    val state = MutableStateFlow(PVInvestmentsDashboardScreenState())

    fun fetchUserCountryAndCity() {
        fetchUserCity()
        fetchUserCountry()
    }

    private fun fetchUserCity() {
        viewModelScope.launch {
            _countriesService.userCityFlow.collect {
                state.emit(
                    state.value.copy(
                        userCity = it
                    )
                )
            }
        }
    }

    private fun fetchUserCountry() {
        viewModelScope.launch {
            _countriesService.userCountryFlow.collect {
                state.emit(
                    state.value.copy(
                        userCountry = it,
                    )
                )
            }
        }
    }

    fun fetchInvestmentsForUser() {

        viewModelScope.launch {

            state.emit(
                state.value.copy(
                    isLoading = true
                )
            )

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

                    getLatestInvestment()
                }
            }
        }
    }

    fun fetchPvPlantForUserCity() {
        viewModelScope.launch {

            _pvPlantService.pvPlantForUserCityFlow.collect {
                state.emit(
                    state.value.copy(
                        pvPlantForUserCity = it,
                        isPVPlantActive = it?.active ?: false,
                        isLoading = false
                    )
                )
                if (it?.id != null) {
                    fetchPlantData(it.id)
                }
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
                    getUserProductionForLast30Days()
                    getPvPlantProductionForLast30Days()
                }
            }
        }
    }

    fun getLatestInvestment() {
        viewModelScope.launch {
            val latestInvestment = state.value.userInvestments?.maxByOrNull { it.investmentDate }

            state.emit(
                state.value.copy(
                    latestPVInvestment = latestInvestment ?: state.value.userInvestments?.last()
                )
            )
        }
    }

    fun getUserProductionForLast30Days() {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    userProductionLast30Days = getSummedTotalProductionValue(DisplayOption.YOUR_PRODUCTION)
                )
            )
        }
    }

    fun getPvPlantProductionForLast30Days() {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    totalProductionLast30Days = getSummedTotalProductionValue(DisplayOption.TOTAL_PRODUCTION)
                )
            )
        }
    }

    private fun getSummedTotalProductionValue(productionType: DisplayOption): Double {
        val userInvestments = state.value.userInvestments
        val plantData = state.value.plantData
        val pvPlant = state.value.pvPlantForUserCity

        val summedValue = when (productionType) {
            DisplayOption.YOUR_PRODUCTION -> {
                if (userInvestments != null && pvPlant != null && plantData != null) {

                    val userProduction = getUserProductionValues(
                        allInvestments = userInvestments,
                        pvPlant = pvPlant,
                        plantData = plantData
                    )

                    val list = if (userProduction.size > 30) {
                        userProduction.subList(userProduction.size - 30, userProduction.size)
                    } else {
                        userProduction
                    }

                    var summedValue = 0.0
                    list.forEach { summedValue += it.second }
                    summedValue
                } else {
                    0.0
                }
            }

            DisplayOption.TOTAL_PRODUCTION -> {

                val list = if ((plantData?.size ?: 0) >= 30) {
                    plantData?.subList(plantData.size - 29, plantData.size)
                } else {
                    plantData
                }

                var summedValue = 0.0
                list?.forEach { summedValue += it.Ep }
                summedValue
            }
        }
        return summedValue
    }

    private fun getUserProductionValues(
        allInvestments: List<PVInvestment>,
        pvPlant: PVPlant,
        plantData: List<PVPlantData>
    ): List<Pair<Long, Float>> {

        val userProductionPerDay = mutableListOf<Pair<Long, Float>>()

        val lastPlanProductionDate =
            plantData.maxBy { it.date.timeInMillis }.date.toInstant().atZone(
                ZoneId.systemDefault()
            ).truncatedTo(ChronoUnit.DAYS)
        val firstInvestmentDate =
            allInvestments.minBy { it.investmentDate }.investmentDate.toInstant().atZone(
                ZoneId.systemDefault()
            ).truncatedTo(ChronoUnit.DAYS)

        var current = firstInvestmentDate
        while (current.isBefore(lastPlanProductionDate) || current.isEqual(lastPlanProductionDate)) {
            var summedCapacityFromUserForThisDay = 0.0
            getAllPvInvestmentsForDay(allInvestments, current).forEach {
                summedCapacityFromUserForThisDay += (it.investmentCapacity ?: 0.0)
            }

            val proportion = summedCapacityFromUserForThisDay / (pvPlant.capacity ?: 0.0)
            val producedEnergyForDay = plantData.find {
                it.date.toInstant().atZone(ZoneId.systemDefault())
                    .truncatedTo(ChronoUnit.DAYS) == current
            }?.Ep ?: 0.0
            val value = (proportion / 100) * producedEnergyForDay
            userProductionPerDay.add(
                Pair(
                    first = current.plusHours(10).toInstant().toEpochMilli(),
                    second = value.toFloat()
                )
            )

            current = current.plusDays(1)
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
}