package eu.inscico.aurora_app.ui.components.timePicker.time

import android.util.Log
import java.util.*

class TimeUtils {

    companion object {

        fun isHourReal(hour: Int): Boolean {
            return hour in 0..24
        }

        fun getValidHourString(hourString: String): String {
            val hour = if (hourString.isNotEmpty()) {
                hourString.toInt()
            } else {
                0
            }

            return if (hourString.length < 2) {
                if (hour > 2) {
                    String.format("%02d", hour)
                } else {
                    hour.toString()
                }
            } else {
                val shortenedHourString = if (hour > 24) {
                    hourString.substring(hourString.length - 1)
                } else {
                    hourString.substring(hourString.length - 2)
                }
                val shortenedHour = shortenedHourString.toInt()
                if (isHourReal(shortenedHour)) {
                    String.format("%02d", shortenedHour)
                } else {
                    getLatestHour().toString()
                }
            }
        }

        fun getValidMinuteFromInput(minuteString: String): Int {
            Log.d("getValidMinuteFromInput", "minuteString: $minuteString")
            return when {
                minuteString.isEmpty() -> {
                    0
                }
                minuteString.length > 2 -> {
                    val shortenedMinuteString = if (minuteString.toInt() > 59) {
                        minuteString.substring(minuteString.length - 1)
                    } else {
                        minuteString.substring(minuteString.length - 2)
                    }
                    Log.d("getValidMinuteFromInput", "shortened: $shortenedMinuteString")
                    val shortenedMinute = shortenedMinuteString.toInt()
                    if (isMinuteReal(shortenedMinute)) {
                        shortenedMinute
                    } else {
                        getLatestMinute()
                    }
                }
                else -> {
                    minuteString.toInt()
                }
            }
        }

        fun getValidMinuteString(minuteString: String): String {

            val validMinute = getValidMinuteFromInput(minuteString)

            return when {
                validMinute < 10 -> {
                    String.format("%02d", validMinute)
                }
                else -> {
                    validMinute.toString()
                }
            }
        }

        fun isMinuteReal(minute: Int): Boolean {
            return minute in 0..59
        }

        fun getLatestHour(is24Hour: Boolean = true): Int {
            return if (is24Hour) {
                24
            } else {
                12
            }
        }

        fun getLatestMinute(): Int {
            return 59
        }

        fun setTimeToCalendar(timeItem: TimeItem, day: Calendar? = null): Calendar {
            val calendar = day ?: Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, timeItem.hour)
            calendar.set(Calendar.MINUTE, timeItem.minute)
            return calendar
        }
    }
}