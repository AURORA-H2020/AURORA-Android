package eu.inscico.aurora_app.model.consumptionSummery

import eu.inscico.aurora_app.model.ConsumptionSummeryEntryResponse
import eu.inscico.aurora_app.model.ConsumptionSummeryResponse
import eu.inscico.aurora_app.model.consumptions.ConsumptionType

data class ConsumptionSummery(
    val totalCarbonEmissions: Double?,
    val entries: List<ConsumptionSummeryEntry>?
) {
    companion object {
        fun from(item: ConsumptionSummeryResponse): ConsumptionSummery? {

            val entries = item.entries?.map {
                ConsumptionSummeryEntry.from(it) ?: return null
            }

            return ConsumptionSummery(
                totalCarbonEmissions = item.totalCarbonEmissions,
                entries = entries)
        }
    }
}

data class ConsumptionSummeryEntry(
    val absoluteValue: Int,
    val category: ConsumptionType,
    val value: Double
){
    companion object {
        fun from(item: ConsumptionSummeryEntryResponse): ConsumptionSummeryEntry? {
            return ConsumptionSummeryEntry(
                absoluteValue = item.absoluteValue ?: return null,
                category = ConsumptionType.parseStringToConsumptionType(item.category) ?: return null,
                value = item.value ?: return null,
                )
        }
    }
}
