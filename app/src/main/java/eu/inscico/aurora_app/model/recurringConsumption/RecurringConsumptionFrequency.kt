package eu.inscico.aurora_app.model.recurringConsumption

data class RecurringConsumptionFrequency(
    var unit: RecurringConsumptionIntervalUnit,
    var weekdays: List<RecurringConsumptionIntervalWeekday>?,
    var dayOfMonth: Int?
) {

    companion object {

        fun from(item: RecurringConsumptionFrequencyResponse?): RecurringConsumptionFrequency? {

            if(item == null) return null

            val unit = RecurringConsumptionIntervalUnit.parseStringToIntervalUnit(item.unit ?: return null) ?: return null

            val weekdays = item.weekdays?.mapNotNull {
                RecurringConsumptionIntervalWeekday.parseIntToIntervalWeekday(it)
            }

            return RecurringConsumptionFrequency(
                unit = unit,
                weekdays = weekdays,
                dayOfMonth = item.dayOfMonth
            )
        }
    }
}