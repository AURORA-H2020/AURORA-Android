package eu.inscico.aurora_app.model.consumptions

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType.Companion.getDisplayName

enum class DistrictHeatingSource {
    @Json(name = "coal")
    COAL,
    @Json(name = "naturalGas")
    NATURAL_GAS,
    @Json(name = "oil")
    OIL,
    @Json(name = "electric")
    ELECTRIC,
    @Json(name = "solarThermal")
    SOLAR_THERMAL,
    @Json(name = "geothermal")
    GEO_THERMAL,
    @Json(name = "biomass")
    BIOMASS,
    @Json(name = "wasteTreatment")
    WASTE_TREATMENT,
    @Json(name = "default")
    DEFAULT;

    companion object {

        fun parseStringToDistrictHeatingSource(string: String?): DistrictHeatingSource? {
            return when (string) {
                "coal" -> COAL
                "naturalGas" -> NATURAL_GAS
                "oil" -> OIL
                "electric" -> ELECTRIC
                "solarThermal" -> SOLAR_THERMAL
                "geothermal" -> GEO_THERMAL
                "biomass" -> BIOMASS
                "wasteTreatment" -> WASTE_TREATMENT
                "default" -> DEFAULT
                else -> null
            }
        }

        fun DistrictHeatingSource.parseDistrictHeatingSourceToString(): String? {
            return when (this) {
                COAL -> "coal"
                NATURAL_GAS -> "naturalGas"
                OIL -> "oil"
                ELECTRIC -> "electric"
                SOLAR_THERMAL -> "solarThermal"
                GEO_THERMAL -> "geothermal"
                BIOMASS -> "biomass"
                WASTE_TREATMENT -> "wasteTreatment"
                DEFAULT -> "default"
                else -> null
            }
        }

        fun DistrictHeatingSource.getDisplayNameRes(): Int {
            return when (this) {
                COAL -> R.string.district_heating_source_type_coal_title
                NATURAL_GAS -> R.string.district_heating_source_type_natural_gas_title
                OIL -> R.string.district_heating_source_type_oil_title
                ELECTRIC -> R.string.district_heating_source_type_electricity_title
                SOLAR_THERMAL -> R.string.district_heating_source_type_solar_thermal_title
                GEO_THERMAL -> R.string.district_heating_source_type_geo_thermal_title
                BIOMASS -> R.string.district_heating_source_type_biomass_title
                WASTE_TREATMENT -> R.string.district_heating_source_type_waste_treatment_title
                DEFAULT -> R.string.district_heating_source_type_default_title
            }
        }

        fun DistrictHeatingSource.getDisplayName(context: Context): String {
            return context.getString(this.getDisplayNameRes())
        }

        fun getDistrictHeatingSourceList(): List<DistrictHeatingSource> {
            return listOf(
                NATURAL_GAS,
                OIL,
                ELECTRIC,
                SOLAR_THERMAL,
                GEO_THERMAL,
                WASTE_TREATMENT,
                DEFAULT
            )
        }

        fun getDistrictHeatingSourceStringList(context: Context): List<String>{
            return DistrictHeatingSource.getDistrictHeatingSourceList().map {
                it.getDisplayName(context)
            }
        }
    }
}