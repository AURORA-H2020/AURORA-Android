package eu.inscico.aurora_app.services.shared

import android.content.Context
import android.content.res.Configuration
import android.icu.util.LocaleData
import android.icu.util.ULocale
import android.os.Build
import eu.inscico.aurora_app.model.user.RegionEnum
import eu.inscico.aurora_app.utils.PrefsUtils
import java.util.*

class UnitService(
    private val context: Context
) {

    var appRegion: RegionEnum
        get() {
            val regionString = PrefsUtils.get(
                context,
                "auroraAppRegion",
                RegionEnum.parseRegionToString(RegionEnum.SYSTEM)
            )
            return RegionEnum.parseStringToRegion(regionString)
        }
        set(value) {
            val regionString = RegionEnum.parseRegionToString(value)
            PrefsUtils.save(context, "electricityReminderActive", regionString)
        }


    // region: Distance
    // ---------------------------------------------------------------------------------------------

    fun getDistanceUnit(config: Configuration): String {

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))

                    when (measurementSystem) {
                        LocaleData.MeasurementSystem.SI -> "km"
                        LocaleData.MeasurementSystem.UK -> "mi"
                        LocaleData.MeasurementSystem.US -> "mi"
                        else -> "km"
                    }
                } else {
                    "km"
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> "km"
            RegionEnum.UNITED_KINGDOM -> "mi"
            RegionEnum.USA -> "mi"
        }
    }

    fun getConvertedDistance(
        config: Configuration,
        distanceInKm: Double?,
        decimals: Int? = 0
    ): Double {

        if (distanceInKm == null || distanceInKm == 0.0) {
            return 0.0
        }

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))
                    return when (measurementSystem) {
                        LocaleData.MeasurementSystem.UK,
                        LocaleData.MeasurementSystem.US -> {
                            val formattedDistance = if (decimals != null) {
                                String.format("%.${decimals}f", distanceInKm * 0.62137)
                                    .replace(",", ".")
                                    .toDouble()
                            } else {
                                distanceInKm * 0.62137
                            }
                            formattedDistance
                        }
                        else -> {
                            val formattedDistance = if (decimals != null) {
                                String.format("%.${decimals}f", distanceInKm).replace(",", ".")
                                    .toDouble()
                            } else {
                                distanceInKm
                            }
                            formattedDistance
                        }
                    }
                } else {
                    val formattedDistance = if (decimals != null) {
                        String.format("%.${decimals}f", distanceInKm).replace(",", ".")
                            .toDouble()
                    } else {
                        distanceInKm
                    }
                    formattedDistance
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.SPAIN,
            RegionEnum.DENMARK -> {
                val formattedDistance = if (decimals != null) {
                    String.format("%.${decimals}f", distanceInKm).replace(",", ".")
                        .toDouble()
                } else {
                    distanceInKm
                }
                formattedDistance
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                val formattedDistance = if (decimals != null) {
                    String.format("%.${decimals}f", distanceInKm * 0.62137)
                        .replace(",", ".")
                        .toDouble()
                } else {
                    distanceInKm * 0.62137
                }
                formattedDistance
            }
        }
    }

    fun getValueInCorrectNumberFormat(config: Configuration, value: Double): String {
        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))

                    when (measurementSystem) {
                        LocaleData.MeasurementSystem.UK,
                        LocaleData.MeasurementSystem.US -> value.toString().replace(",", ".")
                        else -> value.toString().replace(".", ",")
                    }
                } else {
                    value.toString().replace(",", ".")
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.PORTUGAL,
            RegionEnum.SPAIN,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> {
                value.toString().replace(".", ",")
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                value.toString().replace(",", ".")
            }
        }
    }

    fun getConvertedDistanceWithUnit(
        config: Configuration,
        distanceInKm: Double?,
        decimals: Int? = 0
    ): String {
        val convertedDistance = getConvertedDistance(config, distanceInKm, decimals)
        val convertedDistanceFormatted = getValueInCorrectNumberFormat(config, convertedDistance)
        return "$convertedDistanceFormatted ${getDistanceUnit(config)}"
    }

    fun getCalculatedDistanceValueForUnit(config: Configuration, distance: Double): Double {

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))
                    return when (measurementSystem) {
                        LocaleData.MeasurementSystem.UK,
                        LocaleData.MeasurementSystem.US -> {
                            distance * 1.609344
                        }
                        else -> distance
                    }
                } else {
                    distance
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> {
                distance
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                distance * 1.609344
            }
        }
    }

    // endregion


    // region: Weight
    // ---------------------------------------------------------------------------------------------


    fun getWeightUnit(config: Configuration): String {

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))
                    return when (measurementSystem) {
                        LocaleData.MeasurementSystem.UK,
                        LocaleData.MeasurementSystem.US -> "lbs"
                        else -> "kg"
                    }
                } else {
                    "kg"
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> {
                "kg"
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> "lbs"
        }
    }

    fun getConvertedWeight(
        config: Configuration,
        weightInKg: Double?,
        decimals: Int? = null
    ): Double {

        if (weightInKg == null || weightInKg == 0.0) {
            return 0.0
        }

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))
                    return when (measurementSystem) {
                        LocaleData.MeasurementSystem.UK,
                        LocaleData.MeasurementSystem.US -> {
                            val formattedWeight = if (decimals != null) {
                                String.format("%.${decimals}f", weightInKg * 2.2046)
                                    .replace(",", ".")
                                    .toDouble()
                            } else {
                                weightInKg * 2.2046
                            }
                            formattedWeight
                        }
                        else -> {
                            val formattedWeight = if (decimals != null) {
                                String.format("%.${decimals}f", weightInKg).replace(",", ".")
                                    .toDouble()
                            } else {
                                weightInKg
                            }
                            formattedWeight
                        }
                    }
                } else {
                    val formattedWeight = if (decimals != null) {
                        String.format("%.${decimals}f", weightInKg).replace(",", ".").toDouble()
                    } else {
                        weightInKg
                    }
                    formattedWeight
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> {
                val formattedWeight = if (decimals != null) {
                    String.format("%.${decimals}f", weightInKg).replace(",", ".").toDouble()
                } else {
                    weightInKg
                }
                formattedWeight
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                val formattedWeight = if (decimals != null) {
                    String.format("%.${decimals}f", weightInKg * 2.2046)
                        .replace(",", ".")
                        .toDouble()
                } else {
                    weightInKg * 2.2046
                }
                formattedWeight

            }
        }
    }

    fun getConvertedWeightWithUnit(
        config: Configuration,
        weightInKg: Double?,
        decimals: Int? = 0
    ): String {
        val convertedWeight = getConvertedWeight(config, weightInKg, decimals)
        val convertedWeightFormatted = getValueInCorrectNumberFormat(config, convertedWeight)
        return "$convertedWeightFormatted ${getWeightUnit(config)}"
    }

    // endregion


// region: Currency
// ---------------------------------------------------------------------------------------------

    fun getCurrencyUnit(config: Configuration): String {

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                val currentLocale = config.locales[0]
                val currencySymbol = Currency.getInstance(currentLocale).symbol
                currencySymbol
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA -> "€"
            RegionEnum.DENMARK -> "DKK"
            RegionEnum.UNITED_KINGDOM -> "£"
            RegionEnum.USA -> "$"

        }
    }

    fun getCurrencyUnitByLocale(locale: String): String {
        val currency: Currency = Currency.getInstance(locale)
        return currency.symbol
    }

    // endregion

    // region: Time
    // ---------------------------------------------------------------------------------------------

    fun getDateFormat(config: Configuration, withTime: Boolean = false): String {

        val dateFormat = when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))

                    when (measurementSystem) {
                        LocaleData.MeasurementSystem.US -> "MM/dd/yyyy"
                        LocaleData.MeasurementSystem.UK -> "dd/MM/yyyy"
                        else -> "dd.MM.yyyy"
                    }
                } else {
                    "dd.MM.yyyy"
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.PORTUGAL,
            RegionEnum.SPAIN,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> "dd.MM.yyyy"
            RegionEnum.UNITED_KINGDOM -> "dd/MM/yyyy"
            RegionEnum.USA -> "MM/dd/yyyy"
        }

        return if(withTime){
            when (appRegion) {
                RegionEnum.SYSTEM -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val measurementSystem =
                            LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))

                        when (measurementSystem) {
                            LocaleData.MeasurementSystem.US -> "$dateFormat h:mm a"
                            else -> "$dateFormat HH:mm"
                        }
                    } else {
                        "$dateFormat HH:mm"
                    }
                }
                RegionEnum.GERMANY,
                RegionEnum.PORTUGAL,
                RegionEnum.SPAIN,
                RegionEnum.SLOVENIA,
                RegionEnum.DENMARK,
                RegionEnum.UNITED_KINGDOM -> "$dateFormat HH:mm"
                RegionEnum.USA -> "$dateFormat h:mm a"
            }
        } else {
            dateFormat
        }
    }

    fun getTimeFormat(config: Configuration): String {
        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))

                    when (measurementSystem) {
                        LocaleData.MeasurementSystem.US -> "h:mm a"
                        else -> "HH:mm"
                    }
                } else {
                    "HH:mm"
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.PORTUGAL,
            RegionEnum.SPAIN,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK,
            RegionEnum.UNITED_KINGDOM -> "HH:mm"
            RegionEnum.USA -> "h:mm a"
        }
    }

    fun parseToUTC(){
    }

    fun parseToLocalTime(){

    }

    // endregion

}