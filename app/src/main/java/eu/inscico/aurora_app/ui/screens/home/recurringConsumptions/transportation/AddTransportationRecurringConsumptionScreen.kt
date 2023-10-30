package eu.inscico.aurora_app.ui.screens.home.recurringConsumptions.transportation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.TransportationType
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.TransportationTypeSection
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumptionTransportationData
import eu.inscico.aurora_app.ui.components.forms.AddSubtractCountFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import eu.inscico.aurora_app.ui.components.timePicker.TimePickerDialog
import eu.inscico.aurora_app.ui.screens.home.recurringConsumptions.AddOrUpdateRecurringConsumptionViewModel
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.utils.UnitUtils
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransportationRecurringConsumptionScreen(
    initialValues: RecurringConsumptionTransportationData? = null,
    viewModel: AddOrUpdateRecurringConsumptionViewModel = koinViewModel(),
    callback: (RecurringConsumptionTransportationData?) -> Unit
) {

    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val transportationType = remember {
        mutableStateOf<TransportationType?>(initialValues?.transportationType)
    }

    val transportationSection = remember {
        mutableStateOf<TransportationTypeSection?>(viewModel.getSectionForSelectedTransportType(context, transportationType.value))
    }

    val occupancyApproximately = remember {
        mutableStateOf<PublicVehicleOccupancy?>(initialValues?.publicVehicleOccupancy)
    }

    val occupancyPrecisely = remember {
        mutableStateOf(initialValues?.privateVehicleOccupancy ?: 1)
    }

    val startOfTravelAsLong = remember {
        mutableStateOf(initialValues?.timeOfTravel?.timeInMillis ?: Calendar.getInstance().timeInMillis)
    }

    val recurringTransportationConsumption = remember {
        mutableStateOf<RecurringConsumptionTransportationData?>(null)
    }

    val openTimePicker = remember {
        mutableStateOf(false)
    }

    val initialDistance = if(initialValues?.distance != null){
        "${UnitUtils.getConvertedDistance(distanceInKm = initialValues.distance, locale = configuration.locales[0], decimals = 1)}"
    } else {
        ""
    }

    val distance = remember {
        mutableStateOf(initialDistance)
    }

    fun assembleTransportationData(): RecurringConsumptionTransportationData? {

        val timeOfTravel = Calendar.getInstance()
        timeOfTravel.timeInMillis = startOfTravelAsLong.value

        val distanceValue = distance.value.replace(",", ".").toDoubleOrNull()
        val distanceValueKm = UnitUtils.getDistanceValueMetric(distanceValue ?: 0.0 , configuration.locales[0])

        return when(transportationSection.value){
            TransportationTypeSection.CARS_AND_MOTORCYCLES -> {
                RecurringConsumptionTransportationData(
                    timeOfTravel = timeOfTravel,
                    privateVehicleOccupancy = occupancyPrecisely.value,
                    transportationType = transportationType.value ?: return null,
                    publicVehicleOccupancy = null,
                    distance = distanceValueKm,
                )
            }
            TransportationTypeSection.BUSSES,
            TransportationTypeSection.TRAINS_AND_TRAMS -> {
                RecurringConsumptionTransportationData(
                    timeOfTravel = timeOfTravel,
                    privateVehicleOccupancy = null,
                    transportationType = transportationType.value ?: return null,
                    publicVehicleOccupancy = occupancyApproximately.value ?: return null,
                    distance = distanceValueKm,
                )
            }
            TransportationTypeSection.AVIATION,
            TransportationTypeSection.OTHER -> {
                RecurringConsumptionTransportationData(
                    timeOfTravel = timeOfTravel,
                    privateVehicleOccupancy = null,
                    transportationType = transportationType.value ?: return null,
                    publicVehicleOccupancy = null,
                    distance = distanceValueKm,
                )
            }
            null -> return null
        }
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
            text = stringResource(id = R.string.home_consumptions_type_transportation_title),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
    }

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
                        modifier = Modifier
                            .clickable {
                                openTimePicker.value = !openTimePicker.value
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
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

                        Image(
                            painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                        )

                    }
                }
            }
        )

        val startOfTravelAsCalendar = Calendar.getInstance()
        startOfTravelAsCalendar.timeInMillis = startOfTravelAsLong.value

        TimePickerDialog(
            showDialog = openTimePicker,
            time = startOfTravelAsCalendar,
            onTimeChanged = {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = startOfTravelAsLong.value
                calendar.set(Calendar.HOUR_OF_DAY, it.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, it.get(Calendar.MINUTE))
                startOfTravelAsLong.value = calendar.timeInMillis

                callback.invoke(assembleTransportationData())
            },
            validator = {
                true
            }
        )

        Divider()

        val selectedTransportationType = if(transportationType.value != null){
            SpinnerItem.Entry(name = transportationType.value!!.getDisplayName(context) , data = transportationType.value)
        } else {
            null
        }

        SpinnerFormEntry(
            title = stringResource(id = R.string.home_add_consumption_transportation_type_title),
            selectedEntry = selectedTransportationType,
            isRoundedDesign = false,
            allEntries = viewModel.getSpinnerItemsListOfTransportationType(context),
            callback = { item, section ->
                transportationType.value = item.data as TransportationType
                transportationSection.value = section?.section as? TransportationTypeSection

                callback.invoke(assembleTransportationData())
            }
        )

        when(transportationSection.value){
            TransportationTypeSection.CARS_AND_MOTORCYCLES -> {

                val occupancyLimit = when(transportationType.value){
                    TransportationType.FUEL_CAR,
                    TransportationType.ELECTRIC_CAR,
                    TransportationType.HYBRID_CAR -> 15
                    TransportationType.MOTORCYCLE,
                    TransportationType.ELECTRIC_MOTORCYCLE -> 3
                    else -> null
                }

                if(occupancyLimit != null && occupancyPrecisely.value >= occupancyLimit){
                    occupancyPrecisely.value = occupancyLimit
                }

                Divider()

                AddSubtractCountFormEntry(
                    titleRes = R.string.home_add_consumption_transportation_public_occupancy_title,
                    initialValue = occupancyPrecisely.value,
                    isNullCountPossible = false,
                    isRoundedDesign = false,
                    countLimit = occupancyLimit ?: 100
                ) {
                    occupancyPrecisely.value = it
                    callback.invoke(assembleTransportationData())
                }
            }
            TransportationTypeSection.BUSSES,
            TransportationTypeSection.TRAINS_AND_TRAMS -> {

                val allOccupanciesSpinnerItems = viewModel.allPublicVehicleOccupancyTypes.map {
                    SpinnerItem.Entry(name = it.getDisplayName(context), data = it)
                }

                val selectedOccupancy = if(occupancyApproximately.value != null){
                    SpinnerItem.Entry(name = occupancyApproximately.value!!.getDisplayName(context), data = occupancyApproximately.value)
                } else {
                    null
                }

                Divider()

                SpinnerFormEntry(
                    title = stringResource(id = R.string.home_add_consumption_transportation_public_occupancy_title),
                    selectedEntry = selectedOccupancy,
                    isRoundedDesign = false,
                    allEntries = allOccupanciesSpinnerItems,
                    callback = { item , _ ->
                        occupancyApproximately.value = item.data as PublicVehicleOccupancy
                        callback.invoke(assembleTransportationData())
                    }
                )
            }
            TransportationTypeSection.AVIATION,
            TransportationTypeSection.OTHER,
            null -> {}
        }

        Divider()

        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            value = distance.value,
            label = {
                Text(text = stringResource(id = R.string.home_add_consumption_transportation_distance_title))
            },
            onValueChange = {

                val isValueInCorrectFormat = viewModel.isDecimalInputValid(it)
                if(isValueInCorrectFormat || it.isEmpty()){
                    val valueWithCorrectDecimalPoint = if(Locale.getDefault() == Locale.US || Locale.getDefault() == Locale.UK){
                        it.replace(",",".")
                    } else {
                        it.replace(".",",")
                    }
                    distance.value = valueWithCorrectDecimalPoint
                    callback.invoke(assembleTransportationData())
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),

            trailingIcon = {
                Text(modifier = Modifier.padding(end = 32.dp), text = UnitUtils.getDistanceUnit(configuration.locales[0]))
            }
        )

    }
}