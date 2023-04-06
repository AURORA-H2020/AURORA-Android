package eu.inscico.aurora_app.ui.screens.login

import android.app.Activity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.User
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.get

class LoginViewModel(
    private val _authService: AuthService,
    private val _userService: UserService
): ViewModel() {

    fun loginWithGoogle(activity: Activity){
        _authService.loginWithGoogle(activity)
    }

    val currentFirebaseUser = _authService.currentFirebaseUserLive

    val isAuthenticated = _authService.isAuthenticatedLive

    suspend fun loadUser(authId: String): TypedResult<User, Boolean> {
        return _userService.getUserByAuthId(authId)
    }
}