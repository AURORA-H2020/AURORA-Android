package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.country.Country
import eu.inscico.aurora_app.model.user.PVInvestment
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.ui.components.ActionEntry
import eu.inscico.aurora_app.ui.components.DecoratedHeadline
import eu.inscico.aurora_app.utils.CalendarUtils
import org.koin.androidx.compose.get

@Composable
fun LatestInvestmentsWidget(
    latestInvestment: PVInvestment?,
    userCountry: Country?,
    onEditClicked: () -> Unit,
    onAddInvestmentClicked: () -> Unit,
    unitService: UnitService = get()
) {

    val config = LocalConfiguration.current

    if(latestInvestment != null) {

            DecoratedHeadline(
                headline = stringResource(R.string.solar_power_latest_investment_headline),
                leadingIconRes = R.drawable.outline_receipt_24,
                actionIconRes = R.drawable.outline_edit_24,
                onActionClicked = {
                    onEditClicked.invoke()
                }
            )

            Spacer(Modifier.height(8.dp))

        Column(
            Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .fillMaxSize()) {

            val countryCurrency = userCountry?.currencyCode ?: "EUR"
            val currencySymbol = unitService.getCurrencyUnitByLocale(countryCurrency)

            ActionEntry(
                title = stringResource(R.string.solar_power_latest_investment_investment_title),
                iconRes = R.drawable.outline_account_balance_wallet_24,
                isNavigation = false,
                titleColor = MaterialTheme.colorScheme.onSurface,
                optionalTrailingContent = {
                    Text(
                        text = "${unitService.getValueWithDecimalsAsString(value = latestInvestment.investmentPrice, decimals = 2, withLocalDecimalPoint = true)} $currencySymbol",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = TextStyle(
                            fontSize = 17.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            )

            Divider()

            ActionEntry(
                title = stringResource(R.string.solar_power_latest_investment_capacity_title),
                iconRes = R.drawable.outline_brightness_low_24,
                isNavigation = false,
                optionalTrailingContent = {
                    Text(
                        text = "${latestInvestment.investmentCapacity} kW",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = TextStyle(
                            fontSize = 17.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            )

            Divider()

            ActionEntry(
                title = stringResource(R.string.solar_power_latest_investment_share_title),
                iconRes = R.drawable.outline_border_all_24,
                isNavigation = false,
                optionalTrailingContent = {
                    Text(
                        text = "${latestInvestment.share}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = TextStyle(
                            fontSize = 17.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            )

            Divider()

            ActionEntry(
                title = stringResource(R.string.solar_power_latest_investment_date_title),
                iconRes = R.drawable.outline_date_range_24,
                isNavigation = false,
                optionalTrailingContent = {
                    Text(
                        text = CalendarUtils.toDateString(
                            latestInvestment.investmentDate,
                            unitService.getDateFormat(config)
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = TextStyle(
                            fontSize = 17.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            )
        }
    } else {

        DecoratedHeadline(
            headline = stringResource(R.string.solar_power_latest_investment_headline),
            leadingIconRes = R.drawable.outline_receipt_24,
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                onClick = {
                    onAddInvestmentClicked.invoke()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = stringResource(R.string.solar_power_add_investment_headline),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}