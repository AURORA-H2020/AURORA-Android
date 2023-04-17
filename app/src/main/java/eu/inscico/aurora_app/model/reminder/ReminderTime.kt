package eu.inscico.aurora_app.model.reminder

import eu.inscico.aurora_app.model.Frequency
import java.util.*

sealed class ReminderTime(
    val frequency: Frequency
){

    data class ReminderTimeMonthly(
        val day: CalendarDayItem,
        val time: Calendar
    ): ReminderTime(Frequency.MONTHLY)

    data class ReminderTimeWeekly(
        val time: Calendar,
        val weekday: CalendarWeekItem
    ): ReminderTime(Frequency.WEEKLY)

    data class ReminderTimeDaily(
        val time: Calendar,
    ): ReminderTime(Frequency.DAILY)

    data class ReminderTimeYearly(
        val day: CalendarDayItem,
        val month: CalendarMonthItem,
        val time: Calendar
    ): ReminderTime(Frequency.YEARlY)
}
