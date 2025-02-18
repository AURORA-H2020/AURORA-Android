package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.axisGuidelineComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.endAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.ui.theme.heatingRed
import eu.inscico.aurora_app.ui.theme.mobilityBlue

@Composable
fun EnergyProductionChart() {
/*
    Chart(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        chart = columnChart(
            spacing = 1.dp,
            columns = listOf(
                LineComponent(thicknessDp = 8F, color = heatingRed.toArgb())
            ),//shape = Shapes.roundedCornerShape(topLeftPercent = 16, topRightPercent = 16),),),
            mergeMode = ColumnChart.MergeMode.Stack
        ),
        model = barChartData,
        endAxis = endAxis(
            label = axisTextComponent.build(),
            tick = null,
            tickLength = 0.dp,
            guideline = null,
            maxLabelCount = 5,
            title = yAxisName,
            titleComponent = labelTextComponent.build(),
            valueFormatter = yAxisValueFormatter
        ),
        bottomAxis = bottomAxis(
            tick = null,
            label = labelTextComponent.build(),
            guideline = axisGuidelineComponent(MaterialTheme.colorScheme.outlineVariant),
            valueFormatter = xAxisValueFormatter
        ),
    )


 */
}