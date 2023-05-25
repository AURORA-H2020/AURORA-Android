package eu.inscico.aurora_app.model.user

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class UserResponse(

    @DocumentId
    var id: String? = null,


    @get:PropertyName("isMarketingConsentAllowed")
    @set:PropertyName("isMarketingConsentAllowed")
    var isMarketingConsentAllowed: Boolean? = null,

    var city: String? = null,
    var country: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var gender: String? = null,
    var yearOfBirth: Int? = null
){
    companion object {
        fun from(item: User): UserResponse? {
            return UserResponse(
                id = item.id,
                city = item.city,
                country = item.country,
                firstName = item.firstName,
                lastName = item.lastName,
                gender = Gender.parseGenderToString(item.gender),
                yearOfBirth = item.yearOfBirth,
                isMarketingConsentAllowed = item.isMarketingConsentAllowed
            )
        }
    }
}

