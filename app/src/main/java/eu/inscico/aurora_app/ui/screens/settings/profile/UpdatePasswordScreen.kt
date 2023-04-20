package eu.inscico.aurora_app.ui.screens.settings.profile

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
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.ui.screens.login.signInEmail.validatePasswordConfirmation
import eu.inscico.aurora_app.ui.screens.login.signInEmail.validatePasswordForm
import eu.inscico.aurora_app.ui.theme.primary
import org.koin.androidx.compose.get


@Composable
fun UpdatePasswordScreen(
    userService: UserService = get(),
    navigationService: NavigationService = get()
){

    val context = LocalContext.current

    val oldPassword = remember {
        mutableStateOf("")
    }
    val oldPasswordVisible = rememberSaveable { mutableStateOf(false) }

    val newPassword = remember {
        mutableStateOf("")
    }
    val newPasswordVisible = rememberSaveable { mutableStateOf(false) }

    val newPasswordConfirm = remember {
        mutableStateOf("")
    }
    val newPasswordConfirmVisible = rememberSaveable { mutableStateOf(false) }

    val validationErrorList = remember {
        mutableStateOf(emptyList<String>())
    }

    val validationConfirmationErrorList = remember {
        mutableStateOf(emptyList<String>())
    }

    validationConfirmationErrorList.value =
        validatePasswordConfirmation(context, newPassword.value, newPasswordConfirm.value)

    Column(
        Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        AppBar(
            title = stringResource(id = R.string.settings_profile_change_password_title),
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
                    value = oldPassword.value,
                    label = {
                        Text(text = stringResource(id = R.string.settings_change_password_old_password_title))
                    },
                    visualTransformation = if (oldPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (oldPasswordVisible.value)
                            R.drawable.outline_visibility_24
                        else R.drawable.outline_visibility_off_24

                        // Please provide localized description for accessibility services
                        val description = if (oldPasswordVisible.value) "Hide password" else "Show password"

                        IconButton(onClick = {oldPasswordVisible.value = !oldPasswordVisible.value}){
                            Icon(painterResource(id = image), description)
                        }
                    },
                    onValueChange = {
                        oldPassword.value = it
                    })

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = newPassword.value,
                    label = {
                        Text(text = stringResource(id = R.string.settings_change_password_new_password_title))
                    },
                    visualTransformation = if (newPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (newPasswordVisible.value)
                            R.drawable.outline_visibility_24
                        else R.drawable.outline_visibility_off_24

                        // Please provide localized description for accessibility services
                        val description = if (newPasswordVisible.value) "Hide password" else "Show password"

                        IconButton(onClick = {newPasswordVisible.value = !newPasswordVisible.value}){
                            Icon(painterResource(id = image), description)
                        }
                    },
                    onValueChange = {
                        newPassword.value = it
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
                            .padding(top = 4.dp, start = 32.dp, end = 32.dp),
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
                    value = newPasswordConfirm.value,
                    label = {
                        Text(text = stringResource(id = R.string.settings_change_password_new_password_confirm_title))
                    },
                    visualTransformation = if (newPasswordConfirmVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (newPasswordConfirmVisible.value)
                            R.drawable.outline_visibility_24
                        else R.drawable.outline_visibility_off_24

                        // Please provide localized description for accessibility services
                        val description = if (newPasswordConfirmVisible.value) "Hide password" else "Show password"

                        IconButton(onClick = {newPasswordConfirmVisible.value = !newPasswordConfirmVisible.value}){
                            Icon(painterResource(id = image), description)
                        }
                    },
                    onValueChange = {
                        newPasswordConfirm.value = it
                    })

                Text(
                    text = validationConfirmationErrorList.value.firstOrNull() ?: "",
                    modifier = Modifier.padding(horizontal = 32.dp),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                Spacer(Modifier.height(23.dp))

                Button(
                    modifier = Modifier
                        .padding(horizontal = 32.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    enabled = validationErrorList.value.isEmpty() && validationConfirmationErrorList.value.isEmpty(),
                    onClick = {
                        userService.updateUserPassword(oldPassword = oldPassword.value, newPassword = newPassword.value){ wasSucessfull ->
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