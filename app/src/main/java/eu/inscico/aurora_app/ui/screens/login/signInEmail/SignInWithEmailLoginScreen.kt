package eu.inscico.aurora_app.ui.screens.login.signInEmail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

    val email = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }


    Column(
        Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(id = R.string.login_email_sign_in_button_text),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                value = email.value,
                label = {
                    Text(text = stringResource(id = R.string.sign_in_with_email_email_title))
                },
                onValueChange = {
                    email.value = it
                })

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                value = password.value,
                label = {
                    Text(text = stringResource(id = R.string.sign_in_with_email_password_title))
                },
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible.value)
                        R.drawable.outline_visibility_24
                    else R.drawable.outline_visibility_off_24

                    // Please provide localized description for accessibility services
                    val description =
                        if (passwordVisible.value) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        Icon(painterResource(id = image), description)
                    }
                },
                onValueChange = {
                    password.value = it
                })

            Spacer(Modifier.height(46.dp))

            Button(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = viewModel.loginWithEmailAndPassword(
                            email = email.value,
                            password = password.value
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

                }) {
                Text(
                    text = stringResource(id = R.string.sign_in_with_email_tab_bar_login),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(stringResource(id = R.string.sign_in_with_email_tab_login_forgot_password_title))
        }
    }
}