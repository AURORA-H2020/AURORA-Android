package eu.inscico.aurora_app.ui.screens.login

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.utils.TypedResult
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    viewModel: LoginViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    authService: AuthService = get()
) {

    val showCreateProfileScreen = remember {
        mutableStateOf(false)
    }

    val currentFirebaseUser = viewModel.currentFirebaseUser.observeAsState()

    LaunchedEffect(Unit) {
        if (currentFirebaseUser.value != null) {
            val authId = currentFirebaseUser.value?.uid
            if(authId != null) {
                val result = viewModel.loadUser(authId)
                when (result) {
                    is TypedResult.Success -> {
                        showCreateProfileScreen.value = false
                    }
                    is TypedResult.Failure -> {
                        showCreateProfileScreen.value = result.reason
                    }
                }
            } else {
                showCreateProfileScreen.value = false
            }
        }
    }

    if (currentFirebaseUser.value != null){
        if (showCreateProfileScreen.value) {
            navigationService.navControllerAuth?.popBackStack(
                route = NavGraphDirections.Auth.getNavRoute(),
                inclusive = false
            )
            navigationService.toCreateProfile()
        }
    } else {
        navigationService.navControllerAuth?.popBackStack(
            route = NavGraphDirections.Auth.getNavRoute(),
            inclusive = false
        )
        navigationService.toLogin()
    }
}