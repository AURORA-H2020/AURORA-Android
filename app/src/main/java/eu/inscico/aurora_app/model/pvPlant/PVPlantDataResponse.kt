package eu.inscico.aurora_app.model.pvPlant

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class PVPlantDataResponse(

    @DocumentId
    var id: String? = null,

    var Ep: Double? = null,
    var date: Timestamp? = null
) {
    companion object {
        fun from(item: PVPlantData): PVPlantDataResponse {
            return PVPlantDataResponse(
                id = item.id,
                Ep = item.Ep,
                date = Timestamp(item.date.time),
            )
        }
    }
}
