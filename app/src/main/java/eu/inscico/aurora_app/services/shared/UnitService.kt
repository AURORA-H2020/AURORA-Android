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


    // region: Number Formatting
    // ---------------------------------------------------------------------------------------------

    fun getValueWithLocalDecimalPoint(value: String?): String {
        return if(Locale.getDefault() == Locale.US || Locale.getDefault() == Locale.UK){
            value.toString().replace(",",".")
        } else {
            value.toString()
        }
    }

    fun getValueStringAsDouble(valueString: String?): Double? {
        return valueString?.replace(",",".")?.toDoubleOrNull()
    }


    // endregion


    // region: Distance
    // ---------------------------------------------------------------------------------------------

    fun getUserPreferredDistanceUnit(config: Configuration): String {

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

    fun getDistanceInUsersPreferredUnit(
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

    fun getValueInUserPreferredNumberFormat(config: Configuration, value: Double): String {
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
        val convertedDistance = getDistanceInUsersPreferredUnit(config, distanceInKm, decimals)
        val convertedDistanceFormatted = getValueInUserPreferredNumberFormat(config, convertedDistance)
        return "$convertedDistanceFormatted ${getUserPreferredDistanceUnit(config)}"
    }

    fun getDistanceInKm(config: Configuration, distance: Double): Double {

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

    fun getUserPreferredWeightUnit(config: Configuration): String {

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

    fun getWeightInUserPreferredUnit(
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
        val convertedWeight = getWeightInUserPreferredUnit(config, weightInKg, decimals)
        val convertedWeightFormatted = getValueInUserPreferredNumberFormat(config, convertedWeight)
        return "$convertedWeightFormatted ${getUserPreferredWeightUnit(config)}"
    }

    fun getWeightInKg(config: Configuration, distance: Double): Double {

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))
                    return when (measurementSystem) {
                        LocaleData.MeasurementSystem.UK,
                        LocaleData.MeasurementSystem.US -> {
                            distance * 0.45359237
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
                distance * 0.45359237
            }
        }
    }

    // endregion

    // region: Liters per 100 Km / Gallons per 100 miles
    // ---------------------------------------------------------------------------------------------

    fun getUserPreferredVolumePerDistanceUnit(config: Configuration): String {

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))
                    return when (measurementSystem) {
                        LocaleData.MeasurementSystem.UK,
                        LocaleData.MeasurementSystem.US -> "mpg"
                        else -> "L/100km"
                    }
                } else {
                    "L/100km"
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> {
                "L/100km"
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> "mpg"
        }
    }

    // convert fuelConsumption to UK gallons (1L = ~4.54609 gal) -> gallonsPerLiter
    // convert 100km to miles (1km = ~0.62137 mi) -> milesPer100km
    // divide milesPer100km by gallonsPerLiter
    fun getGallonsPer100Miles(
        config: Configuration,
        volumeInLiter: Double?,
        decimals: Int? = null
    ): Double {

        val gallonsPerLiter = getVolumeInUserPreferredUnit(config, volumeInLiter, decimals)
        val milesPer100Km = getDistanceInUsersPreferredUnit(config, 100.0, decimals)
        return milesPer100Km / gallonsPerLiter
    }

    fun getLiterPer100Km(
        config: Configuration,
        gallons: Double?,
        decimals: Int? = null
    ): Double {
        val literPerGallon = getVolumeInLiter(config, gallons)
        return 100.0 / literPerGallon
    }

    fun getVolumePerDistanceInLiterPer100Km(
        config: Configuration,
        valueInUsersUnit: Double?,
        decimals: Int? = 0
    ): Double {

        if (valueInUsersUnit == null || valueInUsersUnit == 0.0) {
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
                            val formattedValue = if (decimals != null) {
                                String.format("%.${decimals}f", 282.481053 / valueInUsersUnit)
                                    .replace(",", ".")
                                    .toDouble()
                            } else {
                                282.481053 / valueInUsersUnit
                            }
                            formattedValue
                        }
                        else -> {
                            val formattedValue = if (decimals != null) {
                                String.format("%.${decimals}f", valueInUsersUnit).replace(",", ".")
                                    .toDouble()
                            } else {
                                valueInUsersUnit
                            }
                            formattedValue
                        }
                    }
                } else {
                    val formattedValue = if (decimals != null) {
                        String.format("%.${decimals}f", valueInUsersUnit).replace(",", ".")
                            .toDouble()
                    } else {
                        valueInUsersUnit
                    }
                    formattedValue
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.SPAIN,
            RegionEnum.DENMARK -> {
                val formattedValue = if (decimals != null) {
                    String.format("%.${decimals}f", valueInUsersUnit).replace(",", ".")
                        .toDouble()
                } else {
                    valueInUsersUnit
                }
                formattedValue
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                val formattedValue = if (decimals != null) {
                    String.format("%.${decimals}f", 282.481053 / valueInUsersUnit)
                        .replace(",", ".")
                        .toDouble()
                } else {
                    282.481053 / valueInUsersUnit
                }
                formattedValue
            }
        }
    }

    fun getVolumePerDistanceInUserPreferredUnit(
        config: Configuration,
        literPer100km: Double?,
        decimals: Int? = 0
    ): Double {

        if (literPer100km == null || literPer100km == 0.0) {
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
                            val formattedValue = if (decimals != null) {
                                String.format("%.${decimals}f", 282.481 / literPer100km)
                                    .replace(",", ".")
                                    .toDouble()
                            } else {
                                getGallonsPer100Miles(config, literPer100km,decimals)
                            }
                            formattedValue
                        }
                        else -> {
                            val formattedValue = if (decimals != null) {
                                String.format("%.${decimals}f", literPer100km).replace(",", ".")
                                    .toDouble()
                            } else {
                                literPer100km
                            }
                            formattedValue
                        }
                    }
                } else {
                    val formattedValue = if (decimals != null) {
                        String.format("%.${decimals}f", literPer100km).replace(",", ".")
                            .toDouble()
                    } else {
                        literPer100km
                    }
                    formattedValue
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.SPAIN,
            RegionEnum.DENMARK -> {
                val formattedValue = if (decimals != null) {
                    String.format("%.${decimals}f", literPer100km).replace(",", ".")
                        .toDouble()
                } else {
                    literPer100km
                }
                formattedValue
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                val formattedValue = if (decimals != null) {
                    String.format("%.${decimals}f", 282.481 / literPer100km)
                        .replace(",", ".")
                        .toDouble()
                } else {
                    282.481 / literPer100km
                }
                formattedValue
            }
        }
    }

    fun getConvertedVolumePerDistanceWithUnit(
        config: Configuration,
        volumeInLiter: Double?,
        decimals: Int? = 0
    ): String {
        val convertedVolumePerDistance = getVolumePerDistanceInUserPreferredUnit(config, volumeInLiter, decimals)
        val convertedVolumePerDistanceFormatted = getValueInUserPreferredNumberFormat(config, convertedVolumePerDistance)
        return "$convertedVolumePerDistanceFormatted ${getUserPreferredVolumePerDistanceUnit(config)}"
    }
    // endregion

    // region: kWh/100km / kWh/mi
    // ---------------------------------------------------------------------------------------------

    fun getUserPreferredKWhPerDistanceUnit(config: Configuration): String {

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))
                    return when (measurementSystem) {
                        LocaleData.MeasurementSystem.UK,
                        LocaleData.MeasurementSystem.US -> "kWh/mi"
                        else -> "kWh/100km"
                    }
                } else {
                    "kWh/100km"
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> {
                "kWh/100km"
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> "kWh/mi"
        }
    }

    // convert 100km to miles (1km = ~0.62137 mi) -> milesPer100km
    // divide milesPer100km by fuelConsumption

    fun getKWhPerKmInKWhPerMile(
        config: Configuration,
        kWh: Double?,
        decimals: Int? = null
    ): Double {

        if (kWh == null || kWh == 0.0) {
            return 0.0
        }

        val milesPer100Km = getDistanceInUsersPreferredUnit(config, 100.0, 5)
        return milesPer100Km / kWh
    }

    fun getKWhPerMileInKWhPerKm(
        config: Configuration,
        kWh: Double?,
        decimals: Int? = null
    ): Double {

        if (kWh == null || kWh == 0.0) {
            return 0.0
        }

        val milesInKm = getDistanceInKm(config, 100.0)
        return milesInKm / kWh
    }

    fun getKWhPerDistanceInUserPreferredUnit(
        config: Configuration,
        kWhPer100Km: Double?,
        decimals: Int? = 0
    ): Double {

        if (kWhPer100Km == null || kWhPer100Km == 0.0) {
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
                            val formattedValue = if (decimals != null) {
                                String.format("%.${decimals}f", getKWhPerKmInKWhPerMile(config, kWhPer100Km,decimals))
                                    .replace(",", ".")
                                    .toDouble()
                            } else {
                                getKWhPerKmInKWhPerMile(config, kWhPer100Km,decimals)
                            }
                            formattedValue
                        }
                        else -> {
                            val formattedValue = if (decimals != null) {
                                String.format("%.${decimals}f", kWhPer100Km).replace(",", ".")
                                    .toDouble()
                            } else {
                                kWhPer100Km
                            }
                            formattedValue
                        }
                    }
                } else {
                    val formattedValue = if (decimals != null) {
                        String.format("%.${decimals}f", kWhPer100Km).replace(",", ".")
                            .toDouble()
                    } else {
                        kWhPer100Km
                    }
                    formattedValue
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.SPAIN,
            RegionEnum.DENMARK -> {
                val formattedDistance = if (decimals != null) {
                    String.format("%.${decimals}f", kWhPer100Km).replace(",", ".")
                        .toDouble()
                } else {
                    kWhPer100Km
                }
                formattedDistance
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                val formattedDistance = if (decimals != null) {
                    String.format("%.${decimals}f", getKWhPerKmInKWhPerMile(config, kWhPer100Km,decimals))
                        .replace(",", ".")
                        .toDouble()
                } else {
                    getKWhPerKmInKWhPerMile(config, kWhPer100Km, decimals)
                }
                formattedDistance
            }
        }
    }

    fun getKWhPerDistanceInKWhPer100Km(
        config: Configuration,
        kWhInUserUnit: Double?,
        decimals: Int? = 0
    ): Double {

        if (kWhInUserUnit == null || kWhInUserUnit == 0.0) {
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
                            val formattedValue = if (decimals != null) {
                                String.format("%.${decimals}f", getKWhPerMileInKWhPerKm(config, kWhInUserUnit,decimals))
                                    .replace(",", ".")
                                    .toDouble()
                            } else {
                                getKWhPerMileInKWhPerKm(config, kWhInUserUnit, decimals)
                            }
                            formattedValue
                        }
                        else -> {
                            val formattedValue = if (decimals != null) {
                                String.format("%.${decimals}f", kWhInUserUnit).replace(",", ".")
                                    .toDouble()
                            } else {
                                kWhInUserUnit
                            }
                            formattedValue
                        }
                    }
                } else {
                    val formattedValue = if (decimals != null) {
                        String.format("%.${decimals}f", kWhInUserUnit).replace(",", ".")
                            .toDouble()
                    } else {
                        kWhInUserUnit
                    }
                    formattedValue
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.SPAIN,
            RegionEnum.DENMARK -> {
                val formattedDistance = if (decimals != null) {
                    String.format("%.${decimals}f", kWhInUserUnit).replace(",", ".")
                        .toDouble()
                } else {
                    kWhInUserUnit
                }
                formattedDistance
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                val formattedDistance = if (decimals != null) {
                    String.format("%.${decimals}f", getKWhPerMileInKWhPerKm(config, kWhInUserUnit, decimals))
                        .replace(",", ".")
                        .toDouble()
                } else {
                    getKWhPerMileInKWhPerKm(config, kWhInUserUnit, decimals)
                }
                formattedDistance
            }
        }
    }

    fun getConvertedKWhPerDistanceWithUnit(
        config: Configuration,
        kWhPerDistance: Double?,
        decimals: Int? = 0
    ): String {
        val convertedKWhPerDistance = getKWhPerDistanceInUserPreferredUnit(config, kWhPerDistance, decimals)
        val convertedKWhPerDistanceFormatted = getValueInUserPreferredNumberFormat(config, convertedKWhPerDistance)
        return "$convertedKWhPerDistanceFormatted ${getUserPreferredKWhPerDistanceUnit(config)}"
    }

    // endregion

    // region: Volume
    // ---------------------------------------------------------------------------------------------

    fun getUserPreferredVolumeUnit(config: Configuration): String {

        return when (appRegion) {
            RegionEnum.SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val measurementSystem =
                        LocaleData.getMeasurementSystem(ULocale.forLocale(config.locales[0]))
                    return when (measurementSystem) {
                        LocaleData.MeasurementSystem.UK,
                        LocaleData.MeasurementSystem.US -> "gal"
                        else -> "l"
                    }
                } else {
                    "l"
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> {
                "l"
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> "gal"
        }
    }

    fun getVolumeInUserPreferredUnit(
        config: Configuration,
        volumeInLiter: Double?,
        decimals: Int? = null
    ): Double {

        if (volumeInLiter == null || volumeInLiter == 0.0) {
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
                            val formattedVolume = if (decimals != null) {
                                String.format("%.${decimals}f", volumeInLiter * 0.2199692483)
                                    .replace(",", ".")
                                    .toDouble()
                            } else {
                                volumeInLiter * 0.2199692483
                            }
                            formattedVolume
                        }
                        else -> {
                            val formattedVolume = if (decimals != null) {
                                String.format("%.${decimals}f", volumeInLiter).replace(",", ".")
                                    .toDouble()
                            } else {
                                volumeInLiter
                            }
                            formattedVolume
                        }
                    }
                } else {
                    val formattedVolume = if (decimals != null) {
                        String.format("%.${decimals}f", volumeInLiter).replace(",", ".").toDouble()
                    } else {
                        volumeInLiter
                    }
                    formattedVolume
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> {
                val formattedVolume = if (decimals != null) {
                    String.format("%.${decimals}f", volumeInLiter).replace(",", ".").toDouble()
                } else {
                    volumeInLiter
                }
                formattedVolume
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                val formattedVolume = if (decimals != null) {
                    String.format("%.${decimals}f", volumeInLiter * 0.2199692483)
                        .replace(",", ".")
                        .toDouble()
                } else {
                    volumeInLiter * 0.2199692483
                }
                formattedVolume
            }
        }
    }

    fun getUserPreferredVolumeWithUnit(
        config: Configuration,
        volumeInLiter: Double?,
        decimals: Int? = 0
    ): String {
        val convertedVolume = getVolumeInUserPreferredUnit(config, volumeInLiter, decimals)
        val convertedVolumeFormatted = getValueInUserPreferredNumberFormat(config, convertedVolume)
        return "$convertedVolumeFormatted ${getUserPreferredVolumeUnit(config)}"
    }

    fun getVolumeInLiter(config: Configuration, volume: Double?): Double {

        if (volume == null || volume == 0.0) {
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
                            volume / 0.21997
                        }
                        else -> volume
                    }
                } else {
                    volume
                }
            }
            RegionEnum.GERMANY,
            RegionEnum.SPAIN,
            RegionEnum.PORTUGAL,
            RegionEnum.SLOVENIA,
            RegionEnum.DENMARK -> {
                volume
            }
            RegionEnum.UNITED_KINGDOM,
            RegionEnum.USA -> {
                volume / 0.21997
            }
        }
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