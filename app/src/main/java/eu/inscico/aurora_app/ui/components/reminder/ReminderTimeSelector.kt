package eu.inscico.aurora_app.ui.components.reminder

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.components.timePicker.TimePickerDialog
import eu.inscico.aurora_app.utils.CalendarUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTimeSelector(
    initialValue: Calendar,
    callback: (Calendar) -> Unit
) {

    val calendar = initialValue
    val selectedTime = remember { mutableStateOf(calendar) }
    val selectedTimeString = remember { mutableStateOf(CalendarUtils.toDateString(calendar, "HH:mm")) }


    val openTimeDialog = remember {
        mutableStateOf(false)
    }

    //val interactionSource = remember { MutableInteractionSource() }

    ListItem(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .clickable {
                openTimeDialog.value = !openTimeDialog.value
            },
        headlineContent = { Text(text = stringResource(id = R.string.settings_notifications_time_title)) },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.End,
            ) {
                Row(
                    Modifier
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = selectedTimeString.value,//CalendarUtils.toDateString(, "HH:mm"),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 17.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.End
                        )
                    )
                }
            }
        }
    )


    TimePickerDialog(
        showDialog = openTimeDialog,
        time = selectedTime.value,
        onTimeChanged = {
            selectedTime.value = it
            selectedTimeString.value = CalendarUtils.toDateString(it, "HH:mm")
            callback.invoke(selectedTime.value)
        },
        validator = {
            true
        }
    )

}