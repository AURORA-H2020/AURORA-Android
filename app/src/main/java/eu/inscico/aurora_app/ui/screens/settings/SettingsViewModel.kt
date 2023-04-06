package eu.inscico.aurora_app.ui.screens.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.services.auth.AuthService

class SettingsViewModel(
    private val _userService: UserService,
    private val _authService: AuthService
): ViewModel() {

    fun userLogout(activity: Activity){
        _authService.googleSignOut(activity)
    }
}