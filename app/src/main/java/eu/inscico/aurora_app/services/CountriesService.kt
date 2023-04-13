package eu.inscico.aurora_app.services

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firestore.v1.StructuredAggregationQuery.Aggregation.Count
import eu.inscico.aurora_app.model.City
import eu.inscico.aurora_app.model.CityResponse
import eu.inscico.aurora_app.model.Country
import eu.inscico.aurora_app.model.CountryResponse
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class CountriesService(
    private val _firestore: FirebaseFirestore
) {

    private val countriesCollectionName = "countries"
    private val citiesCollectionName = "cities"

    private val _countriesLive = MutableLiveData<List<Country>>()
    val countriesLive: LiveData<List<Country>> = _countriesLive

    private val _citiesFromCountryLive = MutableLiveData<List<City>?>()
    val citiesFromCountryLive: LiveData<List<City>?> = _citiesFromCountryLive

    val userCountryLive = MutableLiveData<Country?>()
    val userCityLive = MutableLiveData<City?>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadCountries()
        }
    }

    fun getCountriesDisplayList(): List<String> {
        val countryNames = mutableListOf<String>()
        countriesLive.value?.forEach {
            countryNames.add(getCountryNameForCode(it.countryCode))
        }
        return countryNames
    }

    fun getCountryNameForCode(countryCode: String): String {
        val loc = Locale("", countryCode)
        return loc.displayCountry
    }

    suspend fun loadCountries(): TypedResult<List<Country>, Any> {
        try {

            // Get countries
            val countriesSnapshot = _firestore.collection(countriesCollectionName).get().await()
            val countries = countriesSnapshot.mapNotNull {
                try {
                    val countryResponse = it.toObject<CountryResponse>() ?: return@mapNotNull null
                    val country = Country.from(countryResponse)

                    val countryCode = country?.countryCode
                    if (countryCode != null) {
                        country.displayName = getCountryNameForCode(countryCode)
                    }

                    country

                } catch (e: Exception) {
                    val x = e
                    null
                }
            }

            // Update countries
            _countriesLive.postValue(countries)

            return TypedResult.Success(countries)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    suspend fun getUserCountryById(countryId: String): TypedResult<Country, Any> {
        try {
            // Get countries
            val userCountrySnapshot =
                _firestore.collection(countriesCollectionName).document(countryId).get().await()
            userCountrySnapshot?.let {
                try {
                    val countryResponse =
                        it.toObject<CountryResponse>() ?: return TypedResult.Failure("")
                    val country = Country.from(countryResponse) ?: return TypedResult.Failure("")
                    val countryCode = country.countryCode
                    country.displayName = getCountryNameForCode(countryCode)
                    userCountryLive.postValue(country)
                    return TypedResult.Success(country)
                } catch (e: Exception) {
                    return TypedResult.Failure("")
                }
            }
            return TypedResult.Failure("")
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    suspend fun loadCitiesForCountry(countryId: String): TypedResult<List<City>, Any> {
        _citiesFromCountryLive.postValue(null)
        try {
            // Get countries
            val citiesSnapshot = _firestore.collection(countriesCollectionName).document(countryId)
                .collection(citiesCollectionName).get().await()
            val cities = citiesSnapshot.mapNotNull {
                try {
                    val cityResponse = it.toObject<CityResponse>() ?: return@mapNotNull null
                    City.from(cityResponse)
                } catch (e: Exception) {
                    val x = e
                    null
                }
            }

            // Update countries
            _citiesFromCountryLive.postValue(cities)

            return TypedResult.Success(cities)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    suspend fun getUserCityById(countryId: String, cityId: String): TypedResult<City, Any> {
        try {
            // Get countries
            val userCitySnapshot =
                _firestore.collection(countriesCollectionName).document(countryId)
                    .collection(citiesCollectionName).document(cityId).get().await()
            userCitySnapshot?.let {
                try {
                    val cityResponse =
                        it.toObject<CityResponse>() ?: return TypedResult.Failure("")
                    val city = City.from(cityResponse) ?: return TypedResult.Failure("")
                    userCityLive.postValue(city)
                    return TypedResult.Success(city)
                } catch (e: Exception) {
                    return TypedResult.Failure("")
                }
            }
            return TypedResult.Failure("")
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }
}