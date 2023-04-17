package eu.inscico.aurora_app.ui.components.timePicker.time

import java.util.*

class TimeItem {

    var hour: Int = 1
        private set

    var hourString: String = "01"
        private set


    var minute: Int = 1
        private set

    var minuteString: String = "01"
        private set



    private fun setValues(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute
    }

    fun isSameAs(time: TimeItem?): Boolean {
        if (time == null) return false
        return time.hour == this.hour && time.minute == this.minute
    }

    fun toCalendar(day: Calendar? = null): Calendar {
        val calendar = day ?: Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar
    }

    fun toDate(day: Calendar? = null): Date {
        return toCalendar(day).time
    }

    fun toTimestamp(day: Calendar? = null): Long {
        return toCalendar(day).timeInMillis
    }

    companion object {

        fun fromCalendar(calendar: Calendar): TimeItem {
            val timeItem = TimeItem()
            timeItem.setValues(
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE)
            )
            return timeItem
        }

        fun fromValues(hour: Int, minute: Int): TimeItem {
            val timeItem = TimeItem()
            timeItem.setValues(
                hour = hour,
                minute = minute
            )
            return timeItem
        }
    }
}