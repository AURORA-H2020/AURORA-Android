package eu.inscico.aurora_app.ui.screens.home.recurringConsumptions

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.*
import eu.inscico.aurora_app.model.consumptions.ConsumptionType.Companion.getDisplayName
import eu.inscico.aurora_app.model.recurringConsumption.*
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import eu.inscico.aurora_app.ui.components.recurringConsumptions.FrequencySpinner
import eu.inscico.aurora_app.ui.screens.home.recurringConsumptions.transportation.AddTransportationRecurringConsumptionScreen
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecurringConsumptionScreen(
    initialValues: RecurringConsumption? = null,
    navigationService: NavigationService = get(),
    viewModel: AddRecurringConsumptionViewModel = koinViewModel(),
    userFeedbackService: UserFeedbackService = get()
) {

    val context = LocalContext.current

    val consumptionType = rememberSaveable {
        mutableStateOf(viewModel.consumptionType)
    }

    val isActive = remember {
        mutableStateOf(initialValues?.isEnabled ?: false)
    }

    val showAddTransportationRecurringConsumption = rememberSaveable {
        mutableStateOf(false)
    }

    val description = remember {
        mutableStateOf(initialValues?.description ?: "")
    }

    val transportationData = remember {
        mutableStateOf(initialValues?.transportation)
    }

    val frequency = remember {
        mutableStateOf(initialValues?.frequency)
    }

    val isInputSavable = remember {
        mutableStateOf(false)
    }

    val buttonColor = if (isInputSavable.value) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    fun areInputsValid(): Boolean {
        return frequency.value != null && transportationData.value != null && consumptionType.value != null
    }

    when(consumptionType.value){
        ConsumptionType.ELECTRICITY -> {}
        ConsumptionType.HEATING -> {}
        ConsumptionType.TRANSPORTATION -> {
            showAddTransportationRecurringConsumption.value = !showAddTransportationRecurringConsumption.value
        }
        null -> {}
    }

    Column(
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {

        AppBar(
            title = stringResource(id = R.string.home_recurring_consumptions_add_button_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabHome?.popBackStack()
            }
        )

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            val allSelectableConsumptionTypes = viewModel.allConsumptionTypes.map {
                SpinnerItem.Entry(name = it.getDisplayName(context), data = it)
            }

            val selectedConsumptionType = if (consumptionType.value != null) {
                SpinnerItem.Entry(
                    name = consumptionType.value!!.getDisplayName(context),
                    data = consumptionType.value
                )
            } else {
                null
            }

            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(id = R.string.home_recurring_consumptions_category_title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }


            SpinnerFormEntry(
                title = stringResource(id = R.string.home_recurring_consumptions_category_title),
                selectedEntry = selectedConsumptionType,
                allEntries = allSelectableConsumptionTypes,
                isReadOnly = true,
                callback = { item, _ ->
                    consumptionType.value = item.data as ConsumptionType
                    isInputSavable.value = areInputsValid()
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(id = R.string.home_recurring_consumptions_category_transportation_info_title),
                modifier = Modifier.padding(horizontal = 32.dp),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(id = R.string.home_recurring_consumptions_frequency_title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }


            FrequencySpinner {
                frequency.value = it
                isInputSavable.value = areInputsValid()
            }

            Spacer(Modifier.height(16.dp))

            AddTransportationRecurringConsumptionScreen() {
                transportationData.value = it
                isInputSavable.value = areInputsValid()
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(id = R.string.home_add_consumption_form_description_title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
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
                value = description.value,
                label = {
                    Text(text = stringResource(id = R.string.home_add_consumption_form_description_title))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                onValueChange = {
                    if (it.length <= 5000) {
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 32.dp).fillMaxWidth(),
                    enabled = isInputSavable.value,
                    shape = RoundedCornerShape(32.dp),
                    onClick = {

                        val descriptionValue = description.value.ifEmpty {
                            null
                        }
                        val categoryValue = ConsumptionType.parseConsumptionTypeToString(consumptionType.value)
                        val createdAtValue = initialValues?.createdAt ?: Timestamp(Date(System.currentTimeMillis()))
                        val transportationDataValue = RecurringConsumptionTransportationDataResponse.from(transportationData.value)
                        val isActiveValue = isActive.value
                        val frequencyValue = RecurringConsumptionFrequencyResponse.from(frequency.value)


                        val recurringConsumptionResponse = if(categoryValue != null && transportationDataValue != null && frequencyValue != null){

                            RecurringConsumptionResponse(
                                category = categoryValue,
                                description = descriptionValue,
                                createdAt = createdAtValue,
                                transportation = transportationDataValue,
                                isEnabled = isActiveValue,
                                frequency = frequencyValue
                            )
                        } else {
                            null
                        }

                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = createdAtValue.toDate().time

                        val transportationString = "transportationType: ${transportationDataValue?.transportationType}\n" +
                                "privateVehicleOccupancy: ${transportationDataValue?.privateVehicleOccupancy}\n" +
                                "publicVehicleOccupancy: ${transportationDataValue?.publicVehicleOccupancy}\n" +
                                "hourOfTravel: ${transportationDataValue?.hourOfTravel}\n" +
                                "minuteOfTravel: ${transportationDataValue?.minuteOfTravel}\n" +
                                "distance: ${transportationDataValue?.distance}"

                        Log.e("AddRecurringConsumptionScreen", "" +
                                "category: ${recurringConsumptionResponse?.category}\n" +
                                "description: $descriptionValue\n" +
                                "createdAt: ${CalendarUtils.toDateString(calendar, "dd.MM.yyyy, HH:mm")}\n" +
                                "transportation: $transportationString")


                        if(recurringConsumptionResponse != null){
                            CoroutineScope(Dispatchers.IO).launch {

                                val result = if(initialValues != null){
                                    recurringConsumptionResponse.id = initialValues.id
                                    viewModel.updateRecurringConsumption(recurringConsumptionResponse)
                                } else {
                                    viewModel.createRecurringConsumption(recurringConsumptionResponse)
                                }
                                when (result) {
                                    is TypedResult.Failure -> {
                                        userFeedbackService.showSnackbar(R.string.settings_add_consumption_entry_fail_message)
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
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)) {
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
