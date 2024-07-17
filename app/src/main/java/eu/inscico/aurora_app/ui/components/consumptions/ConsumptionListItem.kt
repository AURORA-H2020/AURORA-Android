package eu.inscico.aurora_app.ui.components.consumptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ConsumptionType.Companion.getDisplayName
import eu.inscico.aurora_app.ui.theme.*
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.services.shared.UnitService
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumptionListItem(
    unitService: UnitService = get(),
    consumption: Consumption,
    callback: (Consumption) -> Unit,
) {

    val context = LocalContext.current
    val config = LocalConfiguration.current

    val headlineText = when (consumption) {
        is Consumption.ElectricityConsumption -> consumption.category.getDisplayName(context)
        is Consumption.HeatingConsumption -> consumption.category.getDisplayName(context)
        is Consumption.TransportationConsumption -> consumption.category.getDisplayName(context)
    }

    val consumptionTime = when (consumption) {
        is Consumption.ElectricityConsumption -> "${CalendarUtils.toDateString(consumption.electricity.startDate, unitService.getDateFormat(config))} - ${CalendarUtils.toDateString(consumption.electricity.endDate, unitService.getDateFormat(config))}"
        is Consumption.HeatingConsumption -> "${CalendarUtils.toDateString(consumption.heating.startDate, unitService.getDateFormat(config))} - ${CalendarUtils.toDateString(consumption.heating.endDate, unitService.getDateFormat(config))}"
        is Consumption.TransportationConsumption -> {
            CalendarUtils.toDateString(consumption.transportation.dateOfTravel, unitService.getDateFormat(config, withTime = true))
        }
    }

    val description = when (consumption) {
        is Consumption.ElectricityConsumption -> consumption.description
        is Consumption.HeatingConsumption -> consumption.description
        is Consumption.TransportationConsumption -> consumption.description
    }

    val carbonEmissionText = when (consumption) {
        is Consumption.ElectricityConsumption -> unitService.getConvertedWeightWithUnit(config, consumption.carbonEmissions, decimals = 1)
        is Consumption.HeatingConsumption -> unitService.getConvertedWeightWithUnit(config, consumption.carbonEmissions, decimals = 1)
        is Consumption.TransportationConsumption -> unitService.getConvertedWeightWithUnit(config, consumption.carbonEmissions, decimals = 1)
    }

    val energyExpendedValue = when (consumption) {
        is Consumption.ElectricityConsumption -> "${String.format("%.0f", consumption.energyExpended)} kWh"
        is Consumption.HeatingConsumption -> "${String.format("%.0f", consumption.energyExpended)} kWh"
        is Consumption.TransportationConsumption -> unitService.getConvertedDistanceWithUnit(config, consumption.value, decimals = 1)
    }

    val red = heatingRed
    val yellow = electricityYellow
    val blue = mobilityBlue

    ListItem(
        modifier = Modifier.clickable {
            callback.invoke(consumption)
        },
        leadingContent = {
            when (consumption) {
                is Consumption.ElectricityConsumption -> {
                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    Color(yellow.value).copy(alpha = 0.2F),
                                    cornerRadius = CornerRadius(16.dp.toPx())
                                )
                            }
                            .size(30.dp)
                    ){
                        Image(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(6.dp),
                            painter = painterResource(id = R.drawable.outline_electric_bolt_24),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(color = yellow),
                        )
                    }
                }
                is Consumption.HeatingConsumption -> {
                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    Color(red.value).copy(alpha = 0.2F),
                                    cornerRadius = CornerRadius(16.dp.toPx())
                                )
                            }
                            .size(30.dp)
                    ){
                        Image(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(6.dp),
                            painter = painterResource(id = R.drawable.outline_local_fire_department_24),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(color = red),
                        )
                    }
                }
                is Consumption.TransportationConsumption -> {
                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    Color(blue.value).copy(alpha = 0.2F),
                                    cornerRadius = CornerRadius(16.dp.toPx())
                                )
                            }
                            .size(30.dp)
                    ){
                        Image(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(6.dp),
                            painter = painterResource(id = R.drawable.outline_directions_car_24),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(color = blue),
                        )
                    }
                }
            }
        },
        headlineContent = {
            Column {
                Text(text = headlineText)
                Text(
                    text = consumptionTime,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                if(description?.isNotEmpty() == true){
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = energyExpendedValue,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = carbonEmissionText,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary
                )

            }
        }
    )

}