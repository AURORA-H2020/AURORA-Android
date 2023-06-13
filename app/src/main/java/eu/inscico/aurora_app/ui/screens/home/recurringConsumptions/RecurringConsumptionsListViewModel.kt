package eu.inscico.aurora_app.ui.screens.home.recurringConsumptions

import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.services.firebase.RecurringConsumptionsService

class RecurringConsumptionsListViewModel(
    private val _recurringConsumptionsService: RecurringConsumptionsService
): ViewModel() {

    val recurringConsumptionsLive = _recurringConsumptionsService.recurringConsumptionsLive


}