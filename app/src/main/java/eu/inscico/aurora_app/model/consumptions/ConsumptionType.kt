package eu.inscico.aurora_app.model.consumptions

import android.content.Context
import eu.inscico.aurora_app.R

enum class ConsumptionType {
    ELECTRICITY,
    HEATING,
    TRANSPORTATION;

    companion object {
        fun parseStringToConsumptionType(consumptionTypeString: String?): ConsumptionType? {
            return when (consumptionTypeString) {
                "transportation" -> TRANSPORTATION
                "heating" -> HEATING
                "electricity" -> ELECTRICITY
                else -> null
            }
        }

        fun parseConsumptionTypeToString(consumptionType: ConsumptionType?): String? {
            return when (consumptionType) {
                ELECTRICITY -> "electricity"
                HEATING -> "heating"
                TRANSPORTATION -> "transportation"
                null -> null
            }
        }

        fun ConsumptionType.getDisplayNameRes(): Int {
            return when(this){
                ELECTRICITY -> R.string.home_consumptions_type_electricity_title
                HEATING -> R.string.home_consumptions_type_heating_title
                TRANSPORTATION -> R.string.home_consumptions_type_transportation_title
            }
        }

        fun ConsumptionType.getDisplayName(context: Context): String {
            return context.getString(getDisplayNameRes())
        }

    }
}