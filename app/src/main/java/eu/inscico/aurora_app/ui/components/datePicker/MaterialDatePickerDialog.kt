package eu.inscico.aurora_app.ui.components.datePicker

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eu.inscico.aurora_app.R
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDatePickerDialog(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    initialSelectedDay: Calendar? = null,
    confirmButtonCallback: (Calendar) -> Unit,
    dateValidator: ((Calendar) -> Boolean)
) {

    val selectedDay = if(initialSelectedDay != null){
        initialSelectedDay
    } else {
        val currentCalendarDay = Calendar.getInstance()
        currentCalendarDay.timeInMillis = System.currentTimeMillis()
        currentCalendarDay
    }

    val selectedDatePickerState = rememberDatePickerState(
        initialDisplayedMonthMillis = selectedDay.timeInMillis,
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = selectedDay.timeInMillis
    )


    if (showDialog.value) {

        DatePickerDialog(
            modifier = modifier,
            dismissButton = {
                TextButton(onClick = {
                    showDialog.value = false
                }) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(id = R.string.cancel)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateInMillis = selectedDatePickerState.selectedDateMillis
                    if (selectedDateInMillis != null) {
                    val selectedDateAsCalendar = Calendar.getInstance()
                    selectedDateAsCalendar.timeInMillis = selectedDateInMillis
                        if (dateValidator(selectedDateAsCalendar)) {
                            confirmButtonCallback.invoke(selectedDateAsCalendar)
                            showDialog.value = false
                        }
                    }
                }) {
                    Text(
                        text = stringResource(id = R.string.okay),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            onDismissRequest = {
                showDialog.value = false
            }) {

                DatePicker(state = selectedDatePickerState, dateValidator = { dateMillisToCheck ->
                    val parsedCalendar = Calendar.getInstance()
                    parsedCalendar.timeInMillis = dateMillisToCheck
                    dateValidator(parsedCalendar)
                })
        }
    }
}