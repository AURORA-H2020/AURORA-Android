package eu.inscico.aurora_app.model.recurringConsumption

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionType

enum class RecurringConsumptionIntervalUnit {
    @Json(name = "daily")
    DAILY,
    @Json(name = "weekly")
    WEEKLY,
    @Json(name = "monthly")
    MONTHLY;

    companion object {
        fun parseStringToIntervalUnit(recurringConsumptionIntervalUnit: String?): RecurringConsumptionIntervalUnit? {
            return when (recurringConsumptionIntervalUnit) {
                "daily" -> DAILY
                "weekly" -> WEEKLY
                "monthly" -> MONTHLY
                else -> null
            }
        }

        fun parseIntervalUnitToString(intervalUnit: RecurringConsumptionIntervalUnit?): String? {
            return when (intervalUnit) {
                DAILY -> "daily"
                WEEKLY -> "weekly"
                MONTHLY -> "monthly"
                null -> null
            }
        }

        fun getIntervalUnitList(): List<RecurringConsumptionIntervalUnit> {
            return listOf(
                DAILY,
                WEEKLY,
                MONTHLY
            )
        }

        fun RecurringConsumptionIntervalUnit.getDisplayNameRes(): Int {
            return when(this){
                DAILY -> R.string.home_recurring_consumptions_frequency_daily_title
                WEEKLY -> R.string.home_recurring_consumptions_frequency_weekly_title
                MONTHLY -> R.string.home_recurring_consumptions_frequency_monthly_title
            }
        }

        fun RecurringConsumptionIntervalUnit.getDisplayName(context: Context): String {
            return context.getString(getDisplayNameRes())
        }
    }
}