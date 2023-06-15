package eu.inscico.aurora_app.model.recurringConsumption

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R

enum class RecurringConsumptionIntervalWeekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    companion object {
        fun parseStringToIntervalWeekday(recurringConsumptionIntervalWeekday: String?): RecurringConsumptionIntervalWeekday? {
            return when (recurringConsumptionIntervalWeekday) {
                "Monday" -> MONDAY
                "Tuesday" -> TUESDAY
                "Wednesday" -> WEDNESDAY
                "Thursday" -> THURSDAY
                "Friday" -> FRIDAY
                "Saturday" -> SATURDAY
                "Sunday" -> SUNDAY
                else -> null
            }
        }

        fun parseIntToIntervalWeekday(recurringConsumptionIntervalWeekday: Int?): RecurringConsumptionIntervalWeekday? {
            return when (recurringConsumptionIntervalWeekday) {
                1 -> MONDAY
                2 -> TUESDAY
                3 -> WEDNESDAY
                4 -> THURSDAY
                5 -> FRIDAY
                6 -> SATURDAY
                7 -> SUNDAY
                else -> null
            }
        }

        fun parseIntervalWeekdayToInt(intervalWeekday: RecurringConsumptionIntervalWeekday?): Int? {
            return when (intervalWeekday) {
                MONDAY -> 1
                TUESDAY -> 2
                WEDNESDAY -> 3
                THURSDAY -> 4
                FRIDAY -> 5
                SATURDAY -> 6
                SUNDAY -> 7
                null -> null
            }
        }

        fun getWeekdayList(): List<RecurringConsumptionIntervalWeekday> {
            return listOf(
                MONDAY,
                TUESDAY,
                WEDNESDAY,
                THURSDAY,
                FRIDAY,
                SATURDAY,
                SUNDAY
            )
        }

        fun RecurringConsumptionIntervalWeekday.getDisplayNameRes(): Int {
            return when(this){
                MONDAY -> R.string.settings_notifications_frequency_weekly_weekday_monday_title
                TUESDAY -> R.string.settings_notifications_frequency_weekly_weekday_tuesday_title
                WEDNESDAY -> R.string.settings_notifications_frequency_weekly_weekday_wednesday_title
                THURSDAY -> R.string.settings_notifications_frequency_weekly_weekday_thursday_title
                FRIDAY -> R.string.settings_notifications_frequency_weekly_weekday_friday_title
                SATURDAY -> R.string.settings_notifications_frequency_weekly_weekday_saturday_title
                SUNDAY -> R.string.settings_notifications_frequency_weekly_weekday_sunday_title
            }
        }

        fun RecurringConsumptionIntervalWeekday.getDisplayName(context: Context): String {
            return context.getString(getDisplayNameRes())
        }
    }
}