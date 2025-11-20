package eu.inscico.aurora_app.ui.screens.home

import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.services.firebase.ConsumptionSummaryService
import eu.inscico.aurora_app.services.firebase.ConsumptionsService
import eu.inscico.aurora_app.services.firebase.PVPlantsService

class HomeViewModel(
    private val _consumptionService: ConsumptionsService,
    private val _consumptionSummaryService: ConsumptionSummaryService,
    private val _pvPlantService: PVPlantsService
): ViewModel() {

    val userConsumptions = _consumptionService.userConsumptionsLive
    val consumptionSummaries = _consumptionSummaryService.consumptionSummariesLive


}