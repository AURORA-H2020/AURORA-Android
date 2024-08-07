package eu.inscico.aurora_app.ui.components.consumptionSummery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptionSummary.EnergyLabel
import eu.inscico.aurora_app.model.consumptionSummary.EnergyLabel.Companion.getLabelColor
import eu.inscico.aurora_app.model.consumptionSummary.EnergyLabel.Companion.getLabelNameRes
import eu.inscico.aurora_app.services.shared.UnitService
import org.koin.androidx.compose.get


@Composable
fun OverallConsumptionSummaryLabel(
    value: Double?,
    label: EnergyLabel?,
    year: Int,
    isCarbonEmissions: Boolean,
    unitService: UnitService = get()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val textColor = when (label) {
        EnergyLabel.A_PLUS,
        EnergyLabel.A,
        EnergyLabel.B,
        EnergyLabel.E,
        EnergyLabel.F,
        EnergyLabel.G -> Color.White
        EnergyLabel.C,
        EnergyLabel.D -> Color.Black
        null -> Color.White
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.home_your_carbon_emissions_labels_overall_title),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = year.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .defaultMinSize(minHeight = 80.dp),
                enabled = label != null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = label?.getLabelColor() ?: MaterialTheme.colorScheme.surfaceVariant),
                onClick = {
                    // nothing
                }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.weight(0.45F),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Text(
                            text = "$year",
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(id = label?.getLabelNameRes() ?: R.string.home_your_carbon_emissions_label_empty_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    Divider(modifier = Modifier.fillMaxHeight().padding(horizontal = 4.dp)
                        .width(1.dp), thickness = 2.dp, color = textColor)

                    Column(
                        modifier = Modifier.weight(0.45F),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {

                        val valueText = if (isCarbonEmissions) {
                            context.getString(
                                R.string.home_your_carbon_emissions_labels_overall_produced_value_title,
                                unitService.getConvertedWeightWithUnit(config = configuration, weightInKg = value, decimals = 1),
                            )
                        } else {
                            context.getString(
                                R.string.home_your_carbon_emissions_labels_overall_used_value_title,
                                unitService.getWeightInUserPreferredUnit(config = configuration, weightInKg = value, decimals = 1).toString(),
                            )
                        }

                        val descriptionText = if (isCarbonEmissions) {
                            stringResource(id = R.string.home_your_carbon_emissions_labels_overall_produced_title)
                        } else {
                            stringResource(id = R.string.home_your_carbon_emissions_labels_overall_used_title)
                        }

                        Text(
                            text = valueText,
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor
                        )

                        Text(
                            text = descriptionText,
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor
                        )
                    }
                }
            }
        }
    }

}