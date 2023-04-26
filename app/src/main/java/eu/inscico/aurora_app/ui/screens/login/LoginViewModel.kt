package eu.inscico.aurora_app.ui.screens.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.user.User
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.utils.TypedResult

class LoginViewModel(
    private val _authService: AuthService,
    private val _userService: UserService
): ViewModel() {

    val currentFirebaseUser = _authService.currentFirebaseUserLive

    suspend fun loadUser(authId: String): TypedResult<User, Boolean> {
        return _userService.getUserByAuthId(authId)
    }

    fun loginWithGoogle(activity: Activity){
        _authService.loginWithGoogle(activity)
    }

    fun loginWithApple(activity: Activity){
        _authService.loginWithApple(activity)
    }
}