package eu.inscico.aurora_app.model.pvPlant

import java.util.Calendar

data class PVPlant(
    var id: String,

    val plantId: String,
    val name: String,
    val installationDate: Calendar?,
    val country: String,
    val city: String,
    val manufacturer: String?,
    val technology: String?,
    val capacity: Double?,
    val pricePerShare: Double?,
    val kwPerShare: Double?,
    val active: Boolean,
    val infoURL: String?,
){
    companion object {
        fun from(item: PVPlantResponse?): PVPlant? {

            val installationDate = if (item?.installationDate != null) {
                Calendar.getInstance().apply {
                    time = item.installationDate.toDate()
                }
            } else {
                return null
            }

            return PVPlant(
                id = item.id ?: return null,
                plantId = item.plantId ?: return null,
                name = item.name ?: return null,
                installationDate = installationDate,
                country = item.country ?: return null,
                city = item.city ?: return null,
                manufacturer = item.manufacturer,
                technology = item.technology,
                capacity = item.capacity,
                pricePerShare = item.pricePerShare,
                kwPerShare = item.kwPerShare,
                active = item.active ?: return null,
                infoURL = item.infoURL
            )
        }
    }
}