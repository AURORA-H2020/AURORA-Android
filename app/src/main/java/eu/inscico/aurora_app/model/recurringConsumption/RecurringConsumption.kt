package eu.inscico.aurora_app.model.recurringConsumption

import com.google.firebase.Timestamp
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy
import eu.inscico.aurora_app.model.consumptions.TransportationType
import java.util.*

data class RecurringConsumption(

    val id: String,
    val createdAt: Timestamp,
    val category: ConsumptionType,
    val frequency: RecurringConsumptionFrequency,
    val transportation: RecurringConsumptionTransportationData,
    val description: String?,
    val isEnabled: Boolean
) {

    companion object {

        fun from(item: RecurringConsumptionResponse?): RecurringConsumption? {

            if(item == null) return null

            val weekdays = mutableListOf<RecurringConsumptionIntervalWeekday>()
            item.frequency?.weekdays?.forEach {
                val weekday =
                    RecurringConsumptionIntervalWeekday.parseIntToIntervalWeekday(it)
                weekday?.let {
                    weekdays.add(it)
                }
            }

            val frequency = RecurringConsumptionFrequency(
                unit = RecurringConsumptionIntervalUnit.parseStringToIntervalUnit(item.frequency?.unit)
                    ?: return null,
                weekdays = weekdays.ifEmpty { null },
                dayOfMonth = item.frequency?.dayOfMonth
            )

            val hour = item.transportation?.hourOfTravel
            val minute = item.transportation?.minuteOfTravel
            val timeOfTravel = if (hour != null && minute != null) {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }
            } else {
                return null
            }

            val transportation = RecurringConsumptionTransportationData(
                fuelConsumption = item.transportation?.fuelConsumption,
                timeOfTravel = timeOfTravel,
                privateVehicleOccupancy = item.transportation?.privateVehicleOccupancy,
                publicVehicleOccupancy = PublicVehicleOccupancy.parseStringToPublicVehicleOccupancy(
                    item.transportation?.publicVehicleOccupancy
                ),
                transportationType = TransportationType.parseStringToTransportationType(item.transportation?.transportationType)
                    ?: return null,
                distance = item.transportation?.distance ?: return null
            )

            return RecurringConsumption(
                id = item.id ?: return null,
                createdAt = item.createdAt ?: return null,
                category = ConsumptionType.parseStringToConsumptionType(item.category)
                    ?: return null,
                frequency = frequency,
                transportation = transportation,
                description = item.description,
                isEnabled = item.isEnabled ?: return null
            )
        }
    }
}
