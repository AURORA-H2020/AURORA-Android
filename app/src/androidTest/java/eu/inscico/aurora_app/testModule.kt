package eu.inscico.aurora_app

import androidx.lifecycle.SavedStateHandle
import eu.inscico.aurora_app.ui.screens.home.HomeViewModel
import eu.inscico.aurora_app.ui.screens.home.consumptionSummary.ConsumptionSummaryViewModel
import eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption.AddConsumptionViewModel
import eu.inscico.aurora_app.ui.screens.settings.SettingsViewModel
import eu.inscico.aurora_app.ui.screens.settings.notifications.SettingsReminderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val testModule = module {

    factory { SavedStateHandle() }

    viewModel {
        ConsumptionSummaryViewModel(
            savedStateHandle = get(),
            _consumptionSummaryService = get()
        )
    }

    viewModel {
        AddConsumptionViewModel(
            _consumptionService = get()
        )
    }

    viewModel {
        SettingsReminderViewModel(
            _notificationService = get()
        )
    }

    viewModel {
        HomeViewModel(
            _consumptionService = get(),
            _consumptionSummaryService = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            _userService = get(),
            _authService = get(),
            _cloudFunctionsService = get()
        )
    }
}