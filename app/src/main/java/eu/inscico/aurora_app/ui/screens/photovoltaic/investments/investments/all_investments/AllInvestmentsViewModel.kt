package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.all_investments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AllInvestmentsViewModel(
    val userService: UserService,
    val countriesService: CountriesService
): ViewModel() {

    val state = MutableStateFlow(AllInvestmentsScreenState())

    fun fetchUserCountry(){
        viewModelScope.launch {
            countriesService.userCountryFlow.collect {
                state.emit(
                    state.value.copy(
                        userCountry = it,
                    )
                )
            }
        }
    }

    fun fetchUserInvestments(){
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    isLoading = true
                )
            )

            val result = userService.loadPVInvestmentsForUser()
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
                            allInvestments = result.value
                        )
                    )
                }
            }
        }
    }
}