package eu.inscico.aurora_app.ui.screens.home

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.services.ConsumptionsService

class HomeViewModel(
    private val _consumptionService: ConsumptionsService
): ViewModel() {

    val userConsumptions = _consumptionService.userConsumptionsLive
}