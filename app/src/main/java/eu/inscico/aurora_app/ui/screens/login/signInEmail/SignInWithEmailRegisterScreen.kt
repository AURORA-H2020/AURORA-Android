package eu.inscico.aurora_app.ui.screens.login.signInEmail

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.auth.AuthService
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
fun SignInWithEmailRegisterScreen(
    viewModel: SignInWithEmailViewModel,
    navigationService: NavigationService = get(),
    authService: AuthService = get()
) {

    val context = LocalContext.current

    val email = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    val passwordConfirm = remember {
        mutableStateOf("")
    }
    val passwordConfirmVisible = rememberSaveable { mutableStateOf(false) }

    val validationErrorList = remember {
        mutableStateOf(emptyList<String>())
    }

    val validationConfirmationErrorList = remember {
        mutableStateOf(emptyList<String>())
    }

    validationConfirmationErrorList.value =
        validatePasswordConfirmation(context, password.value, passwordConfirm.value)

    Column {
        ScrollableContent {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = stringResource(id = R.string.sign_in_with_email_tab_bar_register),
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
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description = if (passwordVisible.value) "Hide password" else "Show password"

                        IconButton(onClick = {passwordVisible.value = !passwordVisible.value}){
                            Icon(image, description)
                        }
                    },
                    onValueChange = {
                        password.value = it
                        validationErrorList.value = validatePasswordForm(context, it)
                    })

                var validationText = ""
                for (errorTextIndex in 0 .. validationErrorList.value.lastIndex) {
                    validationText += validationErrorList.value[errorTextIndex]
                    if (errorTextIndex < validationErrorList.value.lastIndex){
                        validationText += "\n"
                    }
                }
                if(validationText.isNotEmpty()){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = validationText,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = passwordConfirm.value,
                    label = {
                        Text(text = stringResource(id = R.string.sign_in_with_email_password_confirm_title))
                    },
                    visualTransformation = if (passwordConfirmVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordConfirmVisible.value)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description = if (passwordConfirmVisible.value) "Hide password" else "Show password"

                        IconButton(onClick = {passwordConfirmVisible.value = !passwordConfirmVisible.value}){
                            Icon(image, description)
                        }
                    },
                    onValueChange = {
                        passwordConfirm.value = it
                    })

                Text(
                    text = validationConfirmationErrorList.value.firstOrNull() ?: "",
                    modifier = Modifier.padding(horizontal = 32.dp),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                Spacer(Modifier.height(23.dp))

                TextButton(
                    modifier = Modifier
                        .background(primary)
                        .padding(horizontal = 32.dp),
                    enabled = validationErrorList.value.isEmpty() && validationConfirmationErrorList.value.isEmpty(),
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = viewModel.registerWithEmailAndPassword(email = email.value, password = password.value)
                            when(result){
                                is TypedResult.Failure -> {
                                // TODO:
                                }
                                is TypedResult.Success -> {
                                    withContext(Dispatchers.Main){
                                        navigationService.navControllerAuth?.popBackStack(route = NavGraphDirections.Auth.getNavRoute(), inclusive = false )
                                    }
                                }
                            }
                        }
                        //
                    }) {
                    Text(
                        text = stringResource(id = R.string.sign_in_with_email_tab_bar_register),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }

}

fun validatePasswordConfirmation(
    context: Context,
    password: String,
    passwordConfirmation: String
): List<String> {

    return if (password != passwordConfirmation) {
        listOf(context.getString(R.string.sign_in_with_email_tab_register_password_confirmation_doesnt_match))
    } else {
        emptyList()
    }
}

fun validatePasswordForm(context: Context, password: String): List<String> {

    val validationErrorList = mutableListOf<String>()

    if (password.length < 8) {
        validationErrorList.add(context.getString(R.string.sign_in_with_email_tab_register_password_requirement_at_least_eight_chars))
    }

    if (!password.contains(Regex("[A-Z]"))) {
        validationErrorList.add(context.getString(R.string.sign_in_with_email_tab_register_password_requirement_at_least_one_uppercase_letter))
    }

    if (!password.contains(Regex("\\d"))) {
        validationErrorList.add(context.getString(R.string.sign_in_with_email_tab_register_password_requirement_at_least_one_number))
    }

    if (!password.contains(Regex("[A-Za-z]+"))) {
        validationErrorList.add(context.getString(R.string.sign_in_with_email_tab_register_password_requirement_at_least_one_letter))
    }

    return validationErrorList
}