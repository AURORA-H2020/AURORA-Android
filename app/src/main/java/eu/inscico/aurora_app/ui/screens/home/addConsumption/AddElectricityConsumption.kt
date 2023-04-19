package eu.inscico.aurora_app.ui.screens.home.addConsumption

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ListItem
import androidx.compose.material3.TextButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionResponse
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.ElectricityConsumptionDataResponse
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.datePicker.MaterialDatePickerDialog
import eu.inscico.aurora_app.ui.components.forms.AddSubtractCountFormEntry
import eu.inscico.aurora_app.ui.components.forms.BeginEndPickerFormEntry
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.*

@Composable
fun AddElectricityConsumption(
    viewModel: AddConsumptionViewModel,
    navigationService: NavigationService = get()
) {

    val consumption = remember {
        mutableStateOf("")
    }

    val peopleInHousehold = remember {
        mutableStateOf(1)
    }

    val beginDateTime = remember {
        mutableStateOf(Calendar.getInstance().timeInMillis)
    }

    val endDateTime = remember {
        mutableStateOf(Calendar.getInstance().timeInMillis + (86400000 * 2).toLong())
    }

    val costs = remember {
        mutableStateOf("")
    }

    val description = remember {
        mutableStateOf("")
    }

    val isSaveValid = remember {
        mutableStateOf(consumption.value.toDoubleOrNull() != null)
    }


    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = consumption.value,
            label = {
                Text(text = stringResource(id = R.string.home_add_consumption_form_consumption_title))
            },
            onValueChange = {
                consumption.value = it
                isSaveValid.value = it.toDoubleOrNull() != null
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Text(text = "kWh")
            }
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.home_add_consumption_form_electricity_consumption_description_title),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(Modifier.height(16.dp))

        AddSubtractCountFormEntry(
            titleRes = R.string.home_add_consumption_form_people_in_household_title,
            initialValue = peopleInHousehold.value,
            isNullCountPossible = false
        ) {
            peopleInHousehold.value = it
        }


        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.home_add_consumption_form_people_in_household_description_title),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(Modifier.height(16.dp))

        BeginEndPickerFormEntry(
            initialBeginCalendarAsLong = beginDateTime.value,
            initialEndCalendarAsLong = endDateTime.value,
            callback = { beginCalendarAsLong: Long?, endCalendarAsLong: Long? ->
                if (beginCalendarAsLong != null) {
                    beginDateTime.value = beginCalendarAsLong
                }
                if (endCalendarAsLong != null) {
                    endDateTime.value = endCalendarAsLong
                }
            })

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.home_add_consumption_form_electricity_beginning_end_description_title),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = costs.value,
            label = {
                Text(text = stringResource(id = R.string.home_add_consumption_form_costs_title))
            },
            onValueChange = {
                costs.value = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Text(text = "€")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            androidx.compose.material.Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(id = R.string.home_add_consumption_form_description_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = description.value,
            label = {
                Text(text = stringResource(id = R.string.home_add_consumption_form_description_title))
            },
            onValueChange = {
                description.value = it
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.home_add_consumption_form_description_info_text_title),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSecondary
        )

        val buttonColor = if (isSaveValid.value) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton(
                modifier = Modifier
                    .background(buttonColor)
                    .padding(horizontal = 32.dp),
                enabled = isSaveValid.value,
                onClick = {

                    val electricityConsumptionDataResponse = ElectricityConsumptionDataResponse(
                        costs = costs.value.toDoubleOrNull(),
                        endDate = Timestamp(Date(endDateTime.value)),
                        startDate = Timestamp(Date(beginDateTime.value)),
                        householdSize = peopleInHousehold.value
                    )

                    val consumptionResponse = ConsumptionResponse(
                        category = ConsumptionType.parseConsumptionTypeToString(ConsumptionType.ELECTRICITY),
                        value = consumption.value.toDoubleOrNull(),
                        description = description.value,
                        createdAt = Timestamp(Date(System.currentTimeMillis())),
                        electricity = electricityConsumptionDataResponse,
                        heating = null,
                        transportation = null
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val result = viewModel.createConsumption(consumptionResponse)
                        when (result) {
                            is TypedResult.Failure -> {
                                // TODO:
                            }
                            is TypedResult.Success -> {
                                withContext(Dispatchers.Main) {
                                    navigationService.navControllerTabHome?.popBackStack(
                                        route = NavGraphDirections.AddConsumption.getNavRoute(),
                                        inclusive = true
                                    )
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