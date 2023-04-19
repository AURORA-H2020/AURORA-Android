package eu.inscico.aurora_app.ui.screens.home.addConsumption

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.*
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.parsePublicVehicleOccupancyToString
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.parseTransportationTypeToString
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.datePicker.MaterialDatePickerDialog
import eu.inscico.aurora_app.ui.components.forms.AddSubtractCountFormEntry
import eu.inscico.aurora_app.ui.components.forms.BeginEndPickerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import eu.inscico.aurora_app.ui.components.timePicker.TimePickerDialog
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
fun AddTransportationConsumption(
    viewModel: AddConsumptionViewModel,
    navigationService: NavigationService = get()
){

    val context = LocalContext.current

    val distance = remember {
        mutableStateOf("")
    }

    val startOfTravelAsLong = remember {
        mutableStateOf(Calendar.getInstance().timeInMillis)
    }

    val transportationType = remember {
        mutableStateOf<TransportationType?>(null)
    }

    val transportationSection = remember {
        mutableStateOf<TransportationTypeSection?>(null)
    }

    val occupancyApproximately = remember {
        mutableStateOf<PublicVehicleOccupancy?>(null)
    }

    val occupancyPrecisely = remember {
        mutableStateOf(1)
    }

    val description = remember {
        mutableStateOf("")
    }

    val openDatePicker = remember {
        mutableStateOf(false)
    }

    val openTimePicker = remember {
        mutableStateOf(false)
    }

    fun isSaveValid(): Boolean {
        return distance.value.toDoubleOrNull() != null
                && transportationType.value != null
                && ((transportationSection.value != TransportationTypeSection.TRAINS_AND_TRAMS
                || transportationSection.value != TransportationTypeSection.BUSSES) || occupancyApproximately.value != null)
    }

    val isSaveValid = remember {
        mutableStateOf(isSaveValid())
    }


    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        val buttonBackground = MaterialTheme.colorScheme.outlineVariant

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    shape = RoundedCornerShape(4.dp)
                ),
            headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_transportation_start_of_travel_title)) },
            trailingContent = {
                Row(
                    horizontalArrangement = Arrangement.End,
                ) {
                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    Color(buttonBackground.value).copy(alpha = 0.2F),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                            }
                            .clickable {
                                openDatePicker.value = !openDatePicker.value
                            }
                            .defaultMinSize(minWidth = 90.dp, minHeight = 35.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = startOfTravelAsLong.value

                        Text(
                            text = CalendarUtils.toDateString(calendar),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 15.sp,
                                textAlign = TextAlign.End,

                                ),
                            textAlign = TextAlign.End
                        )

                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    Color(buttonBackground.value).copy(alpha = 0.2F),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                            }
                            .clickable {
                                openTimePicker.value = !openTimePicker.value
                            }
                            .defaultMinSize(minWidth = 90.dp, minHeight = 35.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = startOfTravelAsLong.value

                        Text(
                            text = CalendarUtils.toDateString(calendar, "HH:mm"),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 15.sp,
                                textAlign = TextAlign.End,

                                ),
                            textAlign = TextAlign.End
                        )

                    }
                }
            }
        )


        val startOfTravelAsCalendar = Calendar.getInstance()
        startOfTravelAsCalendar.timeInMillis = startOfTravelAsLong.value

        MaterialDatePickerDialog(
            modifier = Modifier,
            showDialog = openDatePicker,
            confirmButtonCallback = {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, startOfTravelAsCalendar.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, startOfTravelAsCalendar.get(Calendar.MINUTE))

                startOfTravelAsLong.value = calendar.timeInMillis
            },
            dateValidator = {
                true
            }
        )


        TimePickerDialog(
            showDialog = openTimePicker,
            time = startOfTravelAsCalendar,
            onTimeChanged = {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = startOfTravelAsLong.value
                calendar.set(Calendar.HOUR_OF_DAY, it.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, it.get(Calendar.MINUTE))
                startOfTravelAsLong.value = calendar.timeInMillis
            },
            validator = {
                true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val selectedTransportationType = if(transportationType.value != null){
            SpinnerItem.Entry(name = transportationType.value!!.getDisplayName(context) , data = transportationType.value)
        } else {
            null
        }

        SpinnerFormEntry(
            title = stringResource(id = R.string.home_add_consumption_transportation_type_title),
            selectedEntry = selectedTransportationType,
            allEntries = viewModel.getSpinnerItemsListOfTransportationType(context),
            callback = { item, section ->
                transportationType.value = item.data as TransportationType
                transportationSection.value = section?.section as? TransportationTypeSection
                isSaveValid.value = isSaveValid()
            }
        )

        when(transportationSection.value){
            TransportationTypeSection.CARS_AND_MOTORCYCLES -> {

                Spacer(Modifier.height(8.dp))

                AddSubtractCountFormEntry(
                    titleRes = R.string.home_add_consumption_transportation_public_occupancy_with_count_title,
                    initialValue = occupancyPrecisely.value,
                    isNullCountPossible = false
                ) {
                    occupancyPrecisely.value = it
                    isSaveValid.value = isSaveValid()
                }
            }
            TransportationTypeSection.BUSSES,
            TransportationTypeSection.TRAINS_AND_TRAMS -> {

                Spacer(Modifier.height(4.dp))

                val allOccupanciesSpinnerItems = viewModel.allPublicVehicleOccupancyTypes.map {
                    SpinnerItem.Entry(name = it.getDisplayName(context), data = it)
                }

                val selectedOccupancy = if(occupancyApproximately.value != null){
                    SpinnerItem.Entry(name = occupancyApproximately.value!!.getDisplayName(context), data = occupancyApproximately.value)
                } else {
                    null
                }

                SpinnerFormEntry(
                    title = stringResource(id = R.string.home_add_consumption_transportation_public_occupancy_title),
                    selectedEntry = selectedOccupancy,
                    allEntries = allOccupanciesSpinnerItems,
                    callback = { item , _ ->
                        occupancyApproximately.value = item.data as PublicVehicleOccupancy
                        isSaveValid.value = isSaveValid()
                    }
                )
            }
            TransportationTypeSection.AVIATION,
            TransportationTypeSection.OTHER,
            null -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = distance.value,
            label = {
                Text(text = stringResource(id = R.string.home_add_consumption_transportation_distance_title))
            },
            onValueChange = {
                distance.value = it
                isSaveValid.value = isSaveValid()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            trailingIcon = {
                Text(text = "km")
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

                    val transportationConsumptionDataResponse = when(transportationSection.value){
                        TransportationTypeSection.CARS_AND_MOTORCYCLES -> {
                            TransportationConsumptionDataResponse(
                                dateOfTravel = Timestamp(Date(startOfTravelAsLong.value)),
                                privateVehicleOccupancy = occupancyPrecisely.value,
                                transportationType = transportationType.value?.parseTransportationTypeToString()
                            )
                        }
                        TransportationTypeSection.BUSSES,
                        TransportationTypeSection.TRAINS_AND_TRAMS -> {
                            TransportationConsumptionDataResponse(
                                publicVehicleOccupancy = occupancyApproximately.value?.parsePublicVehicleOccupancyToString(),
                                dateOfTravel = Timestamp(Date(startOfTravelAsLong.value)),
                                transportationType = transportationType.value?.parseTransportationTypeToString()
                            )
                        }
                        TransportationTypeSection.AVIATION,
                        TransportationTypeSection.OTHER -> {
                            TransportationConsumptionDataResponse(
                                dateOfTravel = Timestamp(Date(startOfTravelAsLong.value)),
                                transportationType = transportationType.value?.parseTransportationTypeToString()
                            )
                        }
                        null -> null
                    }


                    val consumptionResponse = ConsumptionResponse(
                        category = ConsumptionType.parseConsumptionTypeToString(ConsumptionType.TRANSPORTATION),
                        value = distance.value.toDoubleOrNull(),
                        description = description.value,
                        createdAt = Timestamp(Date(System.currentTimeMillis())),
                        electricity = null,
                        heating = null,
                        transportation = transportationConsumptionDataResponse
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