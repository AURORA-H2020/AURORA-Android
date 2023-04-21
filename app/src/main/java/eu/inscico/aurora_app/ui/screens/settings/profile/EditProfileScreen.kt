package eu.inscico.aurora_app.ui.screens.settings.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.user.Gender
import eu.inscico.aurora_app.model.user.Gender.Companion.toGenderString
import eu.inscico.aurora_app.model.user.UserResponse
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.FormEntry
import eu.inscico.aurora_app.ui.components.FormEntryType
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get()
) {

    val currentUserLive = viewModel.currentUserLive.observeAsState()

    val firstName = remember {
        mutableStateOf(currentUserLive.value?.firstName ?: "")
    }

    val lastName = remember {
        mutableStateOf(currentUserLive.value?.lastName ?: "")
    }

    val birthYear = remember {
        mutableStateOf(currentUserLive.value?.yearOfBirth.toString())
    }

    val gender = remember {
        mutableStateOf(currentUserLive.value?.gender ?: Gender.OTHER)
    }

    fun validateSaveAllowed(): Boolean {
        return (firstName.value != currentUserLive.value?.firstName
                || lastName.value != currentUserLive.value?.lastName
                || birthYear.value != currentUserLive.value?.yearOfBirth.toString()
                || gender.value != currentUserLive.value?.gender)
    }

    val isSaveValid = remember {
        mutableStateOf(validateSaveAllowed())
    }


    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val context = LocalContext.current

        AppBar(
            title = stringResource(id = R.string.settings_profile_edit_profile_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabSettings?.popBackStack()
            }
        )

        ScrollableContent(
            background = MaterialTheme.colorScheme.background
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
            ) {

                FormEntry(
                    title = stringResource(id = R.string.create_profile_first_name_hint),
                    formEntryType = FormEntryType.TEXT_INPUT,
                    initialItem = firstName.value,
                    callback = { name, _ ->
                        firstName.value = name
                        isSaveValid.value = validateSaveAllowed()
                    }
                )

                Divider()

                FormEntry(
                    title = stringResource(id = R.string.create_profile_last_name_hint),
                    formEntryType = FormEntryType.TEXT_INPUT,
                    initialItem = lastName.value,
                    callback = { name, _ ->
                        lastName.value = name
                        isSaveValid.value = validateSaveAllowed()
                    }
                )

                Divider()

                FormEntry(
                    title = stringResource(id = R.string.create_profile_year_of_birth_hint),
                    formEntryType = FormEntryType.SPINNER,
                    initialItem = birthYear.value,
                    items = viewModel.calendarYears,
                    callback = { name, _ ->
                        birthYear.value = name
                        isSaveValid.value = validateSaveAllowed()
                    }
                )

                Divider()

                FormEntry(
                    title = stringResource(id = R.string.create_profile_gender_hint),
                    formEntryType = FormEntryType.SPINNER,
                    initialItem = gender.value.toGenderString(context),
                    items = Gender.getGenderDisplayList(context),
                    callback = { _, index ->
                        gender.value = viewModel.genders.get(index ?: viewModel.genders.lastIndex)
                        isSaveValid.value = validateSaveAllowed()
                    }
                )

                Divider()

                FormEntry(
                    title = stringResource(id = R.string.create_profile_country_title),
                    formEntryType = FormEntryType.SPINNER,
                    initialItem = viewModel.currentCountry.value?.displayName ?: "",
                    readOnly = true
                )

                if (viewModel.currentCity.value != null) {
                    Divider()
                    FormEntry(
                        title = stringResource(id = R.string.create_profile_city_hint),
                        formEntryType = FormEntryType.SPINNER,
                        initialItem = viewModel.currentCity.value?.name ?: "",
                        readOnly = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val buttonColor = if(isSaveValid.value){
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                Button(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth(),
                    enabled = isSaveValid.value,
                    shape = RoundedCornerShape(32.dp),
                    onClick = {
                        val updatedUser = UserResponse(
                            city = currentUserLive.value?.city,
                            country = currentUserLive.value?.country,
                            firstName = firstName.value,
                            lastName = lastName.value,
                            gender = Gender.parseGenderToString(gender.value),
                            yearOfBirth = birthYear.value.toIntOrNull()
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = viewModel.updateUser(updatedUser)
                            when (result) {
                                is TypedResult.Failure -> {
                                    userFeedbackService.showSnackbar(R.string.settings_update_profile_fail_message)
                                }
                                is TypedResult.Success -> {
                                    withContext(Dispatchers.Main) {
                                        navigationService.navControllerTabSettings?.popBackStack()
                                    }
                                }
                            }
                        }
                    }) {
                    Text(
                        text = stringResource(id = R.string.settings_edit_profile_submit_button_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}