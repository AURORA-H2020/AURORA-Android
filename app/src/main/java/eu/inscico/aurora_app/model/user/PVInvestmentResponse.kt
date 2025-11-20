package eu.inscico.aurora_app.model.user

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class PVInvestmentResponse(

    @DocumentId
    var id: String? = null,

    val city: String? = null,
    val createdAt: Timestamp? = null,
    val investmentCapacity: Double? = null,
    val investmentDate: Timestamp? = null,
    val investmentPrice: Double? = null,
    val note: String? = null,
    val pvPlant: String? = null,
    val share: Double? = null,
    val updatedAt: Timestamp? = null,
) {
    companion object {
        fun from(item: PVInvestment): PVInvestmentResponse {
            return PVInvestmentResponse(
                id = item.id,
                city = item.city,
                createdAt = if (item.createdAt?.time != null) Timestamp(item.createdAt.time) else null,
                investmentCapacity = item.investmentCapacity,
                investmentDate = Timestamp(item.investmentDate.time),
                investmentPrice = item.investmentPrice,
                note = item.note,
                pvPlant = item.pvPlant,
                share = item.share,
                updatedAt = if (item.updatedAt?.time != null) Timestamp(item.updatedAt.time) else null
                )
        }
    }
}
