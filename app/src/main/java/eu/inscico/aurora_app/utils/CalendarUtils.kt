package eu.inscico.aurora_app.utils

import android.util.Log
import eu.inscico.aurora_app.model.reminder.CalendarDayItem
import eu.inscico.aurora_app.model.reminder.CalendarMonthItem
import eu.inscico.aurora_app.model.reminder.CalendarWeekItem
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * a collection of functions to work with calendar-related data
 */

class CalendarUtils {

    companion object {

        fun getGlobalCalendar(calendarDayItem: CalendarDayItem): Calendar {

            val calendar = calendarDayItem.toCalendar()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            return calendar

        }


        fun getWeekDayNames(formatPattern: String = "EEEEE"): List<String> {

            val namesList = mutableListOf<String>()
            val calendar = Calendar.getInstance()

            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val firstDayOfWeek = calendar.firstDayOfWeek
            val dayOfWeekDiff = firstDayOfWeek - currentDayOfWeek

            calendar.add(Calendar.DAY_OF_YEAR, dayOfWeekDiff)
            repeat(7) {

                val sdf = SimpleDateFormat(formatPattern)
                val dayOfTheWeek: String = sdf.format(calendar.time)

                namesList.add(dayOfTheWeek)
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            return namesList
        }

        fun getWeekDays(formatPattern: String = "EEEEE"): List<CalendarWeekItem> {

            val weekDays = mutableListOf<CalendarWeekItem>()
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.DAY_OF_WEEK, 1)

            Log.d("getWeekDaysHELP", toDateString(calendar, "EEEE"))

            repeat(7) {
                val name = toDateString(calendar, "EEEE")
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                val weekDay = CalendarWeekItem(name = name, dayOfWeek = dayOfWeek)

                weekDays.add(weekDay)
                calendar.add(Calendar.DAY_OF_WEEK, 1)
            }

            return weekDays
        }

        fun getWeeksOfMonth(
            calendarDayItem: CalendarDayItem,
            requestedMonth: Int? = null,
            onlySameMonth: Boolean = true
        ): List<List<CalendarDayItem?>> {

            val weeksList = mutableListOf<List<CalendarDayItem?>>()

            val calendar = Calendar.getInstance()
            calendar.set(calendarDayItem.year, calendarDayItem.month, 1)

            val currentMonth = requestedMonth ?: calendar.get(Calendar.MONTH)
            var finishedMonth = false
            while (!finishedMonth) {

                val currentDay = CalendarDayItem.fromCalendar(calendar)
                val currentWeek = getDaysOfWeek(currentDay, currentMonth, onlySameMonth)

                if (currentWeek.find { it?.month == currentMonth } != null) {
                    weeksList.add(currentWeek)
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                } else {
                    finishedMonth = true
                }
            }

            return weeksList
        }

        fun getDaysOfMonth(
            requestedMonth: Int,
        ): List<CalendarDayItem> {

            val days = mutableListOf<CalendarDayItem>()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, requestedMonth)
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            Log.d("getDaysOfMonthHelp", toDateString(calendar))

            while (requestedMonth == calendar.get(Calendar.MONTH)) {
                Log.d("getDaysOfMonthHelp", toDateString(calendar))
                val day = CalendarDayItem.fromValues(
                    year = calendar.get(Calendar.YEAR),
                    month = requestedMonth,
                    day = calendar.get(Calendar.DAY_OF_MONTH)
                )
                days.add(day)
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            return days
        }


        fun getDaysOfWeek(
            calendarDayItem: CalendarDayItem,
            requestedMonth: Int? = null,
            onlySameMonth: Boolean = true
        ): List<CalendarDayItem?> {

            val calendar = Calendar.getInstance()
            calendar.set(calendarDayItem.year, calendarDayItem.month, calendarDayItem.day)

            val initialMonth = requestedMonth ?: calendar.get(Calendar.MONTH)
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 2
            val firstDayOfWeek = calendar.firstDayOfWeek // 1
            var dayOfWeekDiff = firstDayOfWeek - currentDayOfWeek
            if (dayOfWeekDiff > 0) {
                dayOfWeekDiff -= 7
            }

            val calendarDayItems = mutableListOf<CalendarDayItem?>()

            calendar.add(Calendar.DAY_OF_YEAR, dayOfWeekDiff)
            repeat(7) {

                val currentMonth = calendar.get(Calendar.MONTH)

                calendarDayItems.add(
                    if (currentMonth != requestedMonth && onlySameMonth) {
                        null
                    } else {
                        CalendarDayItem.fromCalendar(calendar)
                    }
                )


                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            return calendarDayItems

        }

        fun getMonthItems(): List<CalendarMonthItem> {

            val months = mutableListOf<CalendarMonthItem>()
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("MMMM", Locale.getDefault())

            for (index in 0..11) {
                calendar.set(Calendar.MONTH, index)
                months.add(
                    CalendarMonthItem(
                        number = index,
                        name = sdf.format(calendar.time)
                    )
                )
            }

            return months
        }

        fun dateStringToCalendar(dateString: String, dateFormat: String = "dd.MM.yyyy"): Calendar? {
            val sdf = SimpleDateFormat(dateFormat)
            return try {
                val stringAsDate = sdf.parse(dateString)
                if (stringAsDate != null) {
                    val stringAsCalendar = Calendar.getInstance()
                    stringAsCalendar.time = stringAsDate
                    stringAsCalendar
                } else {
                    null
                }
            } catch (ex: ParseException) {
                null
            }
        }


        fun toDateString(calendarDay: Calendar, dateFormat: String = "dd.MM.yyyy"): String {
            val dateMillis = calendarDay.timeInMillis
            val df2 = SimpleDateFormat(dateFormat)
            return df2.format(dateMillis)
        }
    }

}