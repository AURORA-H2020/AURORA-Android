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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.DistrictHeatingSource.Companion.getDisplayNameRes
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType.Companion.getDisplayNameRes
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.services.shared.UnitService
import org.koin.androidx.compose.get
import kotlin.text.*


@Composable
fun HeatingConsumptionDetails(
    consumption: Consumption.HeatingConsumption,
    unitService: UnitService = get()
) {

    val config = LocalConfiguration.current

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

            val consumptionWithUnit = when(consumption.heating.heatingFuel){
                HeatingFuelType.BIOMASS,
                HeatingFuelType.LOCALLY_PRODUCED_BIOMASS,
                HeatingFuelType.FIREWOOD,
                HeatingFuelType.BUTANE -> {
                    unitService.getConvertedWeightWithUnit(config, consumption.value, 1)
                }
                HeatingFuelType.OIL,
                HeatingFuelType.LPG -> {
                    unitService.getUserPreferredVolumeWithUnit(config, consumption.value, 1)
                }
                HeatingFuelType.NATURAL_GAS,
                HeatingFuelType.GEO_THERMAL,
                HeatingFuelType.SOLAR_THERMAL,
                HeatingFuelType.DISTRICT,
                HeatingFuelType.ELECTRIC -> {
                    val formattedConsumptionValue = unitService.getValueInUserPreferredNumberFormat(config, String.format("%.1f", consumption.value).replace(",",".").toDouble())
                    "$formattedConsumptionValue kWh"
                }
            }

            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.home_consumptions_type_heating_title)) },
                trailingContent = {
                    Text(
                        text = consumptionWithUnit,
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
                "${unitService.getValueInUserPreferredNumberFormat(config, consumption.energyExpended, 1)} ${stringResource(
                    id = R.string.home_your_carbon_emissions_bar_chart_label_energy_expended_title
                )}"
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

            val costsText = if (consumption.heating.costs != null) {
                "${String.format("%.1f", consumption.heating.costs)} ${
                    unitService.getCurrencyUnit(
                        LocalConfiguration.current)}"
            } else {
                null
            }

            if (costsText != null) {
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_form_costs_title)) },
                    trailingContent = {
                        Text(
                            text = costsText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
                Divider()
            }

            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_form_people_in_household_title)) },
                trailingContent = {
                    Text(
                        text = consumption.heating.householdSize.toString(),
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
                        text = CalendarUtils.toDateString(consumption.heating.startDate, unitService.getDateFormat(config)),
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
                        text = CalendarUtils.toDateString(consumption.heating.endDate, unitService.getDateFormat(config)),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            Divider()

            ListItem(
                headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_heating_fuel_title)) },
                trailingContent = {
                    Text(
                        text = stringResource(id = consumption.heating.heatingFuel.getDisplayNameRes()),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )


            if (consumption.heating.districtHeatingSource != null) {
                Divider()

                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.home_add_consumption_district_heating_source_title)) },
                    trailingContent = {
                        Text(
                            text = stringResource(id = consumption.heating.districtHeatingSource.getDisplayNameRes()),
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
        }
    }

}