package eu.inscico.aurora_app.ui.screens.home.consumptions

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption.AddElectricityConsumption
import eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption.AddHeatingConsumption
import eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption.AddTransportationConsumption
import eu.inscico.aurora_app.ui.screens.home.consumptions.consumptionDetails.ElectricityConsumptionDetails
import eu.inscico.aurora_app.ui.screens.home.consumptions.consumptionDetails.HeatingConsumptionDetails
import eu.inscico.aurora_app.ui.screens.home.consumptions.consumptionDetails.TransportationConsumptionDetails
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConsumptionDetailScreen(
    viewModel: ConsumptionDetailViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get()
){

    val context = LocalContext.current

    val consumption = viewModel.selectedConsumption.observeAsState()

    val editModeOn = remember {
        mutableStateOf(false)
    }

    val editIconRes = if(editModeOn.value){
        R.drawable.outline_edit_off_24
    } else {
        R.drawable.outline_edit_24
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val consumptionNameRes = when(consumption.value){
            is Consumption.ElectricityConsumption -> R.string.home_consumptions_type_electricity_title
            is Consumption.HeatingConsumption -> R.string.home_consumptions_type_heating_title
            is Consumption.TransportationConsumption -> R.string.home_consumptions_type_transportation_title
            null -> R.string.no_value_to_display
        }


        AppBar(
            title = stringResource(id = consumptionNameRes),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabHome?.popBackStack()
            },
            actionButton = {
                Row() {
                    Image(
                        painter = painterResource(id = editIconRes),
                        modifier = Modifier
                            .size(42.dp)
                            .padding(horizontal = 7.dp)
                            .clickable {
                                editModeOn.value = !editModeOn.value
                            },
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.outline_delete_outline_24),
                        modifier = Modifier
                            .size(42.dp)
                            .padding(horizontal = 7.dp)
                            .clickable {
                                userFeedbackService.showDialog(
                                    message = context.getString(R.string.dialog_consumption_delete_title),
                                    confirmButtonText = context.getString(R.string.delete),
                                    confirmButtonCallback = {
                                        if (consumption.value != null) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val result =
                                                    viewModel.deleteConsumption(consumption.value!!)
                                                when (result) {
                                                    is TypedResult.Failure -> {
                                                        // TODO:
                                                    }
                                                    is TypedResult.Success -> {
                                                        withContext(Dispatchers.Main){
                                                            navigationService.navControllerTabHome?.popBackStack()
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            // TODO:
                                        }
                                    }
                                )
                            },
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error)
                    )
                }
            }
        )

        if(editModeOn.value){
            when(consumption.value){
                is Consumption.ElectricityConsumption -> {
                    AddElectricityConsumption(initialValues = consumption.value as Consumption.ElectricityConsumption)
                }
                is Consumption.HeatingConsumption -> {
                    AddHeatingConsumption(initialValue = consumption.value as Consumption.HeatingConsumption)
                }
                is Consumption.TransportationConsumption -> {
                    AddTransportationConsumption(initialValues = consumption.value as Consumption.TransportationConsumption)
                }
                null -> {}
            }
        } else {
            when(consumption.value){
                is Consumption.ElectricityConsumption -> {
                    ElectricityConsumptionDetails(consumption.value as Consumption.ElectricityConsumption)
                }
                is Consumption.HeatingConsumption -> {
                    HeatingConsumptionDetails(consumption.value as Consumption.HeatingConsumption)
                }
                is Consumption.TransportationConsumption -> {
                    TransportationConsumptionDetails(consumption.value as Consumption.TransportationConsumption)
                }
                null -> {}
            }

        }
    }

}