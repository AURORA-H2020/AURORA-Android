package eu.inscico.aurora_app.model

import eu.inscico.aurora_app.model.consumptionSummery.ConsumptionSummery
import eu.inscico.aurora_app.model.consumptionSummery.ConsumptionSummeryEntry
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.user.Gender
import eu.inscico.aurora_app.model.user.User
import eu.inscico.aurora_app.model.user.UserResponse

data class ConsumptionSummeryResponse(
    var totalCarbonEmissions: Double? = null,
    var entries: List<ConsumptionSummeryEntryResponse>? = null
) {
    companion object {
        fun from(item: ConsumptionSummery): ConsumptionSummeryResponse? {
            val entries = item.entries?.map {
                ConsumptionSummeryEntryResponse.from(it) ?: return null
            }
                return ConsumptionSummeryResponse(
                totalCarbonEmissions = item.totalCarbonEmissions,
                entries = entries
            )
        }
    }
}

data class ConsumptionSummeryEntryResponse(
    var absoluteValue: Int? = null,
    var category: String? = null,
    var value: Double? = null
) {
    companion object {
        fun from(item: ConsumptionSummeryEntry): ConsumptionSummeryEntryResponse? {
            return ConsumptionSummeryEntryResponse(
                absoluteValue = item.absoluteValue,
                category = ConsumptionType.parseConsumptionTypeToString(item.category),
                value = item.value
            )
        }
    }
}
