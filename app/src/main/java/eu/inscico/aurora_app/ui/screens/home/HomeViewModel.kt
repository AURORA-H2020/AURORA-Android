package eu.inscico.aurora_app.ui.screens.home

import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.services.firebase.ConsumptionsService

class HomeViewModel(
    private val _consumptionService: ConsumptionsService
): ViewModel() {

    val userConsumptions = _consumptionService.userConsumptionsLive
}