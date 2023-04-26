package eu.inscico.aurora_app.ui.screens.settings.profile

import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.user.Gender
import eu.inscico.aurora_app.model.user.UserResponse
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.utils.TypedResult
import java.text.SimpleDateFormat
import java.util.*

class EditProfileViewModel(
    private val _countriesService: CountriesService,
    private val _userService: UserService
) : ViewModel() {

    val currentUserLive = _userService.userLive

    val currentCountry = _countriesService.userCountryLive
    val currentCity = _countriesService.userCityLive

    val genders = listOf(Gender.MALE, Gender.FEMALE, Gender.NON_BINARY, Gender.OTHER)

    val calendarYears = getCalendarYearsEntries()

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

    suspend fun updateUser(user: UserResponse): TypedResult<Boolean, String> {
        return _userService.updateUser(user)
    }
}