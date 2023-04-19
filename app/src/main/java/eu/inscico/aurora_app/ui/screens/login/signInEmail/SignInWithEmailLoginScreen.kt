package eu.inscico.aurora_app.ui.screens.login.signInEmail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.Gender
import eu.inscico.aurora_app.model.UserResponse
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.ui.theme.primary
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get

@Composable
fun SignInWithEmailLoginScreen(
    viewModel: SignInWithEmailViewModel,
    navigationService: NavigationService = get()
){

    val email = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }


    Column() {

        ScrollableContent {

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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = email.value,
                    label = {
                        Text(text = stringResource(id = R.string.sign_in_with_email_email_title))
                    },
                    onValueChange = {
                        email.value = it
                    })

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = password.value,
                    label = {
                        Text(text = stringResource(id = R.string.sign_in_with_email_password_title))
                    },
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible.value)
                            R.drawable.baseline_visibility_24
                        else R.drawable.baseline_visibility_off_24

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

                TextButton(
                    modifier = Modifier
                        .background(primary)
                        .padding(horizontal = 32.dp),
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = viewModel.loginWithEmailAndPassword(
                                email = email.value,
                                password = password.value
                            )
                            when (result) {
                                is TypedResult.Failure -> {}
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
}