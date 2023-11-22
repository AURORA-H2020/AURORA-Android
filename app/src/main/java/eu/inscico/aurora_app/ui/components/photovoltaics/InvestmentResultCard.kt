package eu.inscico.aurora_app.ui.components.photovoltaics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.photovoltaics.PhotovoltaicInvestmentResult
import eu.inscico.aurora_app.ui.components.FormEntry
import eu.inscico.aurora_app.ui.components.FormEntryType
import eu.inscico.aurora_app.ui.theme.investmentResultGreen
import eu.inscico.aurora_app.ui.theme.investmentResultOrange
import eu.inscico.aurora_app.services.shared.UnitService
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentResultCard(
    currency: String,
    investmentResult: PhotovoltaicInvestmentResult,
    resetCallback: () -> Unit,
    learnMoreCallback: () -> Unit,
    unitService: UnitService = get()
) {

    val context = LocalContext.current

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(16.dp))
        ) {

            FormEntry(
                title = stringResource(id = R.string.solar_power_investment_result_your_investment_title),
                formEntryType = FormEntryType.TEXT_INPUT,
                initialItem = "${investmentResult.amount} $currency",
                readOnly = true
            )

            Divider()

            FormEntry(
                title = stringResource(id = R.string.solar_power_investment_result_annual_energy_production_title),
                formEntryType = FormEntryType.TEXT_INPUT,
                initialItem = "${String.format("%.1f", investmentResult.producedEnergy.value)} kWh",
                readOnly = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        InvestmentResultLabel(
            value = investmentResult.normalCarbonEmissions.value,
            infoTextRes = R.string.solar_power_investment_result_conventional_info_text,
            labelTextRes = R.string.solar_power_investment_result_conventional_title,
            buttonColor = investmentResultOrange
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.outline_remove_circle_outline_24),
            modifier = Modifier
                .size(42.dp)
                .padding(horizontal = 7.dp),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(8.dp))

        InvestmentResultLabel(
            value = investmentResult.carbonEmissions.value,
            infoTextRes = R.string.solar_power_investment_result_photovoltaics_info_text,
            labelTextRes = R.string.solar_power_investment_result_photovoltaics_title,
            buttonColor = investmentResultOrange
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.baseline_pause_circle_outline_24),
            modifier = Modifier
                .size(42.dp)
                .padding(horizontal = 7.dp),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(8.dp))

        InvestmentResultLabel(
            value = investmentResult.carbonEmissionsReduction.value,
            infoTextRes = R.string.solar_power_investment_result_reduction_info_text,
            labelTextRes = R.string.solar_power_investment_result_reduction_title,
            buttonColor = investmentResultGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                modifier = Modifier.padding(vertical = 16.dp),
                onClick = {
                    resetCallback.invoke()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
            ) {
                Text(text = stringResource(id = R.string.reset), style = MaterialTheme.typography.labelSmall)
            }
            Button(
                modifier = Modifier.padding(vertical = 16.dp),
                onClick = {
                    learnMoreCallback.invoke()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
            ) {
                Text(style = MaterialTheme.typography.labelSmall, text = stringResource(id = R.string.solar_power_investment_result_learn_more_on_our_website_title))
            }
        }
    }
}