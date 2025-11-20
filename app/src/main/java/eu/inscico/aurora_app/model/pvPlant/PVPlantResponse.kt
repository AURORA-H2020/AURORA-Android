package eu.inscico.aurora_app.model.pvPlant

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class PVPlantResponse(

    @DocumentId
    var id: String? = null,

    val active: Boolean? = null,
    val capacity: Double? = null,
    val city: String? = null,
    val country: String? = null,
    val infoURL: String? = null,
    val installationDate: Timestamp? = null,
    val kwPerShare: Double? = null,
    val manufacturer: String? = null,
    val name: String? = null,
    val plantId: String? = null,
    val pricePerShare: Double? = null,
    val technology: String? = null
){
    companion object {
        fun from(item: PVPlant): PVPlantResponse {
            return PVPlantResponse(
                id = item.id,
                active = item.active,
                capacity = item.capacity,
                city = item.city,
                country = item.country,
                infoURL = item.infoURL,
                installationDate = if (item.installationDate?.time != null) Timestamp(item.installationDate.time) else null,
                kwPerShare = item.kwPerShare,
                manufacturer = item.manufacturer,
                name = item.name ,
                plantId = item.plantId,
                pricePerShare = item.pricePerShare,
                technology = item.technology,
            )
        }
    }
}
