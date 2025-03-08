package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.firebase.PVPlantsService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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
            }
        }
    }

    fun getLatestInvestment(){
        viewModelScope.launch {
            val latestInvestment = state.value.userInvestments?.maxByOrNull { it.investmentDate }

            state.emit(
                state.value.copy(
                    latestPVInvestment =  latestInvestment ?: state.value.userInvestments?.last()
                )
            )
        }
    }
}