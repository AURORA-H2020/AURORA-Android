package eu.inscico.aurora_app.ui.screens.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.UserSignInType
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.services.auth.AuthService

class SettingsViewModel(
    private val _userService: UserService,
    private val _authService: AuthService
): ViewModel() {

    val userSignInType = _authService.userSignInType

    fun userLogout(activity: Activity){
        when(_authService.userSignInType){
            UserSignInType.GOOGLE -> {
                _authService.googleSignOut(activity)
            }
            UserSignInType.APPLE -> TODO()
            UserSignInType.EMAIL -> {
                _authService.logout()
            }
            null -> TODO()
        }
    }
}