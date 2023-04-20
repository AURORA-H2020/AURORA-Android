package eu.inscico.aurora_app.ui.screens.settings.profile

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.ui.screens.login.signInEmail.validatePasswordConfirmation
import eu.inscico.aurora_app.ui.screens.login.signInEmail.validatePasswordForm
import eu.inscico.aurora_app.ui.theme.primary
import org.koin.androidx.compose.get


@Composable
fun UpdateEmailScreen(
    userService: UserService = get(),
    authService: AuthService = get(),
    navigationService: NavigationService = get()
){

    val context = LocalContext.current

    val newEmail = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    val isNewEmailValid = remember {
        mutableStateOf(false)
    }

    Column(
        Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        AppBar(
            title = stringResource(id = R.string.settings_profile_change_email_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabSettings?.popBackStack()
            }
        )

        ScrollableContent(
            background = MaterialTheme.colorScheme.background
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = newEmail.value,
                    label = {
                        Text(text = stringResource(id = R.string.settings_change_email_new_email_title))
                    },
                    onValueChange = {
                        newEmail.value = it
                        isNewEmailValid.value = isValidEmail(it)
                    })

                val currentEmail = authService.currentFirebaseUser?.email

                if(currentEmail != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 32.dp, end = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = context.getString(R.string.settings_change_email_new_email_description, currentEmail),
                            modifier = Modifier.padding(horizontal = 32.dp),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = password.value,
                    label = {
                        Text(text = stringResource(id = R.string.settings_change_password_old_password_title))
                    },
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible.value)
                            R.drawable.outline_visibility_24
                        else R.drawable.outline_visibility_off_24

                        // Please provide localized description for accessibility services
                        val description = if (passwordVisible.value) "Hide password" else "Show password"

                        IconButton(onClick = {passwordVisible.value = !passwordVisible.value}){
                            Icon(painterResource(id = image), description)
                        }
                    },
                    onValueChange = {
                        password.value = it
                    })

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 32.dp, end = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings_change_email_password_description),
                            modifier = Modifier.padding(horizontal = 32.dp),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                Spacer(Modifier.height(23.dp))

                Button(
                    modifier = Modifier
                        .padding(horizontal = 32.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    enabled = isNewEmailValid.value,
                    onClick = {
                        userService.updateUserEmail(newEmail = newEmail.value, password = password.value){ wasSucessfull ->
                            if(wasSucessfull){
                                navigationService.navControllerTabSettings?.popBackStack()
                            } else {
                                // TODO:
                            }
                        }

                    }) {
                    Text(
                        text = stringResource(id = R.string.settings_edit_profile_submit_button_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

fun isValidEmail(email: String?): Boolean {
    return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}