package eu.inscico.aurora_app.ui.components.consumptionSummery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptionSummary.EnergyLabel
import eu.inscico.aurora_app.model.consumptionSummary.EnergyLabel.Companion.getLabelColor
import eu.inscico.aurora_app.model.consumptionSummary.EnergyLabel.Companion.getLabelNameRes
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.ConsumptionType.Companion.getDisplayNameRes
import eu.inscico.aurora_app.model.consumptions.ConsumptionType.Companion.getIconRes

@Composable
fun ConsumptionSummaryLabel(
    year: Int,
    value: Double?,
    consumptionType: ConsumptionType,
    label: EnergyLabel?,
    isCarbonEmission: Boolean = true,
) {

    val context = LocalContext.current

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = consumptionType.getDisplayNameRes()),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val valueText = if(isCarbonEmission){
                context.getString(
                    R.string.home_your_carbon_emissions_labels_value_produced_with_year_title,
                    String.format("%.1f", value ?: 0F),
                    year
                )
            } else {
                context.getString(
                    R.string.home_your_carbon_emissions_labels_value_used_with_year_title,
                    String.format("%.1f", value ?: 0F),
                    year
                )
            }

            Text(
                text = valueText,
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
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                enabled = label != null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = label?.getLabelColor() ?: MaterialTheme.colorScheme.surfaceVariant),
                onClick = {
                    // nothing
                }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.2F)
                    ) {

                        Image(
                            painter = painterResource(id = consumptionType.getIconRes()),
                            modifier = Modifier
                                .padding(horizontal = 7.dp),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(textColor)
                        )

                        Text(
                            text = "|",
                            color = textColor,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.weight(0.8F)
                    ) {

                        Text(
                            text = stringResource(id = label?.getLabelNameRes() ?: R.string.home_your_carbon_emissions_label_empty_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}