package eu.inscico.aurora_app.ui.components.timePicker.time

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.components.timePicker.time.TimeUtils.Companion.getValidHourString
import eu.inscico.aurora_app.ui.components.timePicker.time.TimeUtils.Companion.getValidMinuteString
import eu.inscico.aurora_app.ui.components.timePicker.time.TimeUtils.Companion.setTimeToCalendar
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TimeDigitalDisplay(
    time: Calendar,
    modifier: Modifier,
    isReadOnly: Boolean,
    onTimeChanged: (Calendar) -> Unit,
    validator: (Calendar) -> Boolean
) {

    val timeItem = remember {
        mutableStateOf(TimeItem.fromCalendar(time))
    }

    val hour = remember { mutableStateOf(TextFieldValue(getValidHourString(timeItem.value.hour.toString()))) }

    val minute = remember { mutableStateOf(TextFieldValue(getValidMinuteString(timeItem.value.minute.toString()))) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    val hourInteractionSource = remember {
        MutableInteractionSource()
    }

    val minuteInteractionSource = remember {
        MutableInteractionSource()
    }

    val isHourFocused by hourInteractionSource.collectIsFocusedAsState()
    LaunchedEffect(isHourFocused) {
        hour.value = hour.value.copy(
            selection = if (isHourFocused) {
                TextRange(
                    start = 0,
                    end = hour.value.text.length
                )
            } else {
                TextRange.Zero
            }
        )
    }

    val isMinuteFocused by minuteInteractionSource.collectIsFocusedAsState()
    LaunchedEffect(isMinuteFocused) {
        minute.value = minute.value.copy(
            selection = if (isMinuteFocused) {
                TextRange(
                    start = 0,
                    end = minute.value.text.length
                )
            } else {
                TextRange.Zero
            }
        )
    }


    Column(
        modifier = modifier
    ) {
        Text(stringResource(id = R.string.settings_notifications_time_display_title), Modifier.padding(vertical = 20.dp))
        Row(
            modifier = Modifier,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .height(75.dp)
                    .width(95.dp),
                value = hour.value,
                onValueChange = {
                    val inputString = it.text
                    val processedInputString = getValidHourString(inputString)

                    val selectedHour = processedInputString.toIntOrNull()

                    if (selectedHour != null) {
                        val selectedMinute = minute.value
                        val currentTimeItem =
                            TimeItem.fromValues(selectedHour, selectedMinute.text.toInt())
                        val timeItemAsCalendar = setTimeToCalendar(currentTimeItem, time)
                        if (validator(timeItemAsCalendar)) {
                            hour.value = TextFieldValue(
                                processedInputString,
                                selection = TextRange(processedInputString.length)
                            )
                            onTimeChanged.invoke(timeItemAsCalendar)
                        } else {
                            // TODO:  
                        }
                    }
                },
                textStyle = MaterialTheme.typography.headlineLarge,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.background
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                    keyboardController?.hide()
                    focusManager.moveFocus(FocusDirection.Next)

                },
                readOnly = isReadOnly,
                singleLine = true,
                interactionSource = hourInteractionSource
            )


            Text(
                textAlign = TextAlign.Center,
                text = ":",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .align(Alignment.CenterVertically)
            )
            
            OutlinedTextField(
                modifier = Modifier
                    .height(75.dp)
                    .width(95.dp),
                value = minute.value,
                onValueChange = {

                    val inputString = it.text
                    val processedInputString = getValidMinuteString(inputString)

                    val selectedMinute = processedInputString.toIntOrNull()

                    if (selectedMinute != null) {
                        val selectedHour = hour.value.text.toInt()
                        val currentTimeItem =
                            TimeItem.fromValues(selectedHour, selectedMinute)
                        val timeItemAsCalendar = setTimeToCalendar(currentTimeItem, time)
                        if (validator(timeItemAsCalendar)) {
                            minute.value = TextFieldValue(
                                processedInputString,
                                selection = TextRange(processedInputString.length)
                            )
                            onTimeChanged.invoke(timeItemAsCalendar)
                        }
                    }
                },
                textStyle = MaterialTheme.typography.headlineLarge,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.background
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                    keyboardController?.hide()
                    focusManager.clearFocus()

                    val selectedTime = TimeItem.fromValues(
                        hour = hour.value.text.toInt(),
                        minute = minute.value.text.toInt()
                    )
                    val timeAsCalendar = selectedTime.toCalendar(time)
                    onTimeChanged.invoke(timeAsCalendar)
                },
                readOnly = isReadOnly,
                singleLine = true,
                interactionSource = minuteInteractionSource

            )
        }
    }
}