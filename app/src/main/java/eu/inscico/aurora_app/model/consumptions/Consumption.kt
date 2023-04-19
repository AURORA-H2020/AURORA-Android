package eu.inscico.aurora_app.model.consumptions

import eu.inscico.aurora_app.model.Gender
import eu.inscico.aurora_app.model.User
import eu.inscico.aurora_app.model.UserResponse
import java.util.Calendar

sealed class Consumption {

    data class ElectricityConsumption(
        val carbonEmissions: Double?,
        val category: ConsumptionType,
        val createdAt: Calendar?,
        val energyExpended: Double?,
        val updatedAt: Calendar?,
        val value: Double,
        val version: String?,
        val description: String?,
        val electricity: ElectricityConsumptionData
    ) : Consumption()

    data class HeatingConsumption(
        val carbonEmissions: Double?,
        val category: ConsumptionType,
        val createdAt: Calendar?,
        val energyExpended: Double?,
        val updatedAt: Calendar?,
        val value: Double,
        val description: String?,
        val version: String?,
        val heating: HeatingConsumptionData
    ) : Consumption()

    data class TransportationConsumption(
        val carbonEmissions: Double?,
        val category: ConsumptionType,
        val createdAt: Calendar?,
        val energyExpended: Double?,
        val updatedAt: Calendar?,
        val value: Double,
        val description: String?,
        val version: String?,
        val transportation: TransportationConsumptionData
    ) : Consumption()

    companion object {
        fun from(item: ConsumptionResponse): Consumption? {

            val category = ConsumptionType.parseStringToConsumptionType(item.category)

            return when (category) {
                ConsumptionType.ELECTRICITY -> {
                    val startDate = if (item.electricity?.startDate != null) {
                        Calendar.getInstance().apply {
                            time = item.electricity?.startDate!!.toDate()
                        }
                    } else {
                        return null
                    }

                    val endDate = if (item.electricity?.endDate != null) {
                        Calendar.getInstance().apply {
                            time = item.electricity?.endDate!!.toDate()
                        }
                    } else {
                        return null
                    }

                    val createdAt = if (item.createdAt != null) {
                        Calendar.getInstance().apply {
                            time = item.createdAt!!.toDate()
                        }
                    } else {
                        null
                    }

                    val updatedAt = if (item.updatedAt != null) {
                        Calendar.getInstance().apply {
                            time = item.updatedAt!!.toDate()
                        }
                    } else {
                        null
                    }
                    val electricity = ElectricityConsumptionData(
                        costs = item.electricity?.costs,
                        endDate = endDate,
                        startDate = startDate,
                        householdSize = item.electricity?.householdSize ?: return null
                    )
                    ElectricityConsumption(
                        carbonEmissions = item.carbonEmissions,
                        category = category,
                        createdAt = createdAt,
                        energyExpended = item.energyExpended,
                        updatedAt = updatedAt,
                        description = item.description,
                        value = item.value ?: return null,
                        version = item.version,
                        electricity = electricity
                    )
                }
                ConsumptionType.HEATING -> {
                    val startDate = if (item.heating?.startDate != null) {
                        Calendar.getInstance().apply {
                            time = item.heating?.startDate!!.toDate()
                        }
                    } else {
                        return null
                    }

                    val endDate = if (item.heating?.endDate != null) {
                        Calendar.getInstance().apply {
                            time = item.heating?.endDate!!.toDate()
                        }
                    } else {
                        return null
                    }

                    val createdAt = if (item.createdAt != null) {
                        Calendar.getInstance().apply {
                            time = item.createdAt!!.toDate()
                        }
                    } else {
                        null
                    }

                    val updatedAt = if (item.updatedAt != null) {
                        Calendar.getInstance().apply {
                            time = item.updatedAt!!.toDate()
                        }
                    } else {
                        null
                    }

                    val heatingFuel =
                        HeatingFuelType.parseStringToHeatingFuel(item.heating?.heatingFuel)
                    val districtHeatingSource =
                        DistrictHeatingSource.parseStringToDistrictHeatingSource(item.heating?.districtHeatingSource)

                    val heating = HeatingConsumptionData(
                        costs = item.heating?.costs,
                        endDate = endDate,
                        startDate = startDate,
                        householdSize = item.heating?.householdSize ?: return null,
                        heatingFuel = heatingFuel ?: return null,
                        districtHeatingSource = districtHeatingSource
                    )

                    HeatingConsumption(
                        carbonEmissions = item.carbonEmissions,
                        category = category,
                        createdAt = createdAt,
                        energyExpended = item.energyExpended,
                        updatedAt = updatedAt,
                        value = item.value ?: return null,
                        version = item.version,
                        description = item.description,
                        heating = heating
                    )
                }
                ConsumptionType.TRANSPORTATION -> {
                    val dateOfTravel = if (item.transportation?.dateOfTravel != null) {
                        Calendar.getInstance().apply {
                            time = item.transportation?.dateOfTravel!!.toDate()
                        }
                    } else {
                        return null
                    }

                    val createdAt = if (item.createdAt != null) {
                        Calendar.getInstance().apply {
                            time = item.createdAt!!.toDate()
                        }
                    } else {
                        null
                    }

                    val updatedAt = if (item.updatedAt != null) {
                        Calendar.getInstance().apply {
                            time = item.updatedAt!!.toDate()
                        }
                    } else {
                        null
                    }

                    val transportationType =
                        TransportationType.parseStringToTransportationType(item.transportation?.transportationType)
                    val publicVehicleOccupancy =
                        PublicVehicleOccupancy.parseStringToPublicVehicleOccupancy(item.transportation?.publicVehicleOccupancy)

                    val transportation = when(transportationType){
                        TransportationType.FUEL_CAR,
                        TransportationType.ELECTRIC_CAR,
                        TransportationType.HYBRID_CAR,
                        TransportationType.MOTORCYCLE,
                        TransportationType.ELECTRIC_MOTORCYCLE -> {
                            TransportationConsumptionData(
                                dateOfTravel = dateOfTravel,
                                privateVehicleOccupancy = item.transportation?.privateVehicleOccupancy ?: return null,
                                transportationType = transportationType,
                                publicVehicleOccupancy = null
                            )
                        }
                        TransportationType.ELECTRIC_BUS,
                        TransportationType.HYBRID_ELECTRIC_BUS,
                        TransportationType.ALTERNATIVE_FUEL_BUS,
                        TransportationType.DIESEL_BUS,
                        TransportationType.OTHER_BUS,
                        TransportationType.METRO_TRAM_OR_URBAN_LIGHT_TRAIN,
                        TransportationType.ELECTRIC_PASSENGER_TRAIN,
                        TransportationType.DIESEL_PASSENGER_TRAIN,
                        TransportationType.HIGH_SPEED_TRAIN -> {
                            TransportationConsumptionData(
                                dateOfTravel = dateOfTravel,
                                publicVehicleOccupancy = publicVehicleOccupancy ?: return null,
                                transportationType = transportationType,
                                privateVehicleOccupancy = null
                            )
                        }
                        TransportationType.PLANE,
                        TransportationType.ELECTRIC_BIKE,
                        TransportationType.ELECTRIC_SCOOTER,
                        TransportationType.BIKE,
                        TransportationType.WALKING -> {
                            TransportationConsumptionData(
                                dateOfTravel = dateOfTravel,
                                publicVehicleOccupancy = null,
                                transportationType = transportationType,
                                privateVehicleOccupancy = null
                            )
                        }
                        null -> return null
                    }


                    TransportationConsumption(
                        carbonEmissions = item.carbonEmissions,
                        category = category,
                        createdAt = createdAt,
                        energyExpended = item.energyExpended,
                        updatedAt = updatedAt,
                        value = item.value ?: return null,
                        version = item.version,
                        description = item.description,
                        transportation = transportation
                    )
                }
                null -> return null
            }
        }
    }

}

data class ElectricityConsumptionData(
    val costs: Double?,
    val endDate: Calendar,
    val startDate: Calendar,
    val householdSize: Int,
)

data class HeatingConsumptionData(
    val costs: Double?,
    val endDate: Calendar,
    val startDate: Calendar,
    val householdSize: Int,
    val heatingFuel: HeatingFuelType,
    val districtHeatingSource: DistrictHeatingSource?
)

data class TransportationConsumptionData(
    val dateOfTravel: Calendar,
    val privateVehicleOccupancy: Int?,
    val publicVehicleOccupancy: PublicVehicleOccupancy?,
    val transportationType: TransportationType
)