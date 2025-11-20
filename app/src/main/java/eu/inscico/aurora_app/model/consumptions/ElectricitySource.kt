package eu.inscico.aurora_app.model.consumptions

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R

enum class ElectricitySource {
    @Json(name = "default")
    DEFAULT,
    @Json(name = "defaultGreenProvider")
    DEFAULT_GREEN,
    @Json(name = "homePhotovoltaics")
    HOME_PHOTOVOLTAICS;

    companion object {
        fun parseStringToElectricitySource(electricitySourceString: String?): ElectricitySource {
            return when (electricitySourceString) {
                "default" -> ElectricitySource.DEFAULT
                "homePhotovoltaics" -> ElectricitySource.HOME_PHOTOVOLTAICS
                "defaultGreenProvider" -> ElectricitySource.DEFAULT_GREEN
                else -> ElectricitySource.DEFAULT
            }
        }

        fun parseElectricitySourceToString(electricitySource: ElectricitySource): String {
            return when (electricitySource) {
                DEFAULT -> "default"
                HOME_PHOTOVOLTAICS -> "homePhotovoltaics"
                DEFAULT_GREEN -> "defaultGreenProvider"
            }
        }

        fun ElectricitySource.getDisplayNameRes(): Int {
            return when (this) {
                DEFAULT -> R.string.electricity_source_default_standard
                HOME_PHOTOVOLTAICS -> R.string.electricity_source_home_photovoltaics
                DEFAULT_GREEN -> R.string.electricity_source_default_green
            }
        }

        fun ElectricitySource.getDisplayName(context: Context): String {
            return context.getString(this.getDisplayNameRes())
        }

        fun getElectricitySourceList(): List<ElectricitySource> {
            return listOf(
                ElectricitySource.DEFAULT,
                ElectricitySource.HOME_PHOTOVOLTAICS,
                ElectricitySource.DEFAULT_GREEN,
            )
        }

    }
}