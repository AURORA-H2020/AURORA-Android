package eu.inscico.aurora_app.model.country

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class CountryResponse(
    @DocumentId
    var id: String? = null,
    var countryCode: String? = null,
    var currencyCode: String? = null
)