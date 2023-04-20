package eu.inscico.aurora_app.model

import eu.inscico.aurora_app.model.country.CityResponse

data class City(
    val id: String,
    val hasPhotovoltaics: Boolean?,
    val name: String,
    val pvgisParams: PvGisParams?
) {
    companion object {
        fun from(item: CityResponse): City? {

            val pvgisParamsResponse = item.pvgisParams
            val pvGisParams = if(pvgisParamsResponse != null) {
                PvGisParams.from(pvgisParamsResponse)
            } else {
                null
            }

            return City(
                id = item.id ?: return null,
                hasPhotovoltaics = item.hasPhotovoltaics,
                name = item.name ?: return null,
                pvgisParams = pvGisParams
            )
        }
    }
}
