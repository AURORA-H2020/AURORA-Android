package eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption

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
import eu.inscico.aurora_app.model.consumptions.DistrictHeatingSource.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.DistrictHeatingSource.Companion.parseDistrictHeatingSourceToString
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType.Companion.parseHeatingFuelToString
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.network.NetworkService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.forms.AddSubtractCountFormEntry
import eu.inscico.aurora_app.ui.components.forms.BeginEndPickerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import eu.inscico.aurora_app.utils.LocaleUtils
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
fun AddHeatingConsumption(
    language: Locale = Locale.getDefault(),
    initialValue: Consumption.HeatingConsumption? = null,
    isDuplicate: Boolean? = false,
    viewModel: AddConsumptionViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get(),
    networkService: NetworkService = get(),
    unitService: UnitService = get()
){

    val context = LocalContext.current
    val config = LocalConfiguration.current

    val hasInternet = networkService.hasInternetConnectionLive.observeAsState()

    // set language, only necessary for screenshot ui tests
    LocaleUtils.updateLocale(context, language)

    val convertedInitialConsumption = if(initialValue?.value != null){

        when(initialValue.heating.heatingFuel){
            HeatingFuelType.BIOMASS,
            HeatingFuelType.LOCALLY_PRODUCED_BIOMASS,
            HeatingFuelType.FIREWOOD,
            HeatingFuelType.BUTANE -> {
                unitService.getWeightInUserPreferredUnit(config, initialValue.value, 1)
            }
            HeatingFuelType.OIL,
            HeatingFuelType.LPG -> {
                unitService.getVolumeInUserPreferredUnit(config, initialValue.value, 1)
            }
            HeatingFuelType.NATURAL_GAS,
            HeatingFuelType.GEO_THERMAL,
            HeatingFuelType.SOLAR_THERMAL,
            HeatingFuelType.DISTRICT,
            HeatingFuelType.ELECTRIC -> {
                initialValue.value
            }
        }
    } else {
        0.0
    }

    val convertedInitialConsumptionInUsersNumberFormat =
        unitService.getValueInUserPreferredNumberFormat(config, convertedInitialConsumption)

    val consumption = remember {
        mutableStateOf(convertedInitialConsumptionInUsersNumberFormat)
    }

    val peopleInHousehold = remember {
        mutableStateOf(initialValue?.heating?.householdSize ?: 1)
    }

    val beginDateTime = remember {
        mutableStateOf(initialValue?.heating?.startDate?.timeInMillis ?: Calendar.getInstance().timeInMillis)
    }

    val endDateTime = remember {
        mutableStateOf(initialValue?.heating?.endDate?.timeInMillis ?: (Calendar.getInstance().timeInMillis + (86400000 * 2).toLong()))
    }

    val initialCosts = if(initialValue?.heating?.costs != null){
        String.format("%.2f",initialValue.heating.costs)
    } else {
        ""
    }

    val costs = remember {
        mutableStateOf(initialCosts)
    }

    val description = remember {
        mutableStateOf(initialValue?.description ?: "")
    }

    val heatingFuel = remember {
        mutableStateOf<HeatingFuelType?>(initialValue?.heating?.heatingFuel)
    }

    val districtHeatingSource = remember {
        mutableStateOf<DistrictHeatingSource?>(initialValue?.heating?.districtHeatingSource)
    }


    fun isSaveValid(): Boolean {
        return consumption.value.replace(",", ".").toDoubleOrNull() != null
                && heatingFuel.value != null
                && (heatingFuel.value != HeatingFuelType.DISTRICT || districtHeatingSource.value != null)
    }

    val isSaveValid = remember {
        mutableStateOf(isSaveValid())
    }

    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        val allHeatingFuelSpinnerEntries = viewModel.allHeatingFuels.map {
            SpinnerItem.Entry(name = it.getDisplayName(context), data = it)
        }

        val selectedHeatingFuelEntry = if(heatingFuel.value != null){
            SpinnerItem.Entry(name = heatingFuel.value!!.getDisplayName(context) , data = heatingFuel.value)
        } else {
            null
        }

        SpinnerFormEntry(
            title = stringResource(id = R.string.home_add_consumption_heating_fuel_title),
            selectedEntry = selectedHeatingFuelEntry,
            allEntries = allHeatingFuelSpinnerEntries,
            callback = { item, _ ->
                heatingFuel.value = item.data as HeatingFuelType
                isSaveValid.value = isSaveValid()
            }
        )

        if(heatingFuel.value == HeatingFuelType.DISTRICT){

            val allDistrictHeatingSourceSpinnerItems = viewModel.allDistrictHeatingSource.map {
                SpinnerItem.Entry(name = it.getDisplayName(context), data = it)
            }

            val selectedEntry = if(districtHeatingSource.value != null){
                SpinnerItem.Entry(name = districtHeatingSource.value!!.getDisplayName(context) , data = heatingFuel.value)
            } else {
                null
            }

            Spacer(modifier = Modifier.height(8.dp))

            SpinnerFormEntry(
                title = stringResource(id = R.string.home_add_consumption_district_heating_source_title),
                selectedEntry = selectedEntry,
                allEntries = allDistrictHeatingSourceSpinnerItems,
                callback = { item, _ ->
                    districtHeatingSource.value = item.data as DistrictHeatingSource
                    isSaveValid.value = isSaveValid()
                }
            )

        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.home_add_consumption_form_heating_fuel_type_title),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(Modifier.height(16.dp))

        val consumptionUnit = when(heatingFuel.value){
            HeatingFuelType.BIOMASS,
            HeatingFuelType.LOCALLY_PRODUCED_BIOMASS,
            HeatingFuelType.FIREWOOD,
            HeatingFuelType.BUTANE -> {
                unitService.getUserPreferredWeightUnit(config)
            }
            HeatingFuelType.OIL,
            HeatingFuelType.LPG -> {
                unitService.getUserPreferredVolumeUnit(config)
            }
            HeatingFuelType.NATURAL_GAS,
            HeatingFuelType.GEO_THERMAL,
            HeatingFuelType.SOLAR_THERMAL,
            HeatingFuelType.DISTRICT,
            HeatingFuelType.ELECTRIC -> {
                "kWh"
            }
            else -> { "kWh" }
        }

        OutlinedTextField(
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = consumption.value,
            label = {
                Text(text = stringResource(id = R.string.home_add_consumption_form_consumption_title))
            },
            onValueChange = {
                consumption.value = it
                isSaveValid.value = isSaveValid()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            trailingIcon = { Text(text = consumptionUnit) }
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.home_add_consumption_form_heating_consumption_description_title),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(Modifier.height(16.dp))

        AddSubtractCountFormEntry(
            titleRes = R.string.home_add_consumption_form_people_in_household_title,
            initialValue = peopleInHousehold.value,
            isNullCountPossible = false){
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
                if(beginCalendarAsLong != null){
                    beginDateTime.value = beginCalendarAsLong
                }
                if(endCalendarAsLong != null){
                    endDateTime.value = endCalendarAsLong
                }
            })

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.home_add_consumption_form_heating_beginning_end_description_title),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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

                    val finalDistrictHeatingSource = if(heatingFuel.value == HeatingFuelType.DISTRICT){
                        districtHeatingSource.value
                    } else {
                        null
                    }

                    val finalValue = consumption.value.replace(",",".").toDoubleOrNull()
                    val finalHeatingFuel = heatingFuel.value
                    if(finalValue != null && finalHeatingFuel != null) {
                    val consumptionValue = when(finalHeatingFuel){
                        HeatingFuelType.BIOMASS,
                        HeatingFuelType.LOCALLY_PRODUCED_BIOMASS,
                        HeatingFuelType.FIREWOOD,
                        HeatingFuelType.BUTANE -> {
                            val consumptionInKg = unitService.getWeightInKg(config, finalValue)
                            consumptionInKg
                        }
                        HeatingFuelType.OIL,
                        HeatingFuelType.LPG -> {
                            val consumptionInLiter = unitService.getVolumeInLiter(config, finalValue)
                            consumptionInLiter
                        }
                        HeatingFuelType.NATURAL_GAS,
                        HeatingFuelType.GEO_THERMAL,
                        HeatingFuelType.SOLAR_THERMAL,
                        HeatingFuelType.DISTRICT,
                        HeatingFuelType.ELECTRIC -> {
                            finalValue
                        }
                    }

                        val heatingConsumptionDataResponse = HeatingConsumptionDataResponse(
                            costs = costs.value.replace(",",".").toDoubleOrNull(),
                            endDate = Timestamp(Date(endDateTime.value)),
                            startDate = Timestamp(Date(beginDateTime.value)),
                            householdSize = peopleInHousehold.value,
                            heatingFuel = heatingFuel.value?.parseHeatingFuelToString(),
                            districtHeatingSource = finalDistrictHeatingSource?.parseDistrictHeatingSourceToString(),
                        )

                        val descriptionValue = description.value.ifEmpty {
                            null
                        }

                        val createdAt = if(isDuplicate == true){
                            Timestamp( Date(System.currentTimeMillis()) )
                        } else {
                            Timestamp(initialValue?.createdAt?.time ?: Date(System.currentTimeMillis()))
                        }

                        val consumptionResponse = ConsumptionResponse(
                            category = ConsumptionType.parseConsumptionTypeToString(ConsumptionType.HEATING),
                            value = consumptionValue,
                            description = descriptionValue,
                            createdAt = createdAt,
                            electricity = null,
                            heating = heatingConsumptionDataResponse,
                            transportation = null
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            val result = if(initialValue != null && isDuplicate == false){
                                consumptionResponse.id = initialValue.id
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

                                else -> {}
                            }
                        }
                    } else {
                        userFeedbackService.showSnackbar(R.string.settings_add_consumption_entry_fail_message)
                    }
                },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)) {
                Text(
                    text = stringResource(id = R.string.settings_edit_profile_submit_button_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )

                Spacer(Modifier.height(16.dp))
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