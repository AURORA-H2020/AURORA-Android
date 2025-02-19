package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.all_investments.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.model.country.Country
import eu.inscico.aurora_app.model.user.PVInvestment
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.utils.CalendarUtils
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InvestmentListItem(
    investment: PVInvestment,
    userCountry: Country?,
    unitService: UnitService = get(),
    onClick: (() -> Unit)? = null
) {

    val config = LocalConfiguration.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onClick?.invoke()
        }) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Investition",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start
            )

            val countryCurrency = userCountry?.currencyCode ?: "EUR"
            val currencySymbol = unitService.getCurrencyUnitByLocale(countryCurrency)
            Text(
                text = "${investment.investmentPrice ?: 0} $currencySymbol",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Capacity",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Start
            )

            Text(
                text = "${investment.investmentCapacity ?: 0} W",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Share",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Start
            )

            Text(
                text = "${investment.share}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Investment Date",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Start
            )

            Text(
                text = CalendarUtils.toDateString(
                    investment.investmentDate,
                    unitService.getDateFormat(config)
                ),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End
            )
        }
    }
}