package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.add_investment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.Timestamp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionResponse
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.DistrictHeatingSource.Companion.parseDistrictHeatingSourceToString
import eu.inscico.aurora_app.model.consumptions.HeatingConsumptionDataResponse
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType.Companion.parseHeatingFuelToString
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.DecoratedHeadline
import eu.inscico.aurora_app.ui.components.datePicker.MaterialDatePickerDialog
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPVInvestmentScreen(
    viewModel: AddPVInvestmentViewModel = get(),
    navigationService: NavigationService = get(),
    unitService: UnitService = get(),
    userFeedbackService: UserFeedbackService = get()
) {

    val state = viewModel.state.collectAsStateWithLifecycle().value

    val config = LocalConfiguration.current

    val openDatePickerForStartDate = remember {
        mutableStateOf(false)
    }

    val buttonColor = if (state.isSaveValid) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    LaunchedEffect(key1 = state.creationResult) {
       state.creationResult?.onSuccess {
           userFeedbackService.showSnackbar("Das Investment wurde erfolgreich hinzugefügt.")
           navigationService.navControllerTabPhotovoltaic?.popBackStack()
       }
        state.creationResult?.onFailure {
            userFeedbackService.showSnackbar("Ein Fehler ist aufgetreten. Das Investment konnte nicht hinzugefügt werden.")
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Add Investment",
                hasBackNavigation = true,
                backNavigationCallback = {
                    navigationService.navControllerTabPhotovoltaic?.popBackStack()
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
            ) {

                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    DecoratedHeadline(
                        headline = "Important!",
                        leadingIconRes = R.drawable.outline_warning_amber_24,
                        leadingIconColor = electricityYellow
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Entering you investment data is exclussively for recording purposes. This does not formulate an actual investment or binding order in any form.",
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Button(
                        onClick = {

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(horizontalArrangement = Arrangement.Center) {
                                Text(
                                    text = "How to invest?",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        value = state.shareField,
                        label = {
                            Text(text = "Shares")
                        },
                        onValueChange = {
                            viewModel.isShareFieldValid(it)
                            viewModel.checkIfFormIsReadyToSend()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "How much of the solar panel installation's capacity your investment corresponds to. You should find this information in the documents you received when you made your investment.",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    val pvPlant = viewModel.userPVPlant.collectAsStateWithLifecycle().value
                    val userCountry = viewModel.userCountry.collectAsStateWithLifecycle().value
                    val kwPerShare = pvPlant?.kwPerShare
                    val pricePerShare = pvPlant?.pricePerShare
                    val currency = userCountry?.currencyCode
                    if(pricePerShare != null && kwPerShare != null){
                        val kwWithUnit = "${unitService.getValueWithLocalDecimalPoint(kwPerShare.toString())} W"
                        val priceWithUnit = "${unitService.getValueWithLocalDecimalPoint(pricePerShare.toString())} ${currency ?: "€"}"

                        Text(
                            text = "1 Share = $kwWithUnit ($priceWithUnit)",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )

                        Spacer(Modifier.height(16.dp))
                    }

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(16.dp))
                    ) {

                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            headlineContent = { Text(text = "Investment Date") },
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
                                        calendar.timeInMillis =
                                            state.investmentDateField.timeInMillis

                                        Text(
                                            text = CalendarUtils.toDateString(
                                                calendar,
                                                unitService.getDateFormat(config)
                                            ),
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
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "The day you made the investment on.",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        value = state.noteField,
                        label = {
                            Text(text = "Note")
                        },
                        onValueChange = {
                            viewModel.updateNoteField(it)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Add a note to your investment for personal reference.",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Button(
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                                .fillMaxWidth(),
                            enabled = state.isSaveValid,
                            shape = RoundedCornerShape(32.dp),
                            onClick = {
                                viewModel.createPVInvestment()
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
        })

    val startOfTravelAsCalendar = Calendar.getInstance()
    startOfTravelAsCalendar.timeInMillis = state.investmentDateField.timeInMillis

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
            viewModel.updateInvestmentDateField(calendar)
        },
        dateValidator = {
            val maxCalendar = Calendar.getInstance()
            maxCalendar.add(Calendar.YEAR, 10)
            val minCalendar = Calendar.getInstance()
            minCalendar.add(Calendar.YEAR, -10)

            it.timeInMillis in minCalendar.timeInMillis..maxCalendar.timeInMillis
        }
    )
}