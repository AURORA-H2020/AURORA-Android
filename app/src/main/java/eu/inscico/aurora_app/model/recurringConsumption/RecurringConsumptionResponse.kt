package eu.inscico.aurora_app.model.recurringConsumption

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.TransportationConsumptionDataResponse

data class RecurringConsumptionResponse(

    @DocumentId
    var id: String? = null,

    @ServerTimestamp
    var createdAt: Timestamp? = null,

    var category: String? = null,

    var frequency: RecurringConsumptionFrequencyResponse? = null,

    var transportation: RecurringConsumptionTransportationDataResponse? = null,

    var description: String? = null,

    @get:PropertyName("isEnabled")
    @set:PropertyName("isEnabled")
    var isEnabled: Boolean? = null
) {
    companion object {
        fun from(item: RecurringConsumption): RecurringConsumptionResponse {

            val weekdaysResponse = mutableListOf<Int>()
            item.frequency.weekdays?.forEach {
                val weekdayString =
                    RecurringConsumptionIntervalWeekday.parseIntervalWeekdayToInt(it)
                weekdayString?.let {
                    weekdaysResponse.add(it)
                }
            }

            val frequencyResponse = RecurringConsumptionFrequencyResponse(
                unit = RecurringConsumptionIntervalUnit.parseIntervalUnitToString(item.frequency.unit),
                weekdays = weekdaysResponse,
                dayOfMonth = item.frequency.dayOfMonth
            )

            val transportationResponse = RecurringConsumptionTransportationDataResponse(

            )

            return RecurringConsumptionResponse(
                id = item.id,
                createdAt = item.createdAt,
                category = ConsumptionType.parseConsumptionTypeToString(item.category),
                frequency = frequencyResponse,
                transportation = transportationResponse,
                description = item.description,
                isEnabled = item.isEnabled
            )
        }
    }
}
