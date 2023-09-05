package eu.inscico.aurora_app.ui.screens.settings.profile

import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.user.Gender
import eu.inscico.aurora_app.model.user.HomeEnergyLabel
import eu.inscico.aurora_app.model.user.HouseholdProfileEnum
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
    val homeEnergyLabels = listOf(HomeEnergyLabel.A_PLUS, HomeEnergyLabel.A, HomeEnergyLabel.B, HomeEnergyLabel.C, HomeEnergyLabel.D, HomeEnergyLabel.E, HomeEnergyLabel.F, HomeEnergyLabel.G, HomeEnergyLabel.UNSURE, null)
    val householdProfiles = listOf(HouseholdProfileEnum.RETIRED_INDIVIDUALS, HouseholdProfileEnum.HOME_BASED_WORKERS_STUDENTS, HouseholdProfileEnum.HOMEMAKERS, HouseholdProfileEnum.WORKERS_STUDENTS_OUTSIDE_THE_HOME, null)

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