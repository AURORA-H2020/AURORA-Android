package eu.inscico.aurora_app.model.consumptionSummary

import com.google.firebase.firestore.DocumentId
import eu.inscico.aurora_app.model.consumptions.ConsumptionType

data class ConsumptionSummaryResponse(
    @DocumentId
    var id: String? = null,
    var year: Int? = null,
    var carbonEmission: LabeledConsumptionResponse? = null,
    var energyExpended: LabeledConsumptionResponse? = null,
    var months: List<ConsumptionSummaryMonthResponse>? = null,
    var categories: List<ConsumptionSummaryCategoryResponse>? = null
) {
    companion object {
        fun from(item: ConsumptionSummary): ConsumptionSummaryResponse {

            val categories = item.categories.map {
                ConsumptionSummaryCategoryResponse.from(it)
            }

            val months = item.months.map {
                ConsumptionSummaryMonthResponse.from(it)
            }
            return ConsumptionSummaryResponse(
                id = item.id,
                year = item.year,
                carbonEmission = LabeledConsumptionResponse.from(item.carbonEmission),
                energyExpended = LabeledConsumptionResponse.from(item.energyExpended),
                months = months,
                categories = categories
            )
        }
    }
}

data class ConsumptionSummaryMonthResponse(
    var number: Int? = null,
    var carbonEmission: LabeledConsumptionResponse? = null,
    var energyExpended: LabeledConsumptionResponse? = null,
    var categories: List<ConsumptionSummaryCategoryResponse>? = null
) {
    companion object {
        fun from(item: ConsumptionSummaryMonth): ConsumptionSummaryMonthResponse {

            val categories = item.categories.map {
                ConsumptionSummaryCategoryResponse.from(it)
            }

            return ConsumptionSummaryMonthResponse(
                carbonEmission = LabeledConsumptionResponse.from(item.carbonEmission),
                energyExpended = LabeledConsumptionResponse.from(item.energyExpended),
                number = item.number,
                categories = categories
            )
        }
    }
}

data class ConsumptionSummaryCategoryResponse(
    var carbonEmission: LabeledConsumptionResponse? = null,
    var energyExpended: LabeledConsumptionResponse? = null,
    var category: String? = null,
    var consumptionDays: Any? = null
) {
    companion object {
        fun from(item: ConsumptionSummaryCategory): ConsumptionSummaryCategoryResponse {
            return ConsumptionSummaryCategoryResponse(
                carbonEmission = LabeledConsumptionResponse.from(item.carbonEmission),
                energyExpended = LabeledConsumptionResponse.from(item.energyExpended),
                category = ConsumptionType.parseConsumptionTypeToString(item.category),
                consumptionDays = item.consumptionDays
            )
        }
    }
}

data class LabeledConsumptionResponse(
    var label: String? = null,
    var total: Double? = null,
    var percentage: Double? = null
){
    companion object {
        fun from(item: LabeledConsumption): LabeledConsumptionResponse? {
            return LabeledConsumptionResponse(
                label = EnergyLabel.parseEnergyLabelToString(item.label),
                total = item.total,
                percentage = item.percentage
            )
        }
    }
}

/*
data class ConsumptionSummaryResponse(
    var totalCarbonEmissions: Double? = null,
    var entries: List<ConsumptionSummaryEntryResponse>? = null
) {
    companion object {
        fun from(item: ConsumptionSummary?): ConsumptionSummaryResponse? {
            val entries = item?.entries?.map {
                ConsumptionSummaryEntryResponse.from(it) ?: return null
            }
                return ConsumptionSummaryResponse(
                totalCarbonEmissions = item?.totalCarbonEmissions,
                entries = entries
            )
        }
    }
}

data class ConsumptionSummaryEntryResponse(
    var absoluteValue: Int? = null,
    var category: String? = null,
    var value: Double? = null
) {
    companion object {
        fun from(item: ConsumptionSummaryEntry): ConsumptionSummaryEntryResponse? {
            return ConsumptionSummaryEntryResponse(
                absoluteValue = item.absoluteValue,
                category = ConsumptionType.parseConsumptionTypeToString(item.category),
                value = item.value
            )
        }
    }
}


 */