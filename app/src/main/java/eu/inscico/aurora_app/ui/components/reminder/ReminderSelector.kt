package eu.inscico.aurora_app.ui.components.reminder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.Frequency
import eu.inscico.aurora_app.model.reminder.CalendarDayItem
import eu.inscico.aurora_app.model.reminder.CalendarMonthItem
import eu.inscico.aurora_app.model.reminder.CalendarWeekItem
import eu.inscico.aurora_app.model.reminder.ReminderTime
import eu.inscico.aurora_app.services.notification.NotificationService
import eu.inscico.aurora_app.utils.CalendarUtils
import org.koin.androidx.compose.get
import java.util.*

@Composable
fun ReminderSelector(
    reminderTime: ReminderTime?,
    notificationService: NotificationService = get(),
    callback: (ReminderTime) -> Unit
){

    val context = LocalContext.current
    val currentCalendar = Calendar.getInstance()

    val addTimeSelector = remember {
        mutableStateOf(true)
    }
    val addDaySelector = remember {
        mutableStateOf(true)
    }
    val addMonthSelector = remember {
        mutableStateOf(false)
    }
    val addWeekdaySelector = remember {
        mutableStateOf(false)
    }

    val selectedFrequency = remember {
        mutableStateOf(reminderTime?.frequency ?: Frequency.MONTHLY)
    }
    when(selectedFrequency.value){
        Frequency.DAILY -> {
            addTimeSelector.value = true
            addDaySelector.value = false
            addWeekdaySelector.value = false
            addMonthSelector.value = false
        }
        Frequency.WEEKLY -> {
            addTimeSelector.value = true
            addDaySelector.value = false
            addWeekdaySelector.value = true
            addMonthSelector.value = false
        }
        Frequency.MONTHLY -> {
            addTimeSelector.value = true
            addDaySelector.value = true
            addWeekdaySelector.value = false
            addMonthSelector.value = false
        }
        Frequency.YEARlY -> {
            addTimeSelector.value = true
            addDaySelector.value = true
            addWeekdaySelector.value = false
            addMonthSelector.value = true
        }
    }

    val selectedMonth = remember {
        mutableStateOf(getInitialMonthValue(reminderTime))
    }

    val selectedWeek = remember {
        mutableStateOf(getInitialWeekValue(reminderTime))
    }

    val selectedDay = remember {
        mutableStateOf(getInitialDayValue(reminderTime))
    }

    val selectedTime = remember {
        mutableStateOf(getInitialTimeValue(reminderTime))
    }

    fun getReminderNotificationTime(): ReminderTime {
        return when (selectedFrequency.value) {
            Frequency.DAILY -> {
                val time = Calendar.getInstance()
                time.timeInMillis = selectedTime.value.timeInMillis
                ReminderTime.ReminderTimeDaily(
                        time = time
                )
            }
            Frequency.WEEKLY -> {
                val week = selectedWeek.value
                val time = Calendar.getInstance()
                time.timeInMillis = selectedTime.value.timeInMillis
                ReminderTime.ReminderTimeWeekly(
                    weekday = week,
                    time = time
                )
            }
            Frequency.MONTHLY -> {
                val day = selectedDay.value
                val time = Calendar.getInstance()
                time.timeInMillis = selectedTime.value.timeInMillis
                ReminderTime.ReminderTimeMonthly(
                    day = day,
                    time = time
                )
            }
            Frequency.YEARlY -> {
                val day = selectedDay.value
                val month = selectedMonth.value
                val time = Calendar.getInstance()
                time.timeInMillis = selectedTime.value.timeInMillis
                ReminderTime.ReminderTimeYearly(
                    day = day,
                    month = month,
                    time = time
                )
            }
        }
    }

    val notificationTime = remember {
        mutableStateOf(getReminderNotificationTime())
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(shape = RoundedCornerShape(16.dp))
    ) {

        ReminderFrequencySelector(
            initialSelection = selectedFrequency.value
        ) {
            selectedFrequency.value = it
            notificationTime.value = getReminderNotificationTime()
            callback.invoke(notificationTime.value)
        }

        Divider()

        if (addMonthSelector.value) {
            ReminderMonthSelector(initialValue = selectedMonth.value) {
                selectedMonth.value = it
                notificationTime.value = getReminderNotificationTime()
                callback.invoke(notificationTime.value)
            }
            Divider()
        }

        if (addDaySelector.value) {
            ReminderDaySelector(month = selectedMonth.value, initialValue = selectedDay.value) {
                selectedDay.value = it
                notificationTime.value = getReminderNotificationTime()
                callback.invoke(notificationTime.value)
            }
            Divider()
        }

        if (addWeekdaySelector.value) {
            ReminderWeekSelector(initialValue = selectedWeek.value) {
                selectedWeek.value = it
                notificationTime.value = getReminderNotificationTime()
                callback.invoke(notificationTime.value)
            }
            Divider()
        }

        if (addTimeSelector.value) {
            ReminderTimeSelector(initialValue = selectedTime.value) {
                selectedTime.value = it
                val newNotificationTime = getReminderNotificationTime()
                notificationTime.value = newNotificationTime

                callback.invoke(notificationTime.value)
            }

        }
    }

    notificationTime.value.let {
        Text(
            text = context.getString(
                R.string.settings_notifications_next_reminder_send_info,
                CalendarUtils.toDateString(notificationService.getNextNotificationTime(it), "dd.MM.yyyy, HH:mm")),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}


fun getInitialMonthValue(reminderTime: ReminderTime?): CalendarMonthItem {
    return if(reminderTime is ReminderTime.ReminderTimeYearly){
        reminderTime.month
    } else {
        val currentCalendar = Calendar.getInstance()
        CalendarMonthItem(name = CalendarUtils.toDateString(currentCalendar, "MMMM"), number = currentCalendar.get(Calendar.MONTH))
    }
}

fun getInitialWeekValue(reminderTime: ReminderTime?): CalendarWeekItem {
    return if(reminderTime is ReminderTime.ReminderTimeWeekly){
        reminderTime.weekday
    } else {
        val currentCalendar = Calendar.getInstance()
        CalendarWeekItem(name = CalendarUtils.toDateString(currentCalendar, "EEEE"), dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK))
    }
}

fun getInitialDayValue(reminderTime: ReminderTime?): CalendarDayItem {
    return when(reminderTime) {
        is ReminderTime.ReminderTimeMonthly -> {
            reminderTime.day
        }
        is ReminderTime.ReminderTimeYearly -> {
            reminderTime.day
        }
        is ReminderTime.ReminderTimeDaily,
        is ReminderTime.ReminderTimeWeekly -> {
            val currentCalendar = Calendar.getInstance()
            CalendarDayItem.fromCalendar(currentCalendar)
        }
        null -> {
            val currentCalendar = Calendar.getInstance()
            CalendarDayItem.fromCalendar(currentCalendar)
        }
    }
}

fun getInitialTimeValue(reminderTime: ReminderTime?): Calendar {
    return when(reminderTime) {
        is ReminderTime.ReminderTimeMonthly -> {
            reminderTime.time
        }
        is ReminderTime.ReminderTimeYearly -> {
            reminderTime.time
        }
        is ReminderTime.ReminderTimeDaily -> {
            reminderTime.time
        }
        is ReminderTime.ReminderTimeWeekly -> {
            reminderTime.time
        }
        null -> {
            Calendar.getInstance()
        }
    }
}