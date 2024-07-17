package eu.inscico.aurora_app.model.consumptions

import java.util.Calendar

sealed class Consumption {

    data class ElectricityConsumption(
        val id: String,
        val carbonEmissions: Double?,
        val category: ConsumptionType,
        val createdAt: Calendar?,
        val energyExpended: Double?,
        val updatedAt: Calendar?,
        val value: Double,
        val version: String?,
        val description: String?,
        val electricity: ElectricityConsumptionData,
        val generatedByRecurringConsumptionId: String? = null,
        val electricitySource: ElectricitySource = ElectricitySource.DEFAULT
    ) : Consumption()

    data class HeatingConsumption(
        val id: String,
        val carbonEmissions: Double?,
        val category: ConsumptionType,
        val createdAt: Calendar?,
        val energyExpended: Double?,
        val updatedAt: Calendar?,
        val value: Double,
        val description: String?,
        val version: String?,
        val heating: HeatingConsumptionData,
        val generatedByRecurringConsumptionId: String? = null
    ) : Consumption()

    data class TransportationConsumption(
        val id: String,
        val carbonEmissions: Double?,
        val category: ConsumptionType,
        val createdAt: Calendar?,
        val energyExpended: Double?,
        val updatedAt: Calendar?,
        val value: Double,
        val description: String?,
        val version: String?,
        val transportation: TransportationConsumptionData,
        val generatedByRecurringConsumptionId: String? = null
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

                    val electricitySource =
                        ElectricitySource.parseStringToElectricitySource(item.electricity?.electricitySource)

                    val electricity = ElectricityConsumptionData(
                        costs = item.electricity?.costs,
                        endDate = endDate,
                        startDate = startDate,
                        householdSize = item.electricity?.householdSize ?: return null,
                        electricitySource = electricitySource,
                        electricityExported = item.electricity?.electricityExported
                    )
                    ElectricityConsumption(
                        id = item.id ?: return null,
                        carbonEmissions = item.carbonEmissions,
                        category = category,
                        createdAt = createdAt,
                        energyExpended = item.energyExpended,
                        updatedAt = updatedAt,
                        description = item.description,
                        value = item.value ?: return null,
                        version = item.version,
                        electricity = electricity,
                        generatedByRecurringConsumptionId = item.generatedByRecurringConsumptionId,
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
                        id = item.id ?: return null,
                        carbonEmissions = item.carbonEmissions,
                        category = category,
                        createdAt = createdAt,
                        energyExpended = item.energyExpended,
                        updatedAt = updatedAt,
                        value = item.value ?: return null,
                        version = item.version,
                        description = item.description,
                        heating = heating,
                        generatedByRecurringConsumptionId = item.generatedByRecurringConsumptionId
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

                    val dateOfTravelEnd = if (item.transportation?.dateOfTravelEnd != null) {
                        Calendar.getInstance().apply {
                            time = item.transportation?.dateOfTravelEnd!!.toDate()
                        }
                    } else {
                        null
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
                                dateOfTravelEnd = dateOfTravelEnd,
                                privateVehicleOccupancy = item.transportation?.privateVehicleOccupancy ?: return null,
                                transportationType = transportationType,
                                publicVehicleOccupancy = null,
                                fuelConsumption = item.transportation?.fuelConsumption
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
                                dateOfTravelEnd = dateOfTravelEnd,
                                publicVehicleOccupancy = publicVehicleOccupancy ?: return null,
                                transportationType = transportationType,
                                privateVehicleOccupancy = null,
                                fuelConsumption = null
                            )
                        }
                        TransportationType.PLANE,
                        TransportationType.PLANE_INTRA_EU,
                        TransportationType.PLANE_EXTRA_EU,
                        TransportationType.ELECTRIC_BIKE,
                        TransportationType.ELECTRIC_SCOOTER,
                        TransportationType.BIKE,
                        TransportationType.WALKING -> {
                            TransportationConsumptionData(
                                dateOfTravel = dateOfTravel,
                                dateOfTravelEnd = dateOfTravelEnd,
                                publicVehicleOccupancy = null,
                                transportationType = transportationType,
                                privateVehicleOccupancy = null,
                                fuelConsumption = null
                            )
                        }
                        null -> return null
                    }


                    TransportationConsumption(
                        id = item.id ?: return null,
                        carbonEmissions = item.carbonEmissions,
                        category = category,
                        createdAt = createdAt,
                        energyExpended = item.energyExpended,
                        updatedAt = updatedAt,
                        value = item.value ?: return null,
                        version = item.version,
                        description = item.description,
                        transportation = transportation,
                        generatedByRecurringConsumptionId = item.generatedByRecurringConsumptionId
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
    val electricitySource: ElectricitySource,
    val electricityExported: Double?
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
    val dateOfTravelEnd: Calendar?,
    val privateVehicleOccupancy: Int?,
    val publicVehicleOccupancy: PublicVehicleOccupancy?,
    val transportationType: TransportationType,
    val fuelConsumption: Double?
)