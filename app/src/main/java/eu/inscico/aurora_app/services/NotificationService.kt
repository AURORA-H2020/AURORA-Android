package eu.inscico.aurora_app.services

import android.content.Context
import eu.inscico.aurora_app.model.reminder.ReminderTime
import eu.inscico.aurora_app.services.jsonParsing.JsonParsingService
import eu.inscico.aurora_app.utils.PrefsUtils
import java.util.*

class NotificationService(
    private val context: Context,
    private val _jsonParsingService: JsonParsingService
    ) {

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

    var electricityReminder: ReminderTime?
        get() {
            val reminderAsJson = PrefsUtils.get(context, "electricityReminder", "")
            val reminder = if(reminderAsJson.isNotEmpty()){
                _jsonParsingService.fromJson(reminderAsJson, type = ReminderTime::class.java) as ReminderTime?
            } else {
                null
            }
            return reminder
        }
        set(value) {
            if(value != null){
                val reminderAsJson = _jsonParsingService.toJson(value)
                PrefsUtils.save(context, "electricityReminder", reminderAsJson)
            }

        }

    var heatingReminder: ReminderTime?
        get() {
            val reminderAsJson = PrefsUtils.get(context, "heatingReminder", "")
            val reminder = if(reminderAsJson.isNotEmpty()){
                _jsonParsingService.fromJson(reminderAsJson, type = ReminderTime::class.java) as ReminderTime?
            } else {
                null
            }
            return reminder
        }
        set(value) {
            if(value != null){
                val reminderAsJson = _jsonParsingService.toJson(value)
                PrefsUtils.save(context, "heatingReminder", reminderAsJson)
            }

        }

    var mobilityReminder: ReminderTime?
        get() {
            val reminderAsJson = PrefsUtils.get(context, "mobilityReminder", "")
            val reminder = if(reminderAsJson.isNotEmpty()){
                _jsonParsingService.fromJson(reminderAsJson, type = ReminderTime::class.java) as ReminderTime?
            } else {
                null
            }
            return reminder
        }
        set(value) {
            if(value != null){
                val reminderAsJson = _jsonParsingService.toJson(value)
                PrefsUtils.save(context, "mobilityReminder", reminderAsJson)
            }

        }

    fun getNextNotificationTime(reminderTime: ReminderTime): Calendar {
        return when(reminderTime){
            is ReminderTime.ReminderTimeDaily -> {
                val calendar = Calendar.getInstance()
                if(calendar.timeInMillis >= reminderTime.time.timeInMillis){
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
}