package eu.inscico.aurora_app.core.koin

import androidx.lifecycle.SavedStateHandle
import eu.inscico.aurora_app.ui.AcceptLegalsOverlayViewModel
import eu.inscico.aurora_app.ui.screens.home.HomeViewModel
import eu.inscico.aurora_app.ui.screens.home.consumptionSummary.ConsumptionSummaryViewModel
import eu.inscico.aurora_app.ui.screens.home.consumptions.AllConsumptionsListViewModel
import eu.inscico.aurora_app.ui.screens.home.consumptions.ConsumptionDetailViewModel
import eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption.AddConsumptionViewModel
import eu.inscico.aurora_app.ui.screens.home.recurringConsumptions.AddOrUpdateRecurringConsumptionViewModel
import eu.inscico.aurora_app.ui.screens.home.recurringConsumptions.RecurringConsumptionsListViewModel
import eu.inscico.aurora_app.ui.screens.login.createProfile.CreateProfileViewModel
import eu.inscico.aurora_app.ui.screens.login.LoginViewModel
import eu.inscico.aurora_app.ui.screens.login.signInEmail.SignInWithEmailViewModel
import eu.inscico.aurora_app.ui.screens.photovoltaic.calculator.PhotovoltaicCalculatorViewModel
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.PvProductionChartViewModel
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components.PvProductionBarChart
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components.PvProductionBarChartViewModel
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard.PVInvestmentsDashboardViewModel
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.add_investment.AddPVInvestmentViewModel
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.all_investments.AllInvestmentsViewModel
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.edit_investment.EditPVInvestmentViewModel
import eu.inscico.aurora_app.ui.screens.settings.profile.EditProfileViewModel
import eu.inscico.aurora_app.ui.screens.settings.SettingsViewModel
import eu.inscico.aurora_app.ui.screens.settings.notifications.SettingsReminderViewModel
import eu.inscico.aurora_app.ui.screens.settings.profile.SelectRegionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        CreateProfileViewModel(
            _countriesService = get(),
            _authService = get(),
            _userService = get()
        )
    }

    viewModel {
        SettingsViewModel(
            _userService = get(),
            _authService = get(),
            _cloudFunctionsService = get()
        )
    }

    viewModel {
        LoginViewModel(
            _authService = get(),
            _userService = get()
        )
    }

    viewModel {
        SignInWithEmailViewModel(
            _authService = get()
        )
    }

    viewModel {
        EditProfileViewModel(
            _userService = get(),
            _countriesService = get()
        )
    }

    viewModel {
        SelectRegionViewModel()
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
            _pvPlantService = get()
        )
    }

    viewModel {
        AddConsumptionViewModel(
            _consumptionService = get()
        )
    }

    viewModel {
        AllConsumptionsListViewModel(
            _consumptionService = get(),
            _unitService = get()
        )
    }

    viewModel { (handle: SavedStateHandle) ->
    ConsumptionDetailViewModel(
        savedStateHandle = handle,
        _consumptionService = get()
    )
    }

    viewModel { (handle: SavedStateHandle) ->
        ConsumptionSummaryViewModel(
            savedStateHandle = handle,
            _consumptionSummaryService = get()
        )
    }

    viewModel { (handle: SavedStateHandle) ->
        AddOrUpdateRecurringConsumptionViewModel(
            savedStateHandle = handle,
            _recurringConsumptionService = get()
        )
    }

    viewModel {
        RecurringConsumptionsListViewModel(
            _recurringConsumptionsService = get()
        )
    }

    viewModel {
        PhotovoltaicCalculatorViewModel(
            countriesService = get(),
            pvgisApiService = get()
        )
    }

    viewModel {  (handle: SavedStateHandle) ->
        AcceptLegalsOverlayViewModel(
            savedStateHandle = handle,
            _cloudFunctionsService = get(),
            _authService = get(),
            _userService = get()
        )
    }

    viewModel {
        PVInvestmentsDashboardViewModel(
            _countriesService = get(),
            _pvPlantService = get(),
            _userService = get()
        )
    }

    viewModel {
        AllInvestmentsViewModel(
            userService = get(),
            countriesService = get()
        )
    }

    viewModel {
        AddPVInvestmentViewModel(
            unitService = get(),
            countriesService = get(),
            pvPlantService = get(),
            userService = get()
        )
    }

    viewModel{ (handle: SavedStateHandle) ->
        EditPVInvestmentViewModel(
            savedStateHandle = handle,
            userService = get(),
            unitService = get(),
            pvPlantService = get(),
            countryService = get()
        )
    }

    viewModel {
        PvProductionChartViewModel(
            _userService = get(),
            _pvPlantService = get()
        )
    }

    viewModel {
        PvProductionBarChartViewModel()
    }
}