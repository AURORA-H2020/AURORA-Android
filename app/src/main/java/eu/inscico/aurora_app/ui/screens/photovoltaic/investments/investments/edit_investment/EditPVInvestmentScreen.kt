package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.edit_investment

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
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
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.DecoratedHeadline
import eu.inscico.aurora_app.ui.components.datePicker.MaterialDatePickerDialog
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.utils.CalendarUtils
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPVInvestmentScreen(
    viewModel: EditPVInvestmentViewModel = koinViewModel(),
    userFeedbackService: UserFeedbackService = get(),
    navigationService: NavigationService = get(),
    unitService: UnitService = get()
) {

    val state = viewModel.state.collectAsStateWithLifecycle().value

    val config = LocalConfiguration.current
    val context = LocalContext.current

    val openDatePickerForStartDate = remember {
        mutableStateOf(false)
    }

    val buttonColor = if (state.isSaveValid) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    LaunchedEffect(Unit) {
        viewModel.fetchInvestment()
        viewModel.checkIfFormIsReadyToSend()
    }

    LaunchedEffect(key1 = state.editResult) {
        state.editResult?.onSuccess {
            userFeedbackService.showSnackbar(context.getString(R.string.userfeedback_edit_investment_update_success))
            navigationService.navControllerTabPhotovoltaic?.popBackStack()
        }
        state.editResult?.onFailure {
            userFeedbackService.showSnackbar(context.getString(R.string.userfeedback_edit_investment_update_failure))
        }
    }

    LaunchedEffect(key1 = state.deleteResult) {
        state.deleteResult?.onSuccess {
            userFeedbackService.showSnackbar(context.getString(R.string.userfeedback_edit_investment_delete_success))
            navigationService.navControllerTabPhotovoltaic?.popBackStack()
        }
        state.deleteResult?.onFailure {
            userFeedbackService.showSnackbar(context.getString(R.string.userfeedback_edit_investment_delete_failure))
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(R.string.solar_power_edit_investment_headline),
                hasBackNavigation = true,
                backNavigationCallback = {
                    navigationService.navControllerTabPhotovoltaic?.popBackStack()
                },
                actionButton = {
                    Row(modifier = Modifier.padding(8.dp).clickable {
                        userFeedbackService.showDialog(
                            title = context.getString(R.string.userdialog_delete_investment_title),
                            message = context.getString(R.string.userdialog_delete_investment_message),
                            confirmButtonText = context.getString(R.string.delete),
                            confirmButtonCallback = {
                                viewModel.deletePVInvestment()
                            },
                            dismissButtonText = context.getString(R.string.cancel)
                        )
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_delete_outline_24),
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = null
                        )
                    }
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
                        headline = stringResource(R.string.solar_power_edit_investment_info_text_title),
                        leadingIconRes = R.drawable.outline_warning_amber_24,
                        leadingIconColor = electricityYellow
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.solar_power_edit_investment_info_text_message),
                        style = MaterialTheme.typography.labelMedium,
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
                        value = state.shareField,
                        label = {
                            Text(text = stringResource(R.string.solar_power_latest_investment_shares_title))
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
                        text = stringResource(R.string.solar_power_add_investment_shares_info_text),
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
                            text = "${context.getString(R.string.solar_power_add_investment_shares_info_text_cost_info_part_1)} $kwWithUnit ($priceWithUnit)",
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
                            headlineContent = { Text(text = stringResource(R.string.solar_power_latest_investment_date_title)) },
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
                        text = stringResource(R.string.solar_power_add_investment_date_info_text),
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
                            Text(text = stringResource(R.string.solar_power_add_investment_note_title))
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
                        text = stringResource(R.string.solar_power_add_investment_note_info_text),
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
                                viewModel.updatePVInvestment()
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