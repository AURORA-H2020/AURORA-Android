package eu.inscico.aurora_app.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import eu.inscico.aurora_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReenterPasswordAndDeleteAccountDialog(
    showDialog: Boolean,
    reenteredPasswordCallback:((String) -> Unit)? = null,
    dismissCallback:(() -> Unit)? = null,
){

    val passwordInput = remember {
        mutableStateOf("")
    }

    val passwordConfirmVisible = rememberSaveable { mutableStateOf(false) }

    if(showDialog) {
        Dialog(
            onDismissRequest = { dismissCallback?.invoke() },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    text = stringResource(id = R.string.dialog_account_delete_title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    text = stringResource(id = R.string.dialog_account_delete_description),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )

                OutlinedTextField(
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    visualTransformation = if (passwordConfirmVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordConfirmVisible.value)
                            R.drawable.outline_visibility_24
                        else R.drawable.outline_visibility_off_24

                        // Please provide localized description for accessibility services
                        val description =
                            if (passwordConfirmVisible.value) "Hide password" else "Show password"

                        IconButton(onClick = {
                            passwordConfirmVisible.value = !passwordConfirmVisible.value
                        }) {
                            Icon(painterResource(id = image), description)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = passwordInput.value,
                    label = {
                        Text(text = stringResource(id = R.string.sign_in_with_email_password_title))
                    },
                    onValueChange = {
                        passwordInput.value = it
                    }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        dismissCallback?.invoke()
                    }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    TextButton(onClick = {
                        reenteredPasswordCallback?.invoke(passwordInput.value)
                        dismissCallback?.invoke()
                    }) {
                        Text(stringResource(id = R.string.delete))
                    }
                }
            }
        }
    }
}