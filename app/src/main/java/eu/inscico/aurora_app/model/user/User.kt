package eu.inscico.aurora_app.model.user

import eu.inscico.aurora_app.model.user.Gender.Companion.parseStringToGender

data class User(

    val id: String,
    val city: String?,
    val country: String,
    val firstName: String,
    val lastName: String,
    val gender: Gender?,
    val yearOfBirth: Int?,
    val isMarketingConsentAllowed: Boolean?
) {
    companion object {
        fun from(item: UserResponse?): User? {
            return User(
                id = item?.id ?: return null,
                city = item.city,
                country = item.country ?: return null,
                firstName = item.firstName ?: return null,
                lastName = item.lastName ?: return null,
                gender = parseStringToGender(item.gender),
                yearOfBirth = item.yearOfBirth,
                isMarketingConsentAllowed = item.isMarketingConsentAllowed
            )
        }
    }
}
