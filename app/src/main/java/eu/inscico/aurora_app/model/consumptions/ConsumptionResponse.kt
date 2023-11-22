package eu.inscico.aurora_app.model.consumptions

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import eu.inscico.aurora_app.model.consumptions.DistrictHeatingSource.Companion.parseDistrictHeatingSourceToString
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType.Companion.parseHeatingFuelToString
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.parsePublicVehicleOccupancyToString
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.parseTransportationTypeToString

class ConsumptionResponse(

    @DocumentId
    var id: String? = null,

    var carbonEmissions: Double? = null,
    var category: String? = null,
    var description: String? = null,
    var createdAt: Timestamp? = null,
    var energyExpended: Double? = null,
    var updatedAt: Timestamp? = null,
    var value: Double? = null,
    var version: String? = null,

    var electricity: ElectricityConsumptionDataResponse? = null,
    var heating: HeatingConsumptionDataResponse? = null,
    var transportation: TransportationConsumptionDataResponse? = null,

    var generatedByRecurringConsumptionId: String? = null
) {

    companion object {
        fun from(item: Consumption): ConsumptionResponse {

            return when (item) {
                is Consumption.ElectricityConsumption -> {

                    val electricity = ElectricityConsumptionDataResponse(
                        costs = item.electricity.costs,
                        endDate = Timestamp(item.electricity.endDate.time),
                        startDate = Timestamp(item.electricity.startDate.time),
                        householdSize = item.electricity.householdSize,
                        electricitySource = ElectricitySource.parseElectricitySourceToString(item.electricitySource),
                    )
                    ConsumptionResponse(
                        id = item.id,
                        carbonEmissions = item.carbonEmissions,
                        category = ConsumptionType.parseConsumptionTypeToString(item.category),
                        createdAt = if (item.createdAt?.time != null) Timestamp(item.createdAt.time) else null,
                        energyExpended = item.energyExpended,
                        updatedAt = if (item.updatedAt?.time != null) Timestamp(item.updatedAt.time) else null,
                        value = item.value,
                        version = item.version,
                        description = item.description,
                        electricity = electricity,
                        heating = null,
                        transportation = null,
                        generatedByRecurringConsumptionId = item.generatedByRecurringConsumptionId
                    )
                }
                is Consumption.HeatingConsumption -> {
                    val heating = HeatingConsumptionDataResponse(
                        costs = item.heating.costs,
                        endDate = Timestamp(item.heating.endDate.time),
                        startDate = Timestamp(item.heating.startDate.time),
                        householdSize = item.heating.householdSize,
                        heatingFuel = item.heating.heatingFuel.parseHeatingFuelToString(),
                        districtHeatingSource = item.heating.districtHeatingSource?.parseDistrictHeatingSourceToString(),
                    )
                    ConsumptionResponse(
                        id = item.id,
                        carbonEmissions = item.carbonEmissions,
                        category = ConsumptionType.parseConsumptionTypeToString(item.category),
                        createdAt = if (item.createdAt?.time != null) Timestamp(item.createdAt.time) else null,
                        energyExpended = item.energyExpended,
                        updatedAt = if (item.updatedAt?.time != null) Timestamp(item.updatedAt.time) else null,
                        value = item.value,
                        version = item.version,
                        description = item.description,
                        electricity = null,
                        heating = heating,
                        transportation = null,
                        generatedByRecurringConsumptionId = item.generatedByRecurringConsumptionId
                    )

                }
                is Consumption.TransportationConsumption -> {
                    val dateOfTravelEnd = if(item.transportation.dateOfTravelEnd?.time != null){
                        Timestamp(item.transportation.dateOfTravelEnd.time)
                    } else {
                        null
                    }
                    val transportation = TransportationConsumptionDataResponse(
                        dateOfTravel = Timestamp(item.transportation.dateOfTravel.time),
                        dateOfTravelEnd = dateOfTravelEnd,
                        privateVehicleOccupancy = item.transportation.privateVehicleOccupancy,
                        publicVehicleOccupancy = item.transportation.publicVehicleOccupancy?.parsePublicVehicleOccupancyToString(),
                        transportationType = item.transportation.transportationType.parseTransportationTypeToString()
                    )
                    ConsumptionResponse(
                        id = item.id,
                        carbonEmissions = item.carbonEmissions,
                        category = ConsumptionType.parseConsumptionTypeToString(item.category),
                        createdAt = if (item.createdAt?.time != null) Timestamp(item.createdAt.time) else null,
                        energyExpended = item.energyExpended,
                        updatedAt = if (item.updatedAt?.time != null) Timestamp(item.updatedAt.time) else null,
                        value = item.value,
                        version = item.version,
                        description = item.description,
                        electricity = null,
                        heating = null,
                        transportation = transportation,
                        generatedByRecurringConsumptionId = item.generatedByRecurringConsumptionId
                    )
                }
            }
        }
    }
}

data class ElectricityConsumptionDataResponse(
    var costs: Double? = null,
    var endDate: Timestamp? = null,
    var startDate: Timestamp? = null,
    var householdSize: Int? = null,
    val electricitySource: String? = null
)

data class HeatingConsumptionDataResponse(
    var costs: Double? = null,
    var endDate: Timestamp? = null,
    var startDate: Timestamp? = null,
    var householdSize: Int? = null,
    var heatingFuel: String? = null,
    var districtHeatingSource: String? = null
)

data class TransportationConsumptionDataResponse(
    var dateOfTravel: Timestamp? = null,
    var dateOfTravelEnd: Timestamp? = null,
    var privateVehicleOccupancy: Int? = null,
    var publicVehicleOccupancy: String? = null,
    var transportationType: String? = null
)