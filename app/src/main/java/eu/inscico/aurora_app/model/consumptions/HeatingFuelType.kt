package eu.inscico.aurora_app.model.consumptions

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R

enum class HeatingFuelType {
    @Json(name = "oil")
    OIL,
    @Json(name = "naturalGas")
    NATURAL_GAS,
    @Json(name = "liquifiedPetroGas")
    LPG,
    @Json(name = "biomass")
    BIOMASS,
    @Json(name = "locallyProducedBiomass")
    LOCALLY_PRODUCED_BIOMASS,
    @Json(name = "geothermal")
    GEO_THERMAL,
    @Json(name = "solarThermal")
    SOLAR_THERMAL,
    @Json(name = "district")
    DISTRICT,
    @Json(name = "butane")
    BUTANE,
    @Json(name = "firewood")
    FIREWOOD,
    @Json(name = "electric")
    ELECTRIC;

    companion object {

        fun HeatingFuelType.getDisplayNameRes(): Int {
            return when(this){
                OIL -> R.string.heating_fuel_type_oil_title
                NATURAL_GAS -> R.string.heating_fuel_type_natural_gas_title
                LPG -> R.string.heating_fuel_type_lpg_title
                BIOMASS -> R.string.heating_fuel_type_biomass_title
                LOCALLY_PRODUCED_BIOMASS -> R.string.heating_fuel_type_locally_produced_biomass_title
                GEO_THERMAL -> R.string.heating_fuel_type_geo_thermal_title
                SOLAR_THERMAL -> R.string.heating_fuel_type_solar_thermal_title
                DISTRICT -> R.string.heating_fuel_type_district_title
                ELECTRIC -> R.string.heating_fuel_type_electric_title
                FIREWOOD -> R.string.heating_fuel_type_firewood_title
                BUTANE -> R.string.heating_fuel_type_butane_title
            }
        }

        fun HeatingFuelType.getDisplayName(context: Context): String {
            return context.getString(this.getDisplayNameRes())
        }

        fun parseStringToHeatingFuel(heatingFuelString: String?): HeatingFuelType? {
            return when (heatingFuelString) {
                "oil" -> OIL
                "naturalGas" -> NATURAL_GAS
                "liquifiedPetroGas" -> LPG
                "biomass" -> BIOMASS
                "locallyProducedBiomass" -> LOCALLY_PRODUCED_BIOMASS
                "geothermal" -> GEO_THERMAL
                "solarThermal" -> SOLAR_THERMAL
                "district" -> DISTRICT
                "electric" -> ELECTRIC
                "firewood" -> FIREWOOD
                "butane" -> BUTANE
                else -> null
            }
        }

        fun HeatingFuelType.parseHeatingFuelToString(): String {
            return when (this) {
                OIL -> "oil"
                NATURAL_GAS -> "naturalGas"
                LPG -> "liquifiedPetroGas"
                BIOMASS -> "biomass"
                LOCALLY_PRODUCED_BIOMASS -> "locallyProducedBiomass"
                GEO_THERMAL -> "geothermal"
                SOLAR_THERMAL -> "solarThermal"
                DISTRICT -> "district"
                ELECTRIC -> "electric"
                FIREWOOD -> "firewood"
                BUTANE -> "butane"
            }
        }

        fun getHeatingFuelList(): List<HeatingFuelType> {
            return listOf(
                OIL,
                NATURAL_GAS,
                LPG,
                BIOMASS,
                LOCALLY_PRODUCED_BIOMASS,
                GEO_THERMAL,
                SOLAR_THERMAL,
                DISTRICT,
                ELECTRIC,
                FIREWOOD,
                BUTANE
            )
        }

        fun getHeatingFuelResList(): List<Int>{
            return listOf(
                OIL.getDisplayNameRes(),
                NATURAL_GAS.getDisplayNameRes(),
                LPG.getDisplayNameRes(),
                BIOMASS.getDisplayNameRes(),
                LOCALLY_PRODUCED_BIOMASS.getDisplayNameRes(),
                GEO_THERMAL.getDisplayNameRes(),
                SOLAR_THERMAL.getDisplayNameRes(),
                DISTRICT.getDisplayNameRes(),
                ELECTRIC.getDisplayNameRes(),
                FIREWOOD.getDisplayNameRes(),
                BUTANE.getDisplayNameRes()
            )
        }

        fun getHeatingFuelStringList(context: Context): List<String>{
            return getHeatingFuelList().map {
                it.getDisplayName(context)
            }
        }
    }
}