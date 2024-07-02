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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.*
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.parsePublicVehicleOccupancyToString
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.parseTransportationTypeToString
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.network.NetworkService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.datePicker.MaterialDatePickerDialog
import eu.inscico.aurora_app.ui.components.forms.AddSubtractCountFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import eu.inscico.aurora_app.ui.components.timePicker.TimePickerDialog
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.utils.TypedResult
import eu.inscico.aurora_app.services.shared.UnitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddTransportationConsumption(
    initialValues: Consumption.TransportationConsumption? = null,
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

    val convertedDistance = unitService.getDistanceInUsersPreferredUnit(config, distanceInKm = initialValues?.value, decimals = 1)
    val initialDistance = if (initialValues?.value != null) {
        unitService.getValueInUserPreferredNumberFormat(config, convertedDistance)
    } else {
        ""
    }

    val endOfTravelVisible = remember {
        mutableStateOf(false)
    }

    val distance = remember {
        mutableStateOf(initialDistance)
    }

    val startOfTravelAsLong = remember {
        mutableStateOf(
            initialValues?.transportation?.dateOfTravel?.timeInMillis
                ?: Calendar.getInstance().timeInMillis
        )
    }

    val endOfTravelAsLong = remember {
        mutableStateOf(
            startOfTravelAsLong.value
        )
    }

    val transportationType = remember {
        mutableStateOf<TransportationType?>(initialValues?.transportation?.transportationType)
    }

    val transportationSection = remember {
        mutableStateOf<TransportationTypeSection?>(
            viewModel.getSectionForSelectedTransportType(
                context,
                transportationType.value
            )
        )
    }

    val occupancyApproximately = remember {
        mutableStateOf<PublicVehicleOccupancy?>(initialValues?.transportation?.publicVehicleOccupancy)
    }

    val occupancyPrecisely = remember {
        mutableStateOf(initialValues?.transportation?.privateVehicleOccupancy ?: 1)
    }

    val description = remember {
        mutableStateOf(initialValues?.description ?: "")
    }

    val openDatePickerForStartDate = remember {
        mutableStateOf(false)
    }

    val openTimePickerForStartDate = remember {
        mutableStateOf(false)
    }

    val openDatePickerForEndDate = remember {
        mutableStateOf(false)
    }

    val openTimePickerForEndDate = remember {
        mutableStateOf(false)
    }

    val convertedFuelConsumption =
        if(transportationType.value == TransportationType.ELECTRIC_CAR
        || transportationType.value == TransportationType.ELECTRIC_MOTORCYCLE){
            unitService.getKWhPerDistanceInUserPreferredUnit(config, kWhPer100Km = initialValues?.transportation?.fuelConsumption, decimals = 1)
    } else {
            unitService.getVolumePerDistanceInUserPreferredUnit(config, literPer100km = initialValues?.transportation?.fuelConsumption, decimals = 1)
        }
    val initialFuelConsumption = if (initialValues?.transportation?.fuelConsumption != null) {
        unitService.getValueInUserPreferredNumberFormat(config, convertedFuelConsumption)
    } else {
        ""
    }

    val fuelConsumption = remember {
        mutableStateOf(initialFuelConsumption)
    }

    fun isSaveValid(): Boolean {
        return viewModel.isDecimalInputValid(distance.value) && distance.value.replace(",", ".")
            .toDoubleOrNull() != null
                && transportationType.value != null
                && ((transportationSection.value != TransportationTypeSection.TRAINS_AND_TRAMS
                || transportationSection.value != TransportationTypeSection.BUSSES) || occupancyApproximately.value != null)
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

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(shape = RoundedCornerShape(16.dp))
        ) {

            ListItem(
                modifier = Modifier
                    .fillMaxWidth(),
                headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_transportation_start_of_travel_title)) },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    openDatePickerForStartDate.value =
                                        !openDatePickerForStartDate.value
                                }
                                .defaultMinSize(minWidth = 90.dp, minHeight = 35.dp)
                        ) {

                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = startOfTravelAsLong.value

                            Text(
                                text = CalendarUtils.toDateString(calendar, unitService.getDateFormat(config)),
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.End,

                                    ),
                                textAlign = TextAlign.End
                            )

                            Image(
                                painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                            )

                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Row(
                            modifier = Modifier
                                .clickable {
                                    openTimePickerForStartDate.value =
                                        !openTimePickerForStartDate.value
                                }
                                .defaultMinSize(minWidth = 90.dp, minHeight = 35.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {

                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = startOfTravelAsLong.value

                            Text(
                                text = CalendarUtils.toDateString(calendar, unitService.getTimeFormat(config)),
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.End,

                                    ),
                                textAlign = TextAlign.End
                            )

                            Image(
                                painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                            )

                        }
                    }
                }
            )

            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        endOfTravelVisible.value = !endOfTravelVisible.value
                    },
                headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_transportation_end_of_travel_title)) },
                trailingContent = {

                    if (endOfTravelVisible.value) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        openDatePickerForEndDate.value =
                                            !openDatePickerForEndDate.value
                                    }
                                    .defaultMinSize(minWidth = 90.dp, minHeight = 35.dp)
                            ) {

                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = endOfTravelAsLong.value

                                Text(
                                    text = CalendarUtils.toDateString(calendar, unitService.getDateFormat(config)),
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.End,

                                        ),
                                    textAlign = TextAlign.End
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                                    contentDescription = "",
                                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                                )

                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Row(
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 90.dp, minHeight = 35.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {

                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = endOfTravelAsLong.value

                                Text(
                                    modifier = Modifier.clickable {
                                        openTimePickerForEndDate.value =
                                            !openTimePickerForEndDate.value
                                    },
                                    text = CalendarUtils.toDateString(calendar, unitService.getTimeFormat(config)),
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.End,
                                        ),
                                    textAlign = TextAlign.End
                                )

                                Image(
                                    modifier = Modifier.clickable {
                                        openTimePickerForEndDate.value =
                                            !openTimePickerForEndDate.value
                                    },
                                    painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                                    contentDescription = "",
                                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Image(modifier = Modifier.clickable {
                                    endOfTravelVisible.value = false
                                },
                                    painter = painterResource(id = R.drawable.round_close_24),
                                    contentDescription = "",
                                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                                )

                            }
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.choose),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            )


        }

        val startOfTravelAsCalendar = Calendar.getInstance()
        startOfTravelAsCalendar.timeInMillis = startOfTravelAsLong.value

        MaterialDatePickerDialog(
            modifier = Modifier,
            showDialog = openDatePickerForStartDate,
            confirmButtonCallback = {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.timeInMillis
                calendar.set(
                    Calendar.HOUR_OF_DAY,
                    startOfTravelAsCalendar.get(Calendar.HOUR_OF_DAY)
                )
                calendar.set(Calendar.MINUTE, startOfTravelAsCalendar.get(Calendar.MINUTE))

                startOfTravelAsLong.value = calendar.timeInMillis
                if (startOfTravelAsLong.value > endOfTravelAsLong.value) {
                    endOfTravelAsLong.value = startOfTravelAsLong.value
                }
            },
            dateValidator = {
                val maxCalendar = Calendar.getInstance()
                maxCalendar.add(Calendar.YEAR, 10)
                val minCalendar = Calendar.getInstance()
                minCalendar.add(Calendar.YEAR, -10)

                it.timeInMillis in minCalendar.timeInMillis..maxCalendar.timeInMillis
            }
        )


        TimePickerDialog(
            showDialog = openTimePickerForStartDate,
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

        val endOfTravelAsCalendar = Calendar.getInstance()
        endOfTravelAsCalendar.timeInMillis = endOfTravelAsLong.value

        MaterialDatePickerDialog(
            modifier = Modifier,
            showDialog = openDatePickerForEndDate,
            confirmButtonCallback = {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.timeInMillis
                calendar.set(
                    Calendar.HOUR_OF_DAY,
                    endOfTravelAsCalendar.get(Calendar.HOUR_OF_DAY)
                )
                calendar.set(Calendar.MINUTE, endOfTravelAsCalendar.get(Calendar.MINUTE))

                endOfTravelAsLong.value = calendar.timeInMillis
            },
            dateValidator = {
                val maxCalendar = Calendar.getInstance()
                maxCalendar.add(Calendar.YEAR, 10)

                it.timeInMillis >= startOfTravelAsLong.value && it.timeInMillis <= maxCalendar.timeInMillis
            }
        )


        TimePickerDialog(
            showDialog = openTimePickerForEndDate,
            time = endOfTravelAsCalendar,
            onTimeChanged = {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = endOfTravelAsLong.value
                calendar.set(Calendar.HOUR_OF_DAY, it.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, it.get(Calendar.MINUTE))
                endOfTravelAsLong.value = calendar.timeInMillis
            },
            validator = {
                it.timeInMillis >= startOfTravelAsLong.value
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val selectedTransportationType = if (transportationType.value != null) {
            SpinnerItem.Entry(
                name = transportationType.value!!.getDisplayName(context),
                data = transportationType.value
            )
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

        when (transportationSection.value) {
            TransportationTypeSection.CARS_AND_MOTORCYCLES -> {

                Spacer(Modifier.height(8.dp))

                val occupancyLimit = when (transportationType.value) {
                    TransportationType.FUEL_CAR,
                    TransportationType.ELECTRIC_CAR,
                    TransportationType.HYBRID_CAR -> 15
                    TransportationType.MOTORCYCLE,
                    TransportationType.ELECTRIC_MOTORCYCLE -> 3
                    else -> null
                }

                if (occupancyLimit != null && occupancyPrecisely.value >= occupancyLimit) {
                    occupancyPrecisely.value = occupancyLimit
                }

                AddSubtractCountFormEntry(
                    titleRes = R.string.home_add_consumption_transportation_public_occupancy_title,
                    initialValue = occupancyPrecisely.value,
                    isNullCountPossible = false,
                    countLimit = occupancyLimit ?: 100
                ) {
                    occupancyPrecisely.value = it
                    isSaveValid.value = isSaveValid()
                }
            }
            TransportationTypeSection.BUSSES,
            TransportationTypeSection.TRAINS_AND_TRAMS -> {

                Spacer(Modifier.height(8.dp))

                val allOccupanciesSpinnerItems = viewModel.allPublicVehicleOccupancyTypes.map {
                    SpinnerItem.Entry(name = it.getDisplayName(context), data = it)
                }

                val selectedOccupancy = if (occupancyApproximately.value != null) {
                    SpinnerItem.Entry(
                        name = occupancyApproximately.value!!.getDisplayName(context),
                        data = occupancyApproximately.value
                    )
                } else {
                    null
                }

                SpinnerFormEntry(
                    title = stringResource(id = R.string.home_add_consumption_transportation_public_occupancy_title),
                    selectedEntry = selectedOccupancy,
                    allEntries = allOccupanciesSpinnerItems,
                    callback = { item, _ ->
                        occupancyApproximately.value = item.data as PublicVehicleOccupancy
                        isSaveValid.value = isSaveValid()
                    }
                )
            }
            TransportationTypeSection.AVIATION,
            TransportationTypeSection.OTHER,
            null -> {
            }
        }

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
            value = distance.value,
            label = {
                Text(text = stringResource(id = R.string.home_add_consumption_transportation_distance_title))
            },
            onValueChange = {

                val isValueInCorrectFormat = viewModel.isDecimalInputValid(it)
                if (isValueInCorrectFormat || it.isEmpty()) {
                    val valueWithCorrectDecimalPoint =
                        if (Locale.getDefault() == Locale.US || Locale.getDefault() == Locale.UK) {
                            it.replace(",", ".")
                        } else {
                            it.replace(".", ",")
                        }
                    distance.value = valueWithCorrectDecimalPoint
                }
                isSaveValid.value = isSaveValid()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),

            trailingIcon = {
                Text(text = unitService.getUserPreferredDistanceUnit(config))
            }
        )

        if(transportationType.value == TransportationType.FUEL_CAR
            || transportationType.value == TransportationType.ELECTRIC_CAR
            || transportationType.value == TransportationType.HYBRID_CAR
            || transportationType.value == TransportationType.MOTORCYCLE
            || transportationType.value == TransportationType.ELECTRIC_MOTORCYCLE){

            Spacer(modifier = Modifier.height(16.dp))

            val unit = if(transportationType.value == TransportationType.ELECTRIC_MOTORCYCLE
                || transportationType.value == TransportationType.ELECTRIC_CAR){
                unitService.getUserPreferredKWhPerDistanceUnit(config)
            } else {
                unitService.getUserPreferredVolumePerDistanceUnit(config)
            }


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
                value = fuelConsumption.value,
                label = {
                    Text(text = stringResource(id = R.string.home_add_consumption_transportation_fuel_consumption_optional_title))
                },
                onValueChange = {

                    val isValueInCorrectFormat = viewModel.isDecimalInputValid(it)
                    if (isValueInCorrectFormat || it.isEmpty()) {
                        val valueWithCorrectDecimalPoint =
                            if (Locale.getDefault() == Locale.US || Locale.getDefault() == Locale.UK) {
                                it.replace(",", ".")
                            } else {
                                it.replace(".", ",")
                            }
                        fuelConsumption.value = valueWithCorrectDecimalPoint
                    }
                    isSaveValid.value = isSaveValid()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),

                trailingIcon = {
                    Text(text = unit)
                }
            )
        }


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

                    val dateOfTravelEnd = if (endOfTravelVisible.value) {
                        Timestamp(Date(endOfTravelAsLong.value))
                    } else {
                        null
                    }

                    val fuelConsumptionValue = fuelConsumption.value.replace(",", ".").toDoubleOrNull()
                    val fuelConsumptionInLper100km = if(transportationType.value == TransportationType.ELECTRIC_MOTORCYCLE ||
                        transportationType.value == TransportationType.ELECTRIC_CAR){
                        unitService.getKWhPerDistanceInKWhPer100Km(config, fuelConsumptionValue ?: 0.0, decimals = 1)

                    } else {
                        unitService.getVolumePerDistanceInLiterPer100Km(config, fuelConsumptionValue ?: 0.0, decimals = 1)
                    }

                    val transportationConsumptionDataResponse = when (transportationSection.value) {
                        TransportationTypeSection.CARS_AND_MOTORCYCLES -> {
                            TransportationConsumptionDataResponse(
                                dateOfTravel = Timestamp(Date(startOfTravelAsLong.value)),
                                dateOfTravelEnd = dateOfTravelEnd,
                                privateVehicleOccupancy = occupancyPrecisely.value,
                                transportationType = transportationType.value?.parseTransportationTypeToString(),
                                fuelConsumption = fuelConsumptionInLper100km
                                )
                        }
                        TransportationTypeSection.BUSSES,
                        TransportationTypeSection.TRAINS_AND_TRAMS -> {
                            TransportationConsumptionDataResponse(
                                publicVehicleOccupancy = occupancyApproximately.value?.parsePublicVehicleOccupancyToString(),
                                dateOfTravel = Timestamp(Date(startOfTravelAsLong.value)),
                                dateOfTravelEnd = dateOfTravelEnd,
                                transportationType = transportationType.value?.parseTransportationTypeToString()
                            )
                        }
                        TransportationTypeSection.AVIATION,
                        TransportationTypeSection.OTHER -> {
                            TransportationConsumptionDataResponse(
                                dateOfTravel = Timestamp(Date(startOfTravelAsLong.value)),
                                dateOfTravelEnd = dateOfTravelEnd,
                                transportationType = transportationType.value?.parseTransportationTypeToString()
                            )
                        }
                        null -> null
                    }

                    val descriptionValue = description.value.ifEmpty {
                        null
                    }

                    val distanceValue = distance.value.replace(",", ".").toDoubleOrNull()
                    val distanceValueKm = unitService.getDistanceInKm(config, distanceValue ?: 0.0)

                    val createdAt = if(isDuplicate == true){
                        Timestamp( Date(System.currentTimeMillis()) )
                    } else {
                        Timestamp(initialValues?.createdAt?.time ?: Date(System.currentTimeMillis()))
                    }

                    val consumptionResponse = ConsumptionResponse(
                        category = ConsumptionType.parseConsumptionTypeToString(ConsumptionType.TRANSPORTATION),
                        value = distanceValueKm,
                        description = descriptionValue,
                        createdAt = createdAt,
                        electricity = null,
                        heating = null,
                        transportation = transportationConsumptionDataResponse
                    )

                    if (consumptionResponse.value != null) {
                        CoroutineScope(Dispatchers.IO).launch {

                            val result = if(initialValues != null && isDuplicate == false){
                                consumptionResponse.id = initialValues.id
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