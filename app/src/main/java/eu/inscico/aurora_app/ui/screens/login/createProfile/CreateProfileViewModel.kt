package eu.inscico.aurora_app.ui.screens.login.createProfile

import android.app.Activity
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.user.Gender
import eu.inscico.aurora_app.model.user.UserResponse
import eu.inscico.aurora_app.model.user.UserSignInType
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.utils.TypedResult
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

    suspend fun createUser(user: UserResponse): TypedResult<Boolean, String>{
            return _userService.createUser(user)
    }

    fun userLogout(activity: Activity){
        when(_authService.userSignInType){
            UserSignInType.GOOGLE -> {
                _authService.googleSignOut(activity)
            }
            UserSignInType.APPLE -> {
                _authService.logout()
            }
            UserSignInType.EMAIL -> {
                _authService.logout()
            }
            null -> _authService.logout()
        }
    }

}