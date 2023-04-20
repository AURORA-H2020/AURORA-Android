package eu.inscico.aurora_app.ui.screens.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.UserSignInType
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.utils.TypedResult

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
            UserSignInType.APPLE -> {
            // TODO:
            }
            UserSignInType.EMAIL -> {
                _authService.logout()
            }
            null -> _authService.logout()
        }
    }

    fun deleteUserData(activity: Activity){
        when(_authService.userSignInType){
            UserSignInType.GOOGLE -> {
                _authService.googleRevokeAccess(activity)
            }
            UserSignInType.APPLE -> {

            }
            UserSignInType.EMAIL -> {
                _authService.deleteUser()
            }
            null -> {
                _authService.deleteUser()
            }
        }
    }

    suspend fun deleteUser(activity: Activity): TypedResult<Any, Any>{
        deleteUserData(activity)
        return _userService.deleteUser()
    }

    fun getSupportUrl(): String?{
        val authId = _userService.userLive.value?.id
        val countryId = _userService.userLive.value?.country
        return if(authId != null && countryId != null){
            "https://www.aurora-h2020.eu/app-support/?user_id=$authId&country_id=$countryId"
        } else {
            null
        }
    }
}