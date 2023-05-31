package eu.inscico.aurora_app.ui.screens.login.signInEmail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.login.LoginForm
import eu.inscico.aurora_app.ui.components.login.ResetPasswordForm
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInWithEmailLoginScreen(
    viewModel: SignInWithEmailViewModel,
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get()
) {

    val context = LocalContext.current

    val isResetPasswordFormShown = rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if(isResetPasswordFormShown.value){
                ResetPasswordForm { email ->
                    viewModel.sendPasswordResetEmail(email = email)
                    userFeedbackService.showDialog(
                        message = context.getString(R.string.dialog_sign_in_with_email_tab_login_forgot_password_send_email),
                        confirmButtonText = context.getString(R.string.okay,)){
                        isResetPasswordFormShown.value = false
                    }
                }

            } else {
                LoginForm(
                    loginCallback = { email, password ->

                    CoroutineScope(Dispatchers.IO).launch {
                        val result = viewModel.loginWithEmailAndPassword(
                            email = email,
                            password = password
                        )
                        when (result) {
                            is TypedResult.Failure -> {
                                userFeedbackService.showSnackbar(R.string.login_email_fail_message)
                            }
                            is TypedResult.Success -> {
                                withContext(Dispatchers.Main) {
                                    navigationService.navControllerAuth?.popBackStack(
                                        route = NavGraphDirections.Auth.getNavRoute(),
                                        inclusive = false
                                    )
                                }
                            }
                        }
                    }
                },
                    resetPasswordCallback = {
                        isResetPasswordFormShown.value = true
                    }
                )
            }

        }
    }
}