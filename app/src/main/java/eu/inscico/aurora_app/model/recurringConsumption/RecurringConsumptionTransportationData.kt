package eu.inscico.aurora_app.model.recurringConsumption

import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy
import eu.inscico.aurora_app.model.consumptions.TransportationConsumptionData
import eu.inscico.aurora_app.model.consumptions.TransportationType
import java.util.*

data class RecurringConsumptionTransportationData(
    val transportationType: TransportationType,
    val privateVehicleOccupancy: Int?,
    val publicVehicleOccupancy: PublicVehicleOccupancy?,
    val timeOfTravel: Calendar,
    val distance: Double
) {

    fun from(item: RecurringConsumptionTransportationDataResponse): RecurringConsumptionTransportationData? {

        val timeOfTravel = Calendar.getInstance()
        timeOfTravel.set(Calendar.HOUR_OF_DAY, item.hourOfTravel ?: return null)
        timeOfTravel.set(Calendar.MINUTE, item.minuteOfTravel ?: return null)
        timeOfTravel.set(Calendar.SECOND, 0)

        return RecurringConsumptionTransportationData(
            transportationType = TransportationType.parseStringToTransportationType(item.transportationType) ?: return null,
            privateVehicleOccupancy = item.privateVehicleOccupancy,
            publicVehicleOccupancy = PublicVehicleOccupancy.parseStringToPublicVehicleOccupancy(item.publicVehicleOccupancy),
            timeOfTravel = timeOfTravel,
            distance = item.distance ?: return null
        )
    }
}
