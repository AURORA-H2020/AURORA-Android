package eu.inscico.aurora_app.model.reminder

import java.text.SimpleDateFormat
import java.util.*

/**
 * A custom calendar-item for the internal work of the [CalendarCard]
 */
class CalendarDayItem {

    var day: Int = 1
        private set

    var month: Int = 1
        private set

    var year: Int = 1
        private set

    private fun setValues(day: Int, month: Int, year: Int) {
        this.day = day
        this.month = month
        this.year = year
    }

    fun isSameAs(day: CalendarDayItem?): Boolean {
        if (day == null) return false
        return day.day == this.day && day.month == this.month && day.year == this.year
    }

    fun toCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar
    }

    fun toDate(): Date {
        return toCalendar().time
    }

    fun toTimestamp(): Long {
        return toCalendar().timeInMillis
    }


    fun toDateString(dateFormat: String = "dd.MM.yy"): String {
        val calendarDayInMillis = this.toTimestamp()
        val df2 = SimpleDateFormat(dateFormat)
        return df2.format(calendarDayInMillis)
    }

    fun copy(): CalendarDayItem {
        val copy = CalendarDayItem()
        copy.setValues(day, month , year)
        return copy
    }

    companion object {
        fun today(): CalendarDayItem {
            val calendar = Calendar.getInstance()
            return fromCalendar(calendar)
        }

        fun fromTimestamp(timestamp: Long): CalendarDayItem {
            val calendar = Calendar.getInstance()
            calendar.time = Date(timestamp)
            return fromCalendar(calendar)
        }

        fun fromDate(date: Date): CalendarDayItem {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return fromCalendar(calendar)
        }

        fun fromCalendar(calendar: Calendar): CalendarDayItem {
            val dayItem = CalendarDayItem()
            dayItem.setValues(
                day = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR)
            )
            return dayItem
        }

        fun fromValues(day: Int, month: Int, year: Int): CalendarDayItem {
            val dayItem = CalendarDayItem()
            dayItem.setValues(
                day = day,
                month = month - 1,
                year = year
            )
            return dayItem
        }

    }

    data class Sample(
        val test: Int = 1
    )

}