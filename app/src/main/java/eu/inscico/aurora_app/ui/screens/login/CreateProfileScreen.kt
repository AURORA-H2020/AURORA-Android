package eu.inscico.aurora_app.ui.screens.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.City
import eu.inscico.aurora_app.model.Gender
import eu.inscico.aurora_app.model.Gender.Companion.toGenderString
import eu.inscico.aurora_app.model.UserResponse
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.FormEntry
import eu.inscico.aurora_app.ui.components.FormEntryType
import eu.inscico.aurora_app.ui.theme.primary
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateProfileScreen(
    viewModel: CreateProfileViewModel = koinViewModel()
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
        mutableStateOf(countries.value?.first()/*"udn3GiM30aqviGBkswpl"*/)
    }

    val city = remember {
        mutableStateOf<City?>(viewModel.cities.value?.first())
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
                Text(text = stringResource(id = R.string.logout_button_title))
            },
            callback = {
                // TODO: logout
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.outline_account_circle_24),
                contentDescription = "",
                colorFilter = ColorFilter.tint(primary)
            )

            Text(
                modifier = Modifier.padding(32.dp),
                text = stringResource(id = R.string.create_profile_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(id = R.string.create_profile_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

            FormEntry(
                title = stringResource(id = R.string.create_profile_first_name_hint),
                formEntryType = FormEntryType.TEXT_INPUT,
                initialItem = firstName.value,
                callback = { name, _ ->
                    firstName.value = name
                }
            )

            FormEntry(
                title = stringResource(id = R.string.create_profile_last_name_hint),
                formEntryType = FormEntryType.TEXT_INPUT,
                initialItem = lastName.value,
                callback = { name, _ ->
                    lastName.value = name
                }
            )

            FormEntry(
                title = stringResource(id = R.string.create_profile_year_of_birth_hint),
                formEntryType = FormEntryType.SPINNER,
                initialItem = birthYear.value,
                items = viewModel.calendarYears,
                callback = { name, _ ->
                    birthYear.value = name
                }
            )

            FormEntry(
                title = stringResource(id = R.string.create_profile_gender_hint),
                formEntryType = FormEntryType.SPINNER,
                initialItem = gender.value.toGenderString(context),
                items = Gender.getGenderDisplayList(context),
                callback = { _, index ->
                    gender.value = viewModel.genders.get(index ?: viewModel.genders.lastIndex)
                }
            )
            Divider()

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

            FormEntry(
                title = stringResource(id = R.string.create_profile_country_title),
                formEntryType = FormEntryType.SPINNER,
                initialItem = country.value?.displayName ?: "",
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
            val citiesFromCountryNames = citiesFromCountry.value?.map { it.name }?.toMutableList()
            citiesFromCountryNames?.add(stringResource(id = R.string.create_profile_city_drop_down_other_city))
            if (citiesFromCountry.value?.isNotEmpty() == true) {
                FormEntry(
                    title = stringResource(id = R.string.create_profile_city_hint),
                    formEntryType = FormEntryType.SPINNER,
                    initialItem = city.value?.name ?: "",
                    items = citiesFromCountryNames,
                    callback = { _, index ->
                        if (index != null) {
                            val selectedCity = citiesFromCountry.value?.elementAt(index)
                            city.value = selectedCity
                        }
                    }
                )
                Divider()
            } else {
                Divider()
            }

            Text(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                text = stringResource(id = R.string.create_profile_country_selection_info),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(
                modifier = Modifier
                    .background(primary)
                    .padding(horizontal = 32.dp),
                onClick = {
                    val countryId = country.value?.id
                    if(countryId != null){
                        val user = UserResponse(
                            city = city.value?.id,
                            country = countryId,
                            firstName = firstName.value,
                            lastName = lastName.value,
                            gender = Gender.parseGenderToString(gender.value),
                            yearOfBirth = birthYear.value.toIntOrNull()
                        )
                        viewModel.createUser(user)
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