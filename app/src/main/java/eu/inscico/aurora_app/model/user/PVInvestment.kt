package eu.inscico.aurora_app.model.user

import eu.inscico.aurora_app.model.user.Gender.Companion.parseStringToGender
import java.util.Calendar

data class PVInvestment(
    val id: String,
    val city: String,
    val investmentCapacity: Double?,
    val investmentDate: Calendar,
    val investmentPrice: Double?,
    val note: String?,
    val pvPlant: String,
    val share: Double,
    val createdAt: Calendar?,
    val updatedAt: Calendar?
){
    companion object {
        fun from(item: PVInvestmentResponse?): PVInvestment? {

            val investmentDate = if (item?.investmentDate != null) {
                Calendar.getInstance().apply {
                    time = item.investmentDate.toDate()
                }
            } else {
                return null
            }

            val createdDate = if (item.createdAt != null) {
                Calendar.getInstance().apply {
                    time = item.createdAt.toDate()
                }
            } else {
                null
            }

            val updatedDate = if (item.updatedAt != null) {
                Calendar.getInstance().apply {
                    time = item.updatedAt.toDate()
                }
            } else {
                null
            }

            return PVInvestment(
                id = item.id ?: return null,
                city = item.city ?: return null,
                investmentCapacity = item.investmentCapacity,
                investmentDate = investmentDate,
                investmentPrice = item.investmentPrice,
                note = item.note,
                pvPlant = item.pvPlant ?: return null,
                share = item.share ?: return null,
                createdAt = createdDate,
                updatedAt = updatedDate
            )
        }
    }
}