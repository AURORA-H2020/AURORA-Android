package eu.inscico.aurora_app.ui.components.timePicker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.components.timePicker.time.TimeDigitalDisplay
import java.util.*


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TimePickerDialog(
    time: Calendar,
    showDialog: MutableState<Boolean>,
    onTimeChanged: (Calendar) -> Unit,
    validator: (Calendar) -> Boolean
) {

    val selectedCalendarTime = remember {
        mutableStateOf(time)
    }

    if (showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimeDigitalDisplay(
                        modifier = Modifier
                            .padding(16.dp),
                        time = time,
                        onTimeChanged = {
                            selectedCalendarTime.value = it
                                        },
                        validator = validator,
                        isReadOnly = false
                    )
                    Row(
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        TextButton(onClick = {
                            showDialog.value = false
                        }) {
                            Text(
                                style = MaterialTheme.typography.labelLarge,
                                text = stringResource(id = R.string.cancel)
                            )
                        }
                        TextButton(onClick = {
                            val currentCalendarDay = selectedCalendarTime.value
                            if (validator(currentCalendarDay)) {
                                onTimeChanged.invoke(currentCalendarDay)
                                selectedCalendarTime.value = currentCalendarDay
                                showDialog.value = false
                            } else {
                                selectedCalendarTime.value = currentCalendarDay
                            }
                        }) {
                            Text(
                                text = stringResource(id = R.string.okay),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


