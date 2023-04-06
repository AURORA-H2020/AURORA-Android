package eu.inscico.aurora_app.ui.screens.login

import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.Gender
import eu.inscico.aurora_app.model.User
import eu.inscico.aurora_app.model.UserResponse
import eu.inscico.aurora_app.services.CountriesService
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.services.auth.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateProfileViewModel(
    private val _countriesService: CountriesService,
    private val _authService: AuthService,
    private val _userService: UserService
): ViewModel() {


    val countries = _countriesService.countriesLive
    val cities = _countriesService.citiesFromCountryLive

    val genders = listOf(Gender.MALE, Gender.FEMALE, Gender.NON_BINARY, Gender.OTHER)

    val calendarYears = getCalendarYearsEntries()

    fun loadCitiesForSelectedCountry(countryId: String){
        CoroutineScope(Dispatchers.IO).launch {
            _countriesService.loadCitiesForCountry(countryId)
        }
    }

    fun getCalendarYearsEntries(): List<String> {

        val years = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy")

        for (index in 1900..Calendar.getInstance().get(Calendar.YEAR)) {
            calendar.set(Calendar.YEAR, index)
            years.add(
                sdf.format(calendar.time)
            )
        }

        return years.asReversed()
    }

    fun getFirstNameFromPreSelectedAccount(): String? {
        return _authService.getFirstNameFromGoogle()
    }

    fun getLastNameFromPreSelectedAccount(): String? {
        return _authService.getLastNameFromGoogle()
    }

    fun createUser(user: UserResponse){
        CoroutineScope(Dispatchers.IO).launch {
            _userService.createUser(user)
        }
    }

}