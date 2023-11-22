package eu.inscico.aurora_app.ui.components.consumptionSummery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptionSummary.EnergyLabel
import eu.inscico.aurora_app.model.consumptionSummary.EnergyLabel.Companion.getLabelColor
import eu.inscico.aurora_app.model.consumptionSummary.EnergyLabel.Companion.getLabelNameRes
import eu.inscico.aurora_app.services.shared.UnitService
import org.koin.androidx.compose.get

@Composable
fun DashboardConsumptionSummaryLabel(
    unitService: UnitService = get(),
    carbonValue: Double,
    carbonLabel: EnergyLabel,
    energyValue: Double,
    energyLabel: EnergyLabel,
    onLabelClicked: (isEnergyLabelClicked: Boolean) -> Unit,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val carbonTextColor = when (carbonLabel) {
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

    val energyTextColor = when (energyLabel) {
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(carbonLabel.getLabelColor()).weight(0.49F).clickable {
                    onLabelClicked.invoke(false)
                }
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = carbonLabel.getLabelNameRes()),
                style = MaterialTheme.typography.labelMedium,
                color = carbonTextColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.home_add_consumption_carbon_emissions_title),
                style = MaterialTheme.typography.labelSmall,
                color = carbonTextColor
            )

            Text(
                text = context.getString(
                    R.string.home_your_carbon_emissions_labels_overall_produced_value_title,
                    unitService.getConvertedWeightWithUnit(config = configuration, weightInKg = carbonValue, decimals = 1),
                ),
                style = MaterialTheme.typography.labelSmall,
                color = carbonTextColor
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(energyLabel.getLabelColor()).weight(0.49F).clickable {
                    onLabelClicked.invoke(true)
                }
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = energyLabel.getLabelNameRes()),
                style = MaterialTheme.typography.labelMedium,
                color = energyTextColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.home_your_carbon_emissions_labels_tab_energy_used_title),
                style = MaterialTheme.typography.labelSmall,
                color = energyTextColor,

            )

            val usedEnergyValueFormatted = String.format("%.1f", energyValue).replace(",", ".").toDouble()
            Text(
                text = context.getString(
                    R.string.home_your_carbon_emissions_labels_overall_used_value_title,
                    unitService.getValueInCorrectNumberFormat(configuration, usedEnergyValueFormatted)
                ),
                style = MaterialTheme.typography.labelSmall,
                color = energyTextColor,

            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


