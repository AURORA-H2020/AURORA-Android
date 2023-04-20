package eu.inscico.aurora_app.model

import eu.inscico.aurora_app.model.consumptions.ConsumptionResponse
import eu.inscico.aurora_app.model.user.UserResponse

data class ConsumptionBulkResponse(
    var consumptions: List<ConsumptionResponse>? = null,
    var consumptionSummary: ConsumptionSummeryResponse? = null,
    var user: UserResponse? = null
){
    companion object {
        fun from(item: UserDataDownloadBulk): ConsumptionBulkResponse? {
            val consumptions = item.consumptions?.map {
                ConsumptionResponse.from(it)
            }
            val consumptionSummery = ConsumptionSummeryResponse.from(item.consumptionSummary ?: return null)
            val user = UserResponse.from(item.user)

            return ConsumptionBulkResponse(
                consumptions = consumptions,
                consumptionSummary = consumptionSummery,
                user = user
            )
        }
    }
}
