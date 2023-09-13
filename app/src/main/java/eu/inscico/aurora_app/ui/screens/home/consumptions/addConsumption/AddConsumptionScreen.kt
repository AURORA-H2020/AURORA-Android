package eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.consumptions.AddConsumptionButton
import eu.inscico.aurora_app.ui.components.consumptions.AddConsumptionTypeSwitcher
import eu.inscico.aurora_app.utils.LocaleUtils
import org.koin.androidx.compose.get
import java.util.*

@Composable
fun AddConsumptionScreen(
    language: Locale = Locale.getDefault(),
    preSelected: ConsumptionType? = null,
    navigationService: NavigationService = get()
) {

    val context = LocalContext.current

    // set language, only necessary for screenshot ui tests
    LocaleUtils.updateLocale(context, language)

    val selectedConsumptionEntry = remember {
        mutableStateOf<ConsumptionType?>(preSelected)
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
            Log.e("addHeatingConsumption", "is not null")
            Spacer(modifier = Modifier.height(16.dp))
            AddConsumptionTypeSwitcher(selectedConsumptionEntry.value!!) {
                selectedConsumptionEntry.value = it
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (selectedConsumptionEntry.value) {
            ConsumptionType.ELECTRICITY -> {
                Log.e("addHeatingConsumption", "is selected")
                Column(
                    Modifier.verticalScroll(rememberScrollState()),
                ) {
                    AddElectricityConsumption()
                }
            }
            ConsumptionType.HEATING -> {
                Log.e("addHeatingConsumption", "is selected")
                Column(
                    Modifier.verticalScroll(rememberScrollState()),
                ) {
                    AddHeatingConsumption(language = language)
                }
            }
            ConsumptionType.TRANSPORTATION -> {
                Log.e("addHeatingConsumption", "is selected")
                Column(
                    Modifier.verticalScroll(rememberScrollState()),
                ) {
                    AddTransportationConsumption()
                }
            }
            null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    AddConsumptionButton(consumptionType = ConsumptionType.ELECTRICITY) {
                        selectedConsumptionEntry.value = it
                    }

                    Spacer(Modifier.height(16.dp))

                    AddConsumptionButton(modifier = Modifier.testTag("addHeatingConsumption"), consumptionType = ConsumptionType.HEATING) {
                        Log.e("addHeatingConsumption", "is selected")
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
