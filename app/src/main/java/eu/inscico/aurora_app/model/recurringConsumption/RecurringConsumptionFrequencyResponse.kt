package eu.inscico.aurora_app.model.recurringConsumption


data class RecurringConsumptionFrequencyResponse(
    var unit: String? = null,
    var weekdays: List<Int>? = null,
    var dayOfMonth: Int? = null
) {
    companion object {

        fun from(item: RecurringConsumptionFrequency?): RecurringConsumptionFrequencyResponse? {

            if(item == null) return null

            val weekdayStrings = item.weekdays?.mapNotNull {
                RecurringConsumptionIntervalWeekday.parseIntervalWeekdayToInt(it)
            }

            return RecurringConsumptionFrequencyResponse(
                unit = RecurringConsumptionIntervalUnit.parseIntervalUnitToString(item.unit),
                weekdays = weekdayStrings,
                dayOfMonth = item.dayOfMonth
            )
        }
    }
}
