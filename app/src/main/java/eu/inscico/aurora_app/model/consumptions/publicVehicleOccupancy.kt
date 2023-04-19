package eu.inscico.aurora_app.model.consumptions

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R

enum class PublicVehicleOccupancy {
    @Json(name = "almostEmpty") ALMOST_EMPTY,
    @Json(name = "medium") MEDIUM,
    @Json(name = "nearlyFull") NEARLY_FULL;

    companion object {

        fun parseStringToPublicVehicleOccupancy(string: String?): PublicVehicleOccupancy? {
            return when (string) {
                "almostEmpty" -> ALMOST_EMPTY
                "medium" -> MEDIUM
                "nearlyFull" -> NEARLY_FULL
                else -> null
            }
        }

        fun PublicVehicleOccupancy.parsePublicVehicleOccupancyToString(): String {
            return when (this) {
                ALMOST_EMPTY -> "almostEmpty"
                MEDIUM -> "medium"
                NEARLY_FULL -> "nearlyFull"
                null -> "medium"
            }
        }

        fun getAll(): List<PublicVehicleOccupancy>{
            return listOf(
            ALMOST_EMPTY,
            MEDIUM,
            NEARLY_FULL
            )
        }

        fun PublicVehicleOccupancy.getDisplayRes(): Int{
            return when(this){
                ALMOST_EMPTY -> R.string.home_add_consumption_transportation_public_occupancy_almost_empty_title
                MEDIUM -> R.string.home_add_consumption_transportation_public_occupancy_medium_title
                NEARLY_FULL -> R.string.home_add_consumption_transportation_public_occupancy_nearly_full_title
            }
        }

        fun PublicVehicleOccupancy.getDisplayName(context: Context): String {
            return context.getString(this.getDisplayRes())
        }
    }
}