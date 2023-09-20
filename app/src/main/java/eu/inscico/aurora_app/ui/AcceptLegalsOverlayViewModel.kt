package eu.inscico.aurora_app.ui

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.user.UserResponse
import eu.inscico.aurora_app.model.user.UserSignInType
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.services.firebase.AccountDeletionErrorType
import eu.inscico.aurora_app.services.firebase.CloudFunctionsService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.utils.TypedResult

class AcceptLegalsOverlayViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val _userService: UserService,
    private val _authService: AuthService,
    private val _cloudFunctionsService: CloudFunctionsService
) : ViewModel() {

    val acceptedLegalDocumentsVersion = (savedStateHandle["legalsVersion"] ?: 1L)
    val user = _userService.userLive
    val firebaseUser = _authService.currentFirebaseUserLive

    val userSignInType = _authService.userSignInType

    fun userLogout(activity: Activity) {
        when (_authService.userSignInType) {
            UserSignInType.GOOGLE -> {
                _authService.googleSignOut(activity)
            }
            UserSignInType.APPLE -> {
                _authService.logout()
            }
            UserSignInType.EMAIL -> {
                _authService.logout()
            }
            null -> _authService.logout()
        }
    }

    private fun deleteUser(
        activity: Activity,
        resultCallback: (Boolean, AccountDeletionErrorType?) -> Unit
    ) {
        _userService.deleteUser { isSuccessful, error ->
            resultCallback.invoke(isSuccessful, error)
        }
    }

    fun reAuthenticateAndDeleteUser(
        activity: Activity,
        resultCallback: (Boolean, AccountDeletionErrorType?) -> Unit,
        password: String? = null
    ) {
        when (userSignInType) {
            UserSignInType.GOOGLE -> {
                reAuthenticateGoogleUser { isReauthenticated ->
                    if (isReauthenticated) {
                        _authService.googleRevokeAccess(activity) { isRevoked ->
                            if (isRevoked) {
                                deleteUser(activity, resultCallback)
                            } else {
                                resultCallback.invoke(false, null)
                            }
                        }
                    } else {
                        resultCallback.invoke(false, AccountDeletionErrorType.OTHER)
                    }
                }
            }
            UserSignInType.APPLE -> {
                deleteUser(activity) { isSuccessful, error ->
                    if (isSuccessful) {
                        resultCallback.invoke(true, null)
                    } else {
                        when (error) {
                            AccountDeletionErrorType.REAUTHENTICATION -> {
                                reAuthenticateAppleUser(activity) {
                                    if (it) {
                                        deleteUser(activity, resultCallback)
                                    } else {
                                        resultCallback.invoke(false, AccountDeletionErrorType.OTHER)
                                    }
                                }
                            }
                            AccountDeletionErrorType.OTHER,
                            null -> {
                                resultCallback.invoke(false, AccountDeletionErrorType.OTHER)
                            }
                        }
                    }
                }
            }
            UserSignInType.EMAIL,
            null -> {
                password?.let {
                    reAuthenticateEmailUser(password) {
                        if (it) {
                            deleteUser(activity, resultCallback)
                        } else {
                            resultCallback.invoke(false, AccountDeletionErrorType.OTHER)
                        }
                    }
                }
            }
        }
    }


    fun reAuthenticateAppleUser(activity: Activity, resultCallback: (Boolean) -> Unit) {
        _authService.reAuthenticateWithApple(activity) {
            resultCallback.invoke(it)
        }
    }

    private fun reAuthenticateGoogleUser(resultCallback: (Boolean) -> Unit) {
        _authService.reAuthenticateWithGoogle() {
            resultCallback.invoke(it)
        }
    }

    private fun reAuthenticateEmailUser(password: String, resultCallback: (Boolean) -> Unit) {
        _authService.reAuthenticateWithEmail(password) {
            resultCallback.invoke(it)
        }
    }

    suspend fun updateUser(user: UserResponse): TypedResult<Boolean, String> {
        return _userService.updateUser(user)
    }
}