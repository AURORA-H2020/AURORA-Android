package eu.inscico.aurora_app.ui.screens.home.consumptions.consumptionDetails

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.TransportationType
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.getDisplayNameRes
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.services.shared.UnitService
import org.koin.androidx.compose.get


@Composable
fun TransportationConsumptionDetails(
    consumption: Consumption.TransportationConsumption,
    unitService: UnitService = get()
) {

    val context = LocalContext.current
    val config = LocalConfiguration.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Log.e("FIREBASEHELP", consumption.id)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(16.dp))
        ) {

            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.home_consumptions_type_transportation_title)) },
                trailingContent = {
                    Text(
                        text = unitService.getConvertedDistanceWithUnit(
                            config,
                            consumption.value,
                            decimals = 1
                        ),
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
            val energyExpendedText = if (consumption.energyExpended != null) {
                "${
                    unitService.getValueInUserPreferredNumberFormat(
                        config,
                        consumption.energyExpended,
                        1
                    )
                } ${
                    stringResource(
                        id = R.string.home_your_carbon_emissions_bar_chart_label_energy_expended_title
                    )
                }"
            } else {
                null
            }

            if (energyExpendedText != null) {
                Divider()

                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.consumption_detail_energy_usage_label)) },
                    trailingContent = {
                        Text(
                            text = energyExpendedText,
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
                headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_transportation_start_of_travel_title)) },
                trailingContent = {
                    Text(
                        text = CalendarUtils.toDateString(
                            consumption.transportation.dateOfTravel,
                            unitService.getDateFormat(config, true)
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            if (consumption.transportation.dateOfTravelEnd != null) {

                Divider()

                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_transportation_end_of_travel_title)) },
                    trailingContent = {
                        Text(
                            text = CalendarUtils.toDateString(
                                consumption.transportation.dateOfTravelEnd,
                                unitService.getDateFormat(config, true)
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }

            Divider()

            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_transportation_type_title)) },
                trailingContent = {
                    Text(
                        text = stringResource(id = consumption.transportation.transportationType.getDisplayNameRes()),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )


            val occupancyText = when {
                consumption.transportation.publicVehicleOccupancy != null -> consumption.transportation.publicVehicleOccupancy.getDisplayName(
                    context
                )

                consumption.transportation.privateVehicleOccupancy != null -> "${consumption.transportation.privateVehicleOccupancy}"
                else -> null
            }

            if (occupancyText != null) {
                Divider()
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_transportation_public_occupancy_title)) },
                    trailingContent = {
                        Text(
                            text = occupancyText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }

            if (consumption.transportation.transportationType == TransportationType.ELECTRIC_CAR
                || consumption.transportation.transportationType == TransportationType.HYBRID_CAR
                || consumption.transportation.transportationType == TransportationType.FUEL_CAR
                || consumption.transportation.transportationType == TransportationType.MOTORCYCLE
                || consumption.transportation.transportationType == TransportationType.ELECTRIC_MOTORCYCLE
            ) {
                val fuelConsumptionText = if(
                    consumption.transportation.transportationType == TransportationType.ELECTRIC_CAR
                    || consumption.transportation.transportationType == TransportationType.ELECTRIC_MOTORCYCLE){
                        unitService.getConvertedKWhPerDistanceWithUnit(config, consumption.transportation.fuelConsumption, decimals = 1)
                    } else {
                        unitService.getConvertedVolumePerDistanceWithUnit(config, consumption.transportation.fuelConsumption, decimals = 1)
                }
                Divider()
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_transportation_fuel_consumption_title)) },
                    trailingContent = {
                        Text(
                            text = fuelConsumptionText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
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

        val createdText = if (consumption.createdAt != null) {
            CalendarUtils.toDateString(consumption.createdAt, unitService.getDateFormat(config))
        } else {
            null
        }

        val updatedText = if (consumption.updatedAt != null) {
            CalendarUtils.toDateString(consumption.updatedAt, unitService.getDateFormat(config))
        } else {
            null
        }

        if (createdText != null || updatedText != null) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
            ) {

                if (createdText != null) {
                    ListItem(
                        headlineContent = { Text(text = stringResource(id = R.string.created)) },
                        trailingContent = {
                            Text(
                                text = createdText,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }

                if (updatedText != null) {
                    Divider()
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

            if (consumption.generatedByRecurringConsumptionId != null) {

                Text(
                    text = stringResource(id = R.string.consumption_detail_recurring_consumption_automatically_generated_text),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }

}