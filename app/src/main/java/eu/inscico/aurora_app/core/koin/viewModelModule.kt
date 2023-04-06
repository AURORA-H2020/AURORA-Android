package eu.inscico.aurora_app.core.koin

import eu.inscico.aurora_app.ui.screens.login.CreateProfileViewModel
import eu.inscico.aurora_app.ui.screens.login.LoginViewModel
import eu.inscico.aurora_app.ui.screens.settings.SettingsViewModel
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
}