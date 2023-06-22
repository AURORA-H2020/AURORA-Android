package eu.inscico.aurora_app.services.notification

import android.R
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.reminder.ReminderTime
import eu.inscico.aurora_app.services.jsonParsing.JsonParsingService
import eu.inscico.aurora_app.utils.IntentUtils
import eu.inscico.aurora_app.utils.PendingIntentCategory
import eu.inscico.aurora_app.utils.PrefsUtils
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import java.util.concurrent.TimeUnit


class NotificationService(
    private val context: Context,
    private val _jsonParsingService: JsonParsingService
) {

    companion object {
        const val BEHAVIOUR_CHANGED_POPUP_INTERVAL = 1209600000
    }

    var notificationPermissionHandler: NotificationPermissionHandler? = null

    var electricityReminderActivePrefs: Boolean
        get() {
            return PrefsUtils.get(context, "electricityReminderActive", false)
        }
        set(value) {
            PrefsUtils.save(context, "electricityReminderActive", value)
        }

    var heatingReminderActivePrefs: Boolean
        get() {
            return PrefsUtils.get(context, "heatingReminderActive", false)
        }
        set(value) {
            PrefsUtils.save(context, "heatingReminderActive", value)
        }

    var mobilityReminderActivePrefs: Boolean
        get() {
            return PrefsUtils.get(context, "mobilityReminderActive", false)
        }
        set(value) {
            PrefsUtils.save(context, "mobilityReminderActive", value)
        }

    val behaviourChangesPopupActivePrefsLive = MutableLiveData<Boolean>()
    var behaviourChangesPopupActivePrefs: Boolean
        get() {
            val isActive = PrefsUtils.get(context, "behaviourChangesPopupActive", false)
            behaviourChangesPopupActivePrefsLive.postValue(isActive)
            return isActive
        }
        set(value) {
            PrefsUtils.save(context, "behaviourChangesPopupActive", value)
            behaviourChangesPopupActivePrefsLive.postValue(value)
        }

    var electricityReminder: ReminderTime?
        get() {
            val reminderAsJson = PrefsUtils.get(context, "electricityReminder", "")
            val reminder = if (reminderAsJson.isNotEmpty()) {
                _jsonParsingService.fromJson(
                    reminderAsJson,
                    type = ReminderTime::class.java
                ) as ReminderTime?
            } else {
                null
            }
            return reminder
        }
        set(value) {
            if (value != null) {
                val reminderAsJson = _jsonParsingService.toJson(value)
                PrefsUtils.save(context, "electricityReminder", reminderAsJson)
            }

        }

    var heatingReminder: ReminderTime?
        get() {
            val reminderAsJson = PrefsUtils.get(context, "heatingReminder", "")
            val reminder = if (reminderAsJson.isNotEmpty()) {
                _jsonParsingService.fromJson(
                    reminderAsJson,
                    type = ReminderTime::class.java
                ) as ReminderTime?
            } else {
                null
            }
            return reminder
        }
        set(value) {
            if (value != null) {
                val reminderAsJson = _jsonParsingService.toJson(value)
                PrefsUtils.save(context, "heatingReminder", reminderAsJson)
            }

        }

    var mobilityReminder: ReminderTime?
        get() {
            val reminderAsJson = PrefsUtils.get(context, "mobilityReminder", "")
            val reminder = if (reminderAsJson.isNotEmpty()) {
                _jsonParsingService.fromJson(
                    reminderAsJson,
                    type = ReminderTime::class.java
                ) as ReminderTime?
            } else {
                null
            }
            return reminder
        }
        set(value) {
            if (value != null) {
                val reminderAsJson = _jsonParsingService.toJson(value)
                PrefsUtils.save(context, "mobilityReminder", reminderAsJson)
            }

        }

    var behaviourChangeReminder: Long?
        get() {
            return PrefsUtils.get(context, "behaviourChangeReminder", 0)
        }
        set(value) {
            if (value != null) {
                PrefsUtils.save(context, "behaviourChangeReminder", value)
            }

        }

    fun getNextNotificationTime(reminderTime: ReminderTime): Calendar {
        return when (reminderTime) {
            is ReminderTime.ReminderTimeDaily -> {
                val calendar = Calendar.getInstance()
                if (calendar.timeInMillis >= reminderTime.time.timeInMillis) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                calendar.set(Calendar.HOUR_OF_DAY, reminderTime.time.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, reminderTime.time.get(Calendar.MINUTE))
                calendar
            }
            is ReminderTime.ReminderTimeMonthly -> {
                val calendar = Calendar.getInstance()

                when {
                    calendar.get(Calendar.DAY_OF_MONTH) > reminderTime.day.day -> {
                        calendar.add(Calendar.MONTH, 1)
                    }

                    calendar.get(Calendar.DAY_OF_MONTH) == reminderTime.day.day -> {
                        if (calendar.timeInMillis >= reminderTime.time.timeInMillis) {
                            calendar.add(Calendar.MONTH, 1)
                        }
                    }
                }
                calendar.set(Calendar.DAY_OF_MONTH, reminderTime.day.day)
                calendar.set(Calendar.HOUR_OF_DAY, reminderTime.time.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, reminderTime.time.get(Calendar.MINUTE))
                return calendar
            }
            is ReminderTime.ReminderTimeWeekly -> {
                val calendar = Calendar.getInstance()

                when {
                    calendar.get(Calendar.DAY_OF_WEEK) > reminderTime.weekday.dayOfWeek -> {
                        calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    }

                    calendar.get(Calendar.DAY_OF_WEEK) == reminderTime.weekday.dayOfWeek -> {
                        if (calendar.timeInMillis >= reminderTime.time.timeInMillis) {
                            calendar.add(Calendar.WEEK_OF_YEAR, 1)
                        }
                    }
                }

                calendar.set(Calendar.DAY_OF_WEEK, reminderTime.weekday.dayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, reminderTime.time.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, reminderTime.time.get(Calendar.MINUTE))
                calendar
            }
            is ReminderTime.ReminderTimeYearly -> {
                val calendar = Calendar.getInstance()

                when {
                    calendar.get(Calendar.MONTH) > reminderTime.month.number -> {
                        calendar.add(Calendar.YEAR, 1)
                    }

                    calendar.get(Calendar.MONTH) == reminderTime.month.number -> {
                        if (calendar.get(Calendar.DAY_OF_MONTH) > reminderTime.day.day) {
                            calendar.add(Calendar.YEAR, 1)
                        } else if (calendar.get(Calendar.DAY_OF_MONTH) == reminderTime.day.day) {
                            if (calendar.timeInMillis >= reminderTime.time.timeInMillis) {
                                calendar.add(Calendar.YEAR, 1)
                            }
                        }
                    }
                }
                calendar.set(Calendar.MONTH, reminderTime.month.number)
                calendar.set(Calendar.DAY_OF_MONTH, reminderTime.day.day)
                calendar.set(Calendar.HOUR_OF_DAY, reminderTime.time.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, reminderTime.time.get(Calendar.MINUTE))
                return calendar
            }
        }
    }

    fun updateBehaviourChangePopup(time: Long, isEnabled: Boolean = true) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiver = RecurringConsumptionsBehaviourChangedPopupReceiver::class.java
        val intent = Intent(context, receiver)

        val pendingIntent = IntentUtils.getCorrectPendingIntent(
            context = context,
            id = 333,
            intent = intent,
            flag = PendingIntent.FLAG_CANCEL_CURRENT,
            category = PendingIntentCategory.BROADCAST
        )

        alarmManager.cancel(pendingIntent)
        if(isEnabled){
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }

    fun updateNotificationAlarm(notificationType: ConsumptionType,nextNotificationTime: Calendar, isEnabled: Boolean = true) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val receiver = when(notificationType){
            ConsumptionType.ELECTRICITY -> ElectricityReminderAlarmReceiver::class.java
            ConsumptionType.HEATING -> HeatingReminderAlarmReceiver::class.java
            ConsumptionType.TRANSPORTATION -> TransportationReminderAlarmReceiver::class.java
        }
        val intent = Intent(context, receiver)

        val pendingIntent = IntentUtils.getCorrectPendingIntent(
            context = context,
            id = 444,
            intent = intent,
            flag = PendingIntent.FLAG_CANCEL_CURRENT,
            category = PendingIntentCategory.BROADCAST
        )

        alarmManager.cancel(pendingIntent)

        nextNotificationTime.set(Calendar.SECOND, 0)
        nextNotificationTime.set(Calendar.MILLISECOND, 0)

        if(isEnabled){
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextNotificationTime.timeInMillis, pendingIntent)
        }
    }
}