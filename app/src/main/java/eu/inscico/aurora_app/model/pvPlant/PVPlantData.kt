package eu.inscico.aurora_app.model.pvPlant

import java.util.Calendar

data class PVPlantData(
    val id: String,
    val Ep: Double,
    val date: Calendar
){
    companion object {
        fun from(item: PVPlantDataResponse): PVPlantData? {

            val date = if (item.date != null) {
                Calendar.getInstance().apply {
                    time = item.date!!.toDate()
                }
            } else {
                return null
            }

            return PVPlantData(
                id = item.id ?: return null,
                date = date,
                Ep = item.Ep ?: return null
            )
        }
    }
}
