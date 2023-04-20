package eu.inscico.aurora_app.model.country

data class Country(
    val id: String,
    val countryCode: String,
    val currencyCode: String
) {

    var displayName: String? = null

    companion object {
        fun from(item: CountryResponse): Country? {
            return Country(
                id = item.id ?: return null,
                countryCode = item.countryCode ?: return null,
                currencyCode = item.currencyCode ?: return null
            )
        }
    }
}