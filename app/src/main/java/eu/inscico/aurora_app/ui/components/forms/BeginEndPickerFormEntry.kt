package eu.inscico.aurora_app.ui.components.forms

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.components.datePicker.MaterialDatePickerDialog
import eu.inscico.aurora_app.utils.CalendarUtils
import java.util.*

@Composable
fun BeginEndPickerFormEntry(
    initialBeginCalendarAsLong: Long = Calendar.getInstance().timeInMillis,
    initialEndCalendarAsLong: Long = Calendar.getInstance().timeInMillis + (86400000 * 2).toLong(),
    beginDateValidator: ((Calendar)-> Boolean)? = null,
    endDateValidator: ((Calendar)-> Boolean)? = null,
    callback: (beginCalendarAsLong: Long?, endCalendarAsLong: Long?) -> Unit
){

    val openBeginDateTimePicker = remember {
        mutableStateOf(false)
    }

    val openEndDateTimePicker = remember {
        mutableStateOf(false)
    }

    val beginCalendarAsLong = remember {
        mutableStateOf(initialBeginCalendarAsLong)
    }

    val endCalendarAsLong = remember {
        mutableStateOf(initialEndCalendarAsLong)
    }


    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape = RoundedCornerShape(16.dp))
    ) {

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    openBeginDateTimePicker.value = !openBeginDateTimePicker.value
                },
            headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_form_begin_title)) },
            trailingContent = {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val beginCalendar = Calendar.getInstance()
                    beginCalendar.timeInMillis = beginCalendarAsLong.value

                    Text(
                        text = CalendarUtils.toDateString(beginCalendar),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 15.sp,
                            textAlign = TextAlign.End,

                            ),
                        textAlign = TextAlign.End
                    )

                    Image(
                        painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                    )
                }


            }
        )

        Divider()

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    openEndDateTimePicker.value = !openEndDateTimePicker.value
                },
            headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_form_end_title)) },
            trailingContent = {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val endCalendar = Calendar.getInstance()
                    endCalendar.timeInMillis = endCalendarAsLong.value

                    Text(
                        text = CalendarUtils.toDateString(endCalendar),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 15.sp,
                            textAlign = TextAlign.End,

                            ),
                        textAlign = TextAlign.End
                    )

                    Image(
                        painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                    )
                }
            }
        )

        MaterialDatePickerDialog(
            modifier = Modifier,
            showDialog = openBeginDateTimePicker,
            confirmButtonCallback = {
                beginCalendarAsLong.value = it.timeInMillis
                callback.invoke(beginCalendarAsLong.value, endCalendarAsLong.value)
            },
            dateValidator = {
                if (beginDateValidator != null) {
                    beginDateValidator.invoke(it)
                } else {
                    val endCalendar = Calendar.getInstance()
                    endCalendar.timeInMillis = endCalendarAsLong.value
                    endCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    endCalendar.set(Calendar.MINUTE, 0)

                    val maxCalendar = Calendar.getInstance()
                    maxCalendar.add(Calendar.YEAR, 10)
                    val minCalendar = Calendar.getInstance()
                    minCalendar.add(Calendar.YEAR, -10)

                    it.timeInMillis in minCalendar.timeInMillis .. maxCalendar.timeInMillis && it.timeInMillis <= endCalendar.timeInMillis


                }
            }
        )

        MaterialDatePickerDialog(
            modifier = Modifier,
            showDialog = openEndDateTimePicker,
            confirmButtonCallback = {
                endCalendarAsLong.value = it.timeInMillis
                callback.invoke(beginCalendarAsLong.value, endCalendarAsLong.value)
            },
            dateValidator = {
                if (endDateValidator != null) {
                    endDateValidator.invoke(it)
                } else {
                    val beginCalendar = Calendar.getInstance()
                    beginCalendar.timeInMillis = beginCalendarAsLong.value
                    beginCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    beginCalendar.set(Calendar.MINUTE, 0)

                    val maxCalendar = Calendar.getInstance()
                    maxCalendar.add(Calendar.YEAR, 10)
                    val minCalendar = Calendar.getInstance()
                    minCalendar.add(Calendar.YEAR, -10)

                    it.timeInMillis in minCalendar.timeInMillis .. maxCalendar.timeInMillis && it.timeInMillis >= beginCalendar.timeInMillis
                }
            }
        )
    }
}
