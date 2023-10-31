package eu.inscico.aurora_app.model.consumptions

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R

enum class ElectricitySource {
    @Json(name = "default")
    DEFAULT,
    @Json(name = "homePhotovoltaics")
    HOME_PHOTOVOLTAICS;

    companion object {
        fun parseStringToElectricitySource(electricitySourceString: String?): ElectricitySource {
            return when (electricitySourceString) {
                "default" -> ElectricitySource.DEFAULT
                "homePhotovoltaics" -> ElectricitySource.HOME_PHOTOVOLTAICS
                else -> ElectricitySource.DEFAULT
            }
        }

        fun parseElectricitySourceToString(electricitySource: ElectricitySource): String {
            return when (electricitySource) {
                DEFAULT -> "default"
                HOME_PHOTOVOLTAICS -> "homePhotovoltaics"
            }
        }

        fun ElectricitySource.getDisplayNameRes(): Int {
            return when (this) {
                DEFAULT -> R.string.electricity_source_default
                HOME_PHOTOVOLTAICS -> R.string.electricity_source_home_photovoltaics
            }
        }

        fun ElectricitySource.getDisplayName(context: Context): String {
            return context.getString(this.getDisplayNameRes())
        }

        fun getElectricitySourceList(): List<ElectricitySource> {
            return listOf(
                ElectricitySource.DEFAULT,
                ElectricitySource.HOME_PHOTOVOLTAICS,
            )
        }

    }
}