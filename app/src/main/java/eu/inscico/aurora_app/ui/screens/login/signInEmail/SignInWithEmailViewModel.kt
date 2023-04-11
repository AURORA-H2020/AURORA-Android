package eu.inscico.aurora_app.ui.screens.login.signInEmail

import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.utils.TypedResult

class SignInWithEmailViewModel(
    private val _authService: AuthService
): ViewModel() {

    suspend fun registerWithEmailAndPassword(email: String, password: String): TypedResult<Boolean, String>{
            return _authService.registerWithEmailAndPassword(email, password)
    }

    suspend fun loginWithEmailAndPassword(email: String, password: String): TypedResult<Boolean, String>{
        return _authService.loginWithEmailAndPassword(email, password)
    }
}