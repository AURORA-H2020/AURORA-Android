package eu.inscico.aurora_app.model.recurringConsumption

import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.parsePublicVehicleOccupancyToString
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.parseTransportationTypeToString
import java.util.Calendar

data class RecurringConsumptionTransportationDataResponse(
    var transportationType: String? = null,
    var privateVehicleOccupancy: Int? = null,
    var publicVehicleOccupancy: String? = null,
    var hourOfTravel: Int? = null,
    var minuteOfTravel: Int? = null,
    var distance: Double? = null
) {

    companion object {

        fun from(item: RecurringConsumptionTransportationData?): RecurringConsumptionTransportationDataResponse? {

            if(item == null) return null

            val hourOfTravel = item.timeOfTravel.get(Calendar.HOUR_OF_DAY)
            val minuteOfTravel = item.timeOfTravel.get(Calendar.MINUTE)

            return RecurringConsumptionTransportationDataResponse(
                transportationType = item.transportationType.parseTransportationTypeToString(),
                privateVehicleOccupancy = item.privateVehicleOccupancy,
                publicVehicleOccupancy = item.publicVehicleOccupancy?.parsePublicVehicleOccupancyToString(),
                hourOfTravel = hourOfTravel,
                minuteOfTravel = minuteOfTravel,
                distance = item.distance
            )
        }
    }
}
