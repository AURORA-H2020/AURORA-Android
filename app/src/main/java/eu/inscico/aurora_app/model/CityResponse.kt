package eu.inscico.aurora_app.model

import com.google.firebase.firestore.DocumentId

data class CityResponse(

    @DocumentId
    var id: String? = null,

    var hasPhotovoltaics: Boolean? = null,
    var name: String? = null,
    var pvgisParams: PvgisParamsResponse? = null
)
