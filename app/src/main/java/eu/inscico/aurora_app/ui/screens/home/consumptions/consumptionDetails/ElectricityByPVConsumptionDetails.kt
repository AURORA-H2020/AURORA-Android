package eu.inscico.aurora_app.ui.screens.home.consumptions.consumptionDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ElectricitySource
import eu.inscico.aurora_app.model.consumptions.ElectricitySource.Companion.getDisplayName
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.user.PVInvestment
import eu.inscico.aurora_app.services.firebase.PVPlantsService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.services.navigation.NavTab
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.utils.CalendarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get

@Composable
fun ElectricityByPVConsumptionDetails(
    consumption: Consumption.ElectricityConsumption,
    navigationService: NavigationService = get(),
    unitService: UnitService = get(),
    userService: UserService = get(),
    pvPlantsService: PVPlantsService = get()
) {

    val context = LocalContext.current
    val config = LocalConfiguration.current

    val pvInvestment = remember { mutableStateOf<PVInvestment?>(null) }
    val pvPlant = remember { mutableStateOf<PVPlant?>(null) }

    LaunchedEffect(Unit) {
        val currentPvInvestment =
            userService.pvInvestmentsForUser.value?.find { it.id == consumption.generatedByPvInvestmentId }
        pvInvestment.value = currentPvInvestment

        val pvPlantFromInvestment = pvPlantsService.pvPlantsFlow.value?.firstOrNull { it.id == pvInvestment.value?.pvPlant }
        pvPlant.value = pvPlantFromInvestment
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(16.dp))

        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    modifier = Modifier.padding(8.dp).size(32.dp),
                    tint = electricityYellow,
                    painter = painterResource(id = R.drawable.outline_solar_power_24),
                    contentDescription = ""
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = "This entry was created by your PV investment. It will update automatically with the latest data.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
            pvInvestment.value?.id?.let {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        navigationService.switchTabTo(NavTab.Photovoltaic) {
                            navigationService.toPhotovoltaicEditInvestment(it)
                        }
                    }) {
                        Text(
                            text = stringResource(id = R.string.consumption_detail_pv_investment_electricity_manage_entry_button_title),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(16.dp))
        ) {

            val formattedConsumptionValue = unitService.getValueInUserPreferredNumberFormat(
                config,
                String.format("%.1f", consumption.value).replace(",", ".").toDouble()
            )
            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.home_consumptions_type_electricity_pv_title)) },
                trailingContent = {
                    Text(
                        text = "$formattedConsumptionValue kWh",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            val carbonEmissionText = if (consumption.carbonEmissions != null) {
                unitService.getConvertedWeightWithUnit(config, consumption.carbonEmissions, 1)
            } else {
                null
            }

            if (carbonEmissionText != null) {
                Divider()

                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_carbon_emissions_title)) },
                    trailingContent = {
                        Text(
                            text = carbonEmissionText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }

            val plantInstallationText = pvPlant.value?.name

            if (plantInstallationText != null) {
                Divider()

                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.consumption_detail_pv_installation_site_label)) },
                    trailingContent = {
                        Text(
                            text = plantInstallationText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(16.dp))
        ) {

            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.electricity_source_title)) },
                trailingContent = {
                    Text(
                        text = stringResource(R.string.consumption_detail_pv_investment_electricity_source_title),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            Divider()

            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_form_begin_title)) },
                trailingContent = {
                    Text(
                        text = CalendarUtils.toDateString(
                            consumption.electricity.startDate,
                            unitService.getDateFormat(config)
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            Divider()

            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_form_end_title)) },
                trailingContent = {
                    Text(
                        text = CalendarUtils.toDateString(
                            consumption.electricity.endDate,
                            unitService.getDateFormat(config)
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

        }

        if (consumption.description?.isNotEmpty() == true) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
            ) {

                ListItem(
                    headlineContent = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = consumption.description
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val updatedText = if (consumption.updatedAt != null) {
            CalendarUtils.toDateString(consumption.updatedAt, unitService.getDateFormat(config))
        } else {
            null
        }

        if (updatedText != null) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
            ) {

                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.updated)) },
                    trailingContent = {
                        Text(
                            text = updatedText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )

            }
        }
    }

}