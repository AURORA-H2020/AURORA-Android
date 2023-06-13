package eu.inscico.aurora_app.model.consumptions

import android.content.Context
import androidx.compose.ui.graphics.Color
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.ui.theme.heatingRed
import eu.inscico.aurora_app.ui.theme.mobilityBlue

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

        fun ConsumptionType.getIconRes(): Int {
            return when(this){
                ELECTRICITY -> R.drawable.outline_electric_bolt_24
                HEATING -> R.drawable.outline_local_fire_department_24
                TRANSPORTATION -> R.drawable.outline_directions_car_24
            }
        }

        fun ConsumptionType.getColor(): Color {
            return when(this){
                ELECTRICITY -> electricityYellow
                HEATING -> heatingRed
                TRANSPORTATION -> mobilityBlue
            }
        }

        fun getConsumptionTypeList(): List<ConsumptionType> {
            return listOf(
                TRANSPORTATION,
                HEATING,
                ELECTRICITY
            )
        }

    }
}