package eu.inscico.aurora_app.ui.screens.home.consumptions.consumptionDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.DistrictHeatingSource.Companion.getDisplayNameRes
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType.Companion.getDisplayNameRes
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.PublicVehicleOccupancy.Companion.getDisplayRes
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.getDisplayNameRes
import eu.inscico.aurora_app.utils.CalendarUtils


@Composable
fun TransportationConsumptionDetails(
    consumption: Consumption.TransportationConsumption
){

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(16.dp))) {

            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.home_consumptions_type_transportation_title)) },
                trailingContent = {
                    Text(
                        text = "${String.format("%.1f", consumption.value)} km",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            Divider()

            val carbonEmissionText = if (consumption.carbonEmissions != null) {
                "${String.format("%.1f", consumption.carbonEmissions)} kWh"
            } else {
                "- kWh"
            }

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

        Spacer(modifier = Modifier.height(16.dp))


        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(16.dp))) {

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

        Spacer(modifier = Modifier.height(16.dp))


        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(16.dp))) {

            if (consumption.description?.isNotEmpty() == true) {
                ListItem(headlineContent = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = consumption.description
                    )
                },)

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(16.dp))) {

            val createdText = if (consumption.createdAt != null) {
                CalendarUtils.toDateString(consumption.createdAt)
            } else {
                "-"
            }

            val updatedText = if (consumption.updatedAt != null) {
                CalendarUtils.toDateString(consumption.updatedAt)
            } else {
                "-"
            }

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