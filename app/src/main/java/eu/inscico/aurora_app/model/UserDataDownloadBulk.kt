package eu.inscico.aurora_app.model

import eu.inscico.aurora_app.model.consumptionSummery.ConsumptionSummery
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.user.User

data class UserDataDownloadBulk(
    val consumptions: List<Consumption>?,
    val consumptionSummary: ConsumptionSummery?,
    val user: User
){
    companion object {
        fun from(item: ConsumptionBulkResponse): UserDataDownloadBulk? {
            val consumptions = item.consumptions?.map {
                Consumption.from(it) ?: return null
            }

            val consumptionSummery = ConsumptionSummery.from(item.consumptionSummary ?: return null)

            val user = User.from(item.user ?: return null)

            return UserDataDownloadBulk(
                consumptions = consumptions,
                consumptionSummary = consumptionSummery,
                user = user ?: return null
            )
        }
    }
}
