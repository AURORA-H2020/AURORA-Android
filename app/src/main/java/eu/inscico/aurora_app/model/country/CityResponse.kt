package eu.inscico.aurora_app.model.country

import com.google.firebase.firestore.DocumentId
import eu.inscico.aurora_app.model.PvgisParamsResponse

data class CityResponse(

    @DocumentId
    var id: String? = null,

    var hasPhotovoltaics: Boolean? = null,
    var name: String? = null,
    var pvgisParams: PvgisParamsResponse? = null
)
