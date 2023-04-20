package eu.inscico.aurora_app.core.koin

import androidx.lifecycle.SavedStateHandle
import eu.inscico.aurora_app.ui.screens.home.HomeViewModel
import eu.inscico.aurora_app.ui.screens.home.consumptions.AllConsumptionsListViewModel
import eu.inscico.aurora_app.ui.screens.home.consumptions.ConsumptionDetailViewModel
import eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption.AddConsumptionViewModel
import eu.inscico.aurora_app.ui.screens.login.createProfile.CreateProfileViewModel
import eu.inscico.aurora_app.ui.screens.login.LoginViewModel
import eu.inscico.aurora_app.ui.screens.login.signInEmail.SignInWithEmailViewModel
import eu.inscico.aurora_app.ui.screens.settings.profile.EditProfileViewModel
import eu.inscico.aurora_app.ui.screens.settings.SettingsViewModel
import eu.inscico.aurora_app.ui.screens.settings.notifications.SettingsReminderViewModel
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
            _authService = get()
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
        SettingsReminderViewModel(
            _notificationService = get()
        )
    }

    viewModel {
        HomeViewModel(
            _consumptionService = get()
        )
    }

    viewModel {
        AddConsumptionViewModel(
            _consumptionService = get()
        )
    }

    viewModel {
        AllConsumptionsListViewModel(
            _consumptionService = get()
        )
    }

    viewModel { (handle: SavedStateHandle) ->
    ConsumptionDetailViewModel(
        savedStateHandle = handle,
        _consumptionService = get()
    )
    }
}