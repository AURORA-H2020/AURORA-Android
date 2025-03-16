package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.pvPlant.PVPlantData
import eu.inscico.aurora_app.model.pvProductionChart.ChartTimeRangeType
import eu.inscico.aurora_app.model.pvProductionChart.DisplayOption
import eu.inscico.aurora_app.model.user.PVInvestment
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components.PvProductionBarChart
import org.koin.androidx.compose.get

@Composable
fun PVPlantProductionWidget(
    yourProduction: Double,
    pvPlantProduction: Double,
    pvPlant: PVPlant,
    pvPlantData: List<PVPlantData>,
    userInvestments: List<PVInvestment>,
    unitService: UnitService = get(),
    onClick: (() -> Unit)? = null
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick?.invoke()
            },
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
            Row(Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp).weight(1f)) {
                    Text(
                        text = stringResource(R.string.solar_power_production_chart_option_your_production),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${unitService.getValueWithDecimalsAsString(value = yourProduction, decimals = 2, withLocalDecimalPoint = true)} kW",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.solar_power_production_chart_option_total_production),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${unitService.getValueWithDecimalsAsString(value = pvPlantProduction, decimals = 2, withLocalDecimalPoint = true)} kW",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(modifier = Modifier.weight(2f)) {
                    PvProductionBarChart(
                        modifier = Modifier
                            .fillMaxSize(),
                        pvPlant = pvPlant,
                        pvPlantData = pvPlantData,
                        userInvestments = userInvestments,
                        isSimpleView = true,
                        timeRangeType = ChartTimeRangeType.PAST_30_DAYS,
                        productionOption = DisplayOption.TOTAL_PRODUCTION,
                    )

                }
        }
    }
}