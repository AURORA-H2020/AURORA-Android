package eu.inscico.aurora_app.ui.screens.home.consumptions.consumptionDetails

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
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.getDisplayNameRes
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.utils.UnitUtils


@Composable
fun TransportationConsumptionDetails(
    consumption: Consumption.TransportationConsumption
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
                        text = UnitUtils.getConvertedDistanceWithUnit(consumption.value, locale = LocalConfiguration.current.locales[0], decimals = 1),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            Divider()

            val carbonEmissionText = if (consumption.carbonEmissions != null) {
                "${String.format("%.1f", consumption.carbonEmissions)} kWh"
            } else {
                null
            }

            if (carbonEmissionText != null) {
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
                            "dd.MM.yyyy, HH:mm"
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            if(consumption.transportation.dateOfTravelEnd != null){

                Divider()

                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_transportation_end_of_travel_title)) },
                    trailingContent = {
                        Text(
                            text = CalendarUtils.toDateString(
                                consumption.transportation.dateOfTravelEnd,
                                "dd.MM.yyyy, HH:mm"
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
            CalendarUtils.toDateString(consumption.createdAt)
        } else {
            null
        }

        val updatedText = if (consumption.updatedAt != null) {
            CalendarUtils.toDateString(consumption.updatedAt)
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

                    Divider()
                }

                if (updatedText != null) {
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