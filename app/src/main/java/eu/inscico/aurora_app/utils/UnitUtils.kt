package eu.inscico.aurora_app.utils

import android.content.res.Configuration
import android.icu.util.LocaleData
import android.icu.util.ULocale
import java.util.*

object UnitUtils {


    // region: Distance
    // ---------------------------------------------------------------------------------------------

    fun getSystemDistanceUnit(config: Configuration): String {
        return getDistanceUnit(locale = config.locales[0])
    }

    fun getDistanceUnit(locale: Locale): String {

        val measurementSystem = LocaleData.getMeasurementSystem(ULocale.forLocale(locale))
        return when (measurementSystem) {
            LocaleData.MeasurementSystem.SI -> "km"
            LocaleData.MeasurementSystem.UK -> "mi"
            LocaleData.MeasurementSystem.US -> "mi"
            else -> "km"
        }
    }

    fun getConvertedDistance(distanceInKm: Double?, locale: Locale, decimals: Int? = 0): Double {

        if (distanceInKm == null || distanceInKm == 0.0) {
            return 0.0
        }

        val measurementSystem = LocaleData.getMeasurementSystem(ULocale.forLocale(locale))
        return when (measurementSystem) {
            LocaleData.MeasurementSystem.UK,
            LocaleData.MeasurementSystem.US -> {
                val formattedDistance = if(decimals != null){
                    String.format("%.${decimals}f", distanceInKm * 0.62137).toDouble()
                } else {
                    distanceInKm * 0.62137
                }
                formattedDistance
            }
            else -> {
                val formattedDistance = if(decimals != null){
                    String.format("%.${decimals}f", distanceInKm).toDouble()
                } else {
                    distanceInKm
                }
                formattedDistance
            }
        }
    }

    fun getConvertedDistanceWithUnit(distanceInKm: Double?, locale: Locale, decimals: Int? = 0): String {
        return "${getConvertedDistance(distanceInKm, locale, decimals)} ${getDistanceUnit(locale)}"
    }

    fun getDistanceValueMetric(distance: Double, locale: Locale): Double {
        val measurementSystem = LocaleData.getMeasurementSystem(ULocale.forLocale(locale))
        return when (measurementSystem) {
            LocaleData.MeasurementSystem.UK,
            LocaleData.MeasurementSystem.US -> {
                distance * 1.609344
            }
            else -> distance
        }
    }

    // endregion


    // region: Weight
    // ---------------------------------------------------------------------------------------------

    fun getSystemWeightUnit(config: Configuration): String {
        return getWeightUnit(locale = config.locales[0])
    }

    fun getWeightUnit(locale: Locale): String {

        val measurementSystem = LocaleData.getMeasurementSystem(ULocale.forLocale(locale))
        return when (measurementSystem) {
            LocaleData.MeasurementSystem.SI -> "kg"
            LocaleData.MeasurementSystem.UK -> "lbs"
            LocaleData.MeasurementSystem.US -> "lbs"
            else -> "kg"
        }
    }

    fun getConvertedWeight(weightInKg: Double?, locale: Locale, decimals: Int? = null): String {

        if (weightInKg == null || weightInKg == 0.0) {
            return "0.0 ${getWeightUnit(locale)}"
        }

        val measurementSystem = LocaleData.getMeasurementSystem(ULocale.forLocale(locale))
        return when (measurementSystem) {
            LocaleData.MeasurementSystem.UK,
            LocaleData.MeasurementSystem.US -> {
                val formattedWeight = if(decimals != null){
                    String.format("%.${decimals}f", weightInKg * 2.2046).toDouble()
                } else {
                    weightInKg * 2.2046
                }
                "$formattedWeight ${getWeightUnit(locale)}"
            }
            else -> {
                val formattedWeight = if(decimals != null){
                    String.format("%.${decimals}f", weightInKg).toDouble()
                } else {
                    weightInKg
                }
                "$formattedWeight ${getWeightUnit(locale)}"
            }
        }
    }

    // endregion


// region: Currency
// ---------------------------------------------------------------------------------------------

    fun getSystemCurrencyUnit(config: Configuration): String {
        val currentLocale = config.locales[0]
        val currencySymbol = Currency.getInstance(currentLocale).symbol
        return currencySymbol
    }

    fun getCurrencyUnitByLocale(locale: String): String {
        val currency: Currency = Currency.getInstance(locale)
        return currency.symbol
    }

// endregion

}