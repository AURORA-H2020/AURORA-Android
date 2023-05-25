package eu.inscico.aurora_app.ui.screens.login.createProfile

import android.app.Activity
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.City
import eu.inscico.aurora_app.model.user.Gender
import eu.inscico.aurora_app.model.user.Gender.Companion.toGenderString
import eu.inscico.aurora_app.model.user.UserResponse
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.FormEntry
import eu.inscico.aurora_app.ui.components.FormEntryType
import eu.inscico.aurora_app.ui.components.SwitchWithLabel
import eu.inscico.aurora_app.ui.theme.primary
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    viewModel: CreateProfileViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get()
) {

    val countries = viewModel.countries.observeAsState()

    val firstName = remember {
        mutableStateOf(viewModel.getFirstNameFromPreSelectedAccount() ?: "")
    }

    val lastName = remember {
        mutableStateOf(viewModel.getLastNameFromPreSelectedAccount() ?: "")
    }

    val birthYear = remember {
        mutableStateOf(viewModel.calendarYears.first())
    }

    val gender = remember {
        mutableStateOf(Gender.OTHER)
    }

    val country = remember {
        mutableStateOf(countries.value?.first())
    }

    val city = remember {
        mutableStateOf(viewModel.cities.value?.first())
    }

    val newsletterSwitch = remember {
        mutableStateOf(false)
    }


    val context = LocalContext.current

    Column(
        modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        AppBar(
            title = stringResource(id = R.string.profile_title),
            hasBackNavigation = false,
            actionButton = {
                Row(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            viewModel.userLogout(context as Activity)
                            navigationService.navControllerAuth?.popBackStack()
                        }
                ){
                    Image(
                        painter = painterResource(id = R.drawable.outline_logout_24),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 7.dp),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.heightIn(16.dp))

            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.outline_account_circle_24),
                contentDescription = "",
                colorFilter = ColorFilter.tint(primary)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(id = R.string.create_profile_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.heightIn(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(id = R.string.create_profile_description),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(16.dp))) {

                FormEntry(
                    title = stringResource(id = R.string.create_profile_first_name_hint),
                    formEntryType = FormEntryType.TEXT_INPUT,
                    initialItem = firstName.value,
                    callback = { name, _ ->
                        firstName.value = name
                    }
                )

                Divider()

                FormEntry(
                    title = stringResource(id = R.string.create_profile_last_name_hint),
                    formEntryType = FormEntryType.TEXT_INPUT,
                    initialItem = lastName.value,
                    readOnly = false,
                    callback = { name, _ ->
                        lastName.value = name
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
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(id = R.string.create_profile_country_title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }

            val countryDisplayNames = countries.value?.map { it.displayName ?: "" }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(shape = RoundedCornerShape(16.dp))) {

                FormEntry(
                    title = stringResource(id = R.string.create_profile_country_title),
                    formEntryType = FormEntryType.SPINNER,
                    initialItem = country.value?.displayName ?: countryDisplayNames?.first() ?: "",
                    items = countryDisplayNames,
                    callback = { _, index ->
                        if (index != null) {
                            val selectedCountry = countries.value?.elementAt(index)
                            country.value = selectedCountry

                            city.value = null

                            // load cities for selected country
                            val countryId = selectedCountry?.id
                            if (countryId != null) {
                                viewModel.loadCitiesForSelectedCountry(selectedCountry.id)
                            }
                        }
                    }
                )

                val citiesFromCountry = viewModel.cities.observeAsState()
                val citiesFromCountryNames =
                    citiesFromCountry.value?.map { it.name }?.toMutableList()
                citiesFromCountryNames?.add(stringResource(id = R.string.create_profile_city_drop_down_other_city))
                if (citiesFromCountry.value?.isNotEmpty() == true) {
                    Divider()

                    FormEntry(
                        title = stringResource(id = R.string.create_profile_city_hint),
                        formEntryType = FormEntryType.SPINNER,
                        initialItem = city.value?.name ?: "",
                        items = citiesFromCountryNames,
                        callback = { name, index ->

                            if (name == context.getString(R.string.create_profile_city_drop_down_other_city)) {
                                city.value = null
                            } else if (index != null) {
                                val selectedCity = citiesFromCountry.value?.elementAt(index)
                                city.value = selectedCity
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(id = R.string.create_profile_country_selection_info),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.heightIn(16.dp))

            SwitchWithLabel(
                label = stringResource(id = R.string.create_profile_newsletter_switch_description),
                state = newsletterSwitch.value,
                onStateChange = {
                newsletterSwitch.value = it
            })

            Spacer(modifier = Modifier.heightIn(32.dp))

            Button(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                onClick = {
                    val countryId = country.value?.id
                    if(countryId != null){
                        val user = UserResponse(
                            city = city.value?.id,
                            country = countryId,
                            firstName = firstName.value,
                            lastName = lastName.value,
                            gender = Gender.parseGenderToString(gender.value),
                            yearOfBirth = birthYear.value.toIntOrNull(),
                            isMarketingConsentAllowed = newsletterSwitch.value
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                        val result = viewModel.createUser(user)
                            when(result){
                                is TypedResult.Failure -> {
                                    userFeedbackService.showSnackbar(R.string.login_create_profile_fail_message)
                                }
                                is TypedResult.Success -> {
                                    withContext(Dispatchers.Main){
                                        navigationService.navControllerAuth?.popBackStack(route = NavGraphDirections.Auth.getNavRoute(), inclusive = false )
                                    }
                                }
                            }
                        }
                    }
                }) {
                Text(
                    text = stringResource(id = R.string.profile_submit),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }

}