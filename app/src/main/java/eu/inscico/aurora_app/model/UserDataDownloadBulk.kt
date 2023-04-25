package eu.inscico.aurora_app.model

import eu.inscico.aurora_app.model.consumptionSummary.ConsumptionSummary
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.user.User

data class UserDataDownloadBulk(
    val consumptions: List<Consumption>?,
    val consumptionSummary: ConsumptionSummary?,
    val user: User
){
    companion object {
        fun from(item: ConsumptionBulkResponse): UserDataDownloadBulk? {
            val consumptions = item.consumptions?.map {
                Consumption.from(it) ?: return null
            }

            val consumptionSummary = ConsumptionSummary.from(item.consumptionSummary ?: return null)

            val user = User.from(item.user ?: return null)

            return UserDataDownloadBulk(
                consumptions = consumptions,
                consumptionSummary = consumptionSummary,
                user = user ?: return null
            )
        }
    }
}
