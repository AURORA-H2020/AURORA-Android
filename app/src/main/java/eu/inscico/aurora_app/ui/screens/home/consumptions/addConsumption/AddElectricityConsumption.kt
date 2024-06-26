package eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.*
import eu.inscico.aurora_app.model.consumptions.ElectricitySource.Companion.getDisplayName
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.network.NetworkService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.forms.AddSubtractCountFormEntry
import eu.inscico.aurora_app.ui.components.forms.BeginEndPickerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import eu.inscico.aurora_app.utils.TypedResult
import eu.inscico.aurora_app.services.shared.UnitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddElectricityConsumption(
    initialValues: Consumption.ElectricityConsumption? = null,
    isDuplicate: Boolean? = false,
    viewModel: AddConsumptionViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get(),
    networkService: NetworkService = get(),
    unitService: UnitService = get()
) {

    val context = LocalContext.current
    val config = LocalConfiguration.current

    val hasInternet = networkService.hasInternetConnectionLive.observeAsState()

    val initialConsumption = if(initialValues?.value != null){
        unitService.getValueInCorrectNumberFormat(config, String.format("%.1f",initialValues.value).replace(",",".").toDouble())
    } else {
        ""
    }

    val initialEnergyExportedFromHomePV = if(initialValues?.electricity?.electricityExported != null){
        unitService.getValueInCorrectNumberFormat(config, String.format("%.1f",initialValues.electricity.electricityExported).replace(",",".").toDouble())
    } else {
        ""
    }

    val consumption = remember {
        mutableStateOf(initialConsumption)
    }

    val electricityExpandedFromHomePV = remember {
        mutableStateOf(initialEnergyExportedFromHomePV)
    }

    val peopleInHousehold = remember {
        mutableStateOf(initialValues?.electricity?.householdSize ?: 1)
    }

    val beginDateTime = remember {
        mutableStateOf(initialValues?.electricity?.startDate?.timeInMillis ?: Calendar.getInstance().timeInMillis)
    }

    val endDateTime = remember {
        mutableStateOf(initialValues?.electricity?.endDate?.timeInMillis ?: (Calendar.getInstance().timeInMillis + (86400000 * 2).toLong()))
    }

    val initialCosts = if(initialValues?.electricity?.costs != null){
        String.format("%.1f",initialValues.electricity.costs)
    } else {
        ""
    }

    val costs = remember {
        mutableStateOf(initialCosts)
    }

    val description = remember {
        mutableStateOf(initialValues?.description ?: "")
    }


    val electricitySource = remember {
        mutableStateOf<ElectricitySource?>(initialValues?.electricity?.electricitySource)
    }

    val isSaveValid = remember {
        mutableStateOf(viewModel.isDecimalInputValid(consumption.value) && consumption.value.replace(",", ".").toDoubleOrNull() != null)
    }


    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        val consumptionLabel = if(electricitySource.value == ElectricitySource.HOME_PHOTOVOLTAICS){
            stringResource(id = R.string.home_add_consumption_form_energy_produced_title)
        } else {
            stringResource(id = R.string.home_add_consumption_form_consumption_title)
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
            ,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            value = consumption.value,
            label = {
                Text(text = consumptionLabel)
            },
            onValueChange = {

                val isValueInCorrectFormat = viewModel.isDecimalInputValid(it)
                if(isValueInCorrectFormat || it.isEmpty()){
                    val valueWithCorrectDecimalPoint = if(Locale.getDefault() == Locale.US || Locale.getDefault() == Locale.UK){
                        it.replace(",",".")
                    } else {
                        it.replace(".",",")
                    }
                    consumption.value = valueWithCorrectDecimalPoint
                }
                isSaveValid.value = isValueInCorrectFormat && it.replace(",",".").toDoubleOrNull() != null
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            trailingIcon = {
                Text(text = "kWh")
            }
        )

        if(electricitySource.value == ElectricitySource.HOME_PHOTOVOLTAICS){

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                ,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                value = electricityExpandedFromHomePV.value,
                label = {
                    Text(text = stringResource(id = R.string.home_add_consumption_form_energy_exported_title))
                },
                onValueChange = {

                    val isValueInCorrectFormat = viewModel.isDecimalInputValid(it)
                    if(isValueInCorrectFormat || it.isEmpty()){
                        val valueWithCorrectDecimalPoint = if(Locale.getDefault() == Locale.US || Locale.getDefault() == Locale.UK){
                            it.replace(",",".")
                        } else {
                            it.replace(".",",")
                        }
                        electricityExpandedFromHomePV.value = valueWithCorrectDecimalPoint
                    }
                    isSaveValid.value = isValueInCorrectFormat && it.replace(",",".").toDoubleOrNull() != null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                trailingIcon = {
                    Text(text = "kWh")
                }
            )
        }

        Spacer(Modifier.height(4.dp))

        val consumptionInfoText = if(electricitySource.value == ElectricitySource.HOME_PHOTOVOLTAICS){
            stringResource(id = R.string.home_add_consumption_form_electricity_energy_expanded_description_title)
        } else {
            stringResource(id = R.string.home_add_consumption_form_electricity_consumption_description_title)
        }

        Text(
            text = consumptionInfoText,
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

        val allElectricitySourceSpinnerItems = viewModel.allElectricitySources.map {
            SpinnerItem.Entry(name = it.getDisplayName(context), data = it)
        }

        val selectedEntry = if(electricitySource.value != null){
            SpinnerItem.Entry(name = electricitySource.value!!.getDisplayName(context) , data = electricitySource.value)
        } else {
            null
        }

        SpinnerFormEntry(
            title = stringResource(id = R.string.electricity_source_title),
            selectedEntry = selectedEntry,
            allEntries = allElectricitySourceSpinnerItems,
            callback = { item, _ ->
                electricitySource.value = item.data as ElectricitySource
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.electricity_source_description),
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            value = costs.value,
            label = {
                Text(text = stringResource(id = R.string.home_add_consumption_form_costs_title))
            },
            onValueChange = {
                val isValueInCorrectFormat = viewModel.isDecimalInputValid(it)
                if(isValueInCorrectFormat || it.isEmpty()){
                    val valueWithCorrectDecimalPoint = if(Locale.getDefault() == Locale.US || Locale.getDefault() == Locale.UK){
                        it.replace(",",".")
                    } else {
                        it.replace(".",",")
                    }
                    costs.value = valueWithCorrectDecimalPoint
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            trailingIcon = {
                Text(text = unitService.getCurrencyUnit(LocalConfiguration.current))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            value = description.value,
            placeholder = {
                Text(text = stringResource(id = R.string.home_add_consumption_form_description_title))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            onValueChange = {
                if(it.length <= 5000){
                    description.value = it
                }
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

            Button(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                enabled = isSaveValid.value,
                shape = RoundedCornerShape(32.dp),
                onClick = {

                    val electricityExported = if(electricitySource.value == ElectricitySource.HOME_PHOTOVOLTAICS){
                        electricityExpandedFromHomePV.value.replace(",",".").toDoubleOrNull()
                    } else {
                        null
                    }

                    val electricityConsumptionDataResponse = ElectricityConsumptionDataResponse(
                        costs = costs.value.replace(",",".").toDoubleOrNull(),
                        endDate = Timestamp(Date(endDateTime.value)),
                        startDate = Timestamp(Date(beginDateTime.value)),
                        householdSize = peopleInHousehold.value,
                        electricitySource = ElectricitySource.parseElectricitySourceToString(electricitySource.value ?: ElectricitySource.DEFAULT),
                        electricityExported = electricityExported
                    )

                    val descriptionValue = description.value.ifEmpty {
                        null
                    }

                    val consumptionResponse = ConsumptionResponse(
                        category = ConsumptionType.parseConsumptionTypeToString(ConsumptionType.ELECTRICITY),
                        value = consumption.value.replace(",",".").toDoubleOrNull(),
                        description = descriptionValue,
                        createdAt = Timestamp(initialValues?.createdAt?.time ?: Date(System.currentTimeMillis())),
                        electricity = electricityConsumptionDataResponse,
                        heating = null,
                        transportation = null
                    )

                    if(consumptionResponse.value != null){
                        CoroutineScope(Dispatchers.IO).launch {

                            val result = if(initialValues != null && isDuplicate == false){
                                consumptionResponse.id = initialValues.id
                                consumptionResponse.updatedAt = Timestamp(Calendar.getInstance().time)
                                viewModel.updateConsumption(consumptionResponse)
                            } else {
                                viewModel.createConsumption(consumptionResponse)
                            }
                            when (result) {
                                is TypedResult.Failure -> {
                                    if(result.reason == "NO_INTERNET"){
                                        userFeedbackService.showSnackbar(R.string.userfeedback_add_consumption_no_internet_connection)
                                        withContext(Dispatchers.Main) {
                                            navigationService.navControllerTabHome?.popBackStack()
                                        }
                                    } else {
                                        userFeedbackService.showSnackbar(R.string.settings_add_consumption_entry_fail_message)
                                    }
                                }
                                is TypedResult.Success -> {
                                    withContext(Dispatchers.Main) {
                                        navigationService.navControllerTabHome?.popBackStack()
                                    }
                                }
                            }
                        }
                    } else {
                        userFeedbackService.showSnackbar(R.string.settings_add_consumption_entry_fail_message)
                    }

                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text(
                    text = stringResource(id = R.string.settings_edit_profile_submit_button_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }

        if(hasInternet.value != true){
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(id = R.string.userfeedback_add_consumption_no_internet_connection),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}