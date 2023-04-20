package eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.consumptions.AddConsumptionButton
import eu.inscico.aurora_app.ui.components.consumptions.AddConsumptionTypeSwitcher
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddConsumptionScreen(
    viewModel: AddConsumptionViewModel = koinViewModel(),
    navigationService: NavigationService = get()
) {

    val selectedConsumptionEntry = remember {
        mutableStateOf<ConsumptionType?>(null)
    }

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        AppBar(
            title = stringResource(id = R.string.home_add_consumption_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabHome?.popBackStack()
            }
        )

        if (selectedConsumptionEntry.value != null) {
            Spacer(modifier = Modifier.height(16.dp))
            AddConsumptionTypeSwitcher(selectedConsumptionEntry.value!!) {
                selectedConsumptionEntry.value = it
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (selectedConsumptionEntry.value) {
            ConsumptionType.ELECTRICITY -> {
                AddElectricityConsumption()
            }
            ConsumptionType.HEATING -> {
                AddHeatingConsumption()
            }
            ConsumptionType.TRANSPORTATION -> {
                AddTransportationConsumption()
            }
            null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    AddConsumptionButton(ConsumptionType.ELECTRICITY) {
                        selectedConsumptionEntry.value = it
                    }

                    Spacer(Modifier.height(16.dp))

                    AddConsumptionButton(consumptionType = ConsumptionType.HEATING) {
                        selectedConsumptionEntry.value = it
                    }

                    Spacer(Modifier.height(16.dp))

                    AddConsumptionButton(consumptionType = ConsumptionType.TRANSPORTATION) {
                        selectedConsumptionEntry.value = it
                    }
                }
            }
        }
    }
}
