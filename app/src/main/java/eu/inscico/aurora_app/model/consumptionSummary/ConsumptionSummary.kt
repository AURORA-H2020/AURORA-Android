package eu.inscico.aurora_app.model.consumptionSummary

import eu.inscico.aurora_app.model.consumptions.ConsumptionType

data class ConsumptionSummary(
    val id: String,
    val year: Int,
    val carbonEmission: LabeledConsumption,
    val energyExpended: LabeledConsumption,
    val months: List<ConsumptionSummaryMonth>,
    val categories: List<ConsumptionSummaryCategory>
) {
    companion object {
        fun from(item: ConsumptionSummaryResponse?): ConsumptionSummary? {

            val categories = item?.categories?.map {
                ConsumptionSummaryCategory.from(it) ?: return null
            }

            val months = item?.months?.map {
                ConsumptionSummaryMonth.from(it) ?: return null
            }

            return ConsumptionSummary(
                id = item?.id ?: return null,
                year = item.year ?: return null,
                carbonEmission = LabeledConsumption.from(item.carbonEmission ?: return null) ?: return null,
                energyExpended = LabeledConsumption.from(item.energyExpended ?: return null) ?: return null,
                months = months ?: return null,
                categories = categories ?: return null
            )
        }
    }
}

data class ConsumptionSummaryMonth(
    val number: Int,
    val carbonEmission: LabeledConsumption,
    val energyExpended: LabeledConsumption,
    val categories: List<ConsumptionSummaryCategory>
) {
    companion object {
        fun from(item: ConsumptionSummaryMonthResponse?): ConsumptionSummaryMonth? {

            val categories = item?.categories?.map {
                ConsumptionSummaryCategory.from(it) ?: return null
            }

            return ConsumptionSummaryMonth(
                carbonEmission = LabeledConsumption.from(item?.carbonEmission ?: return null) ?: return null,
                energyExpended = LabeledConsumption.from(item.energyExpended ?: return null) ?: return null,
                number = item.number ?: return null,
                categories = categories ?: return null
            )
        }
    }
}

data class ConsumptionSummaryCategory(
    val carbonEmission: LabeledConsumption,
    val energyExpended: LabeledConsumption,
    val category: ConsumptionType,
    val consumptionDays: Any? = null
) {
    companion object {
        fun from(item: ConsumptionSummaryCategoryResponse?): ConsumptionSummaryCategory? {
            return ConsumptionSummaryCategory(
                carbonEmission = LabeledConsumption.from(item?.carbonEmission ?: return null) ?: return null,
                energyExpended = LabeledConsumption.from(item.energyExpended ?: return null) ?: return null,
                category = ConsumptionType.parseStringToConsumptionType(item.category ?: return null) ?: return null,
                consumptionDays = item.consumptionDays
            )
        }
    }
}

data class LabeledConsumption(
    val total: Double,
    val label: EnergyLabel?,
    val percentage: Double?
) {
    companion object {
        fun from(item: LabeledConsumptionResponse?): LabeledConsumption? {
            return LabeledConsumption(
                total = item?.total ?: return null,
                label = EnergyLabel.parseStringToEnergyLabel(item.label),
                percentage = item.percentage
            )
        }
    }
}

/*
data class ConsumptionSummary(
    val totalCarbonEmissions: Double?,
    val entries: List<ConsumptionSummaryEntry>?
) {
    companion object {
        fun from(item: ConsumptionSummaryResponse?): ConsumptionSummary? {

            val entries = item?.entries?.map {
                ConsumptionSummaryEntry.from(it) ?: return null
            }

            return ConsumptionSummary(
                totalCarbonEmissions = item?.totalCarbonEmissions,
                entries = entries)
        }
    }
}

data class ConsumptionSummaryEntry(
    val absoluteValue: Int,
    val category: ConsumptionType,
    val value: Double
){
    companion object {
        fun from(item: ConsumptionSummaryEntryResponse): ConsumptionSummaryEntry? {
            return ConsumptionSummaryEntry(
                absoluteValue = item.absoluteValue ?: return null,
                category = ConsumptionType.parseStringToConsumptionType(item.category) ?: return null,
                value = item.value ?: return null,
                )
        }
    }
}

 */
