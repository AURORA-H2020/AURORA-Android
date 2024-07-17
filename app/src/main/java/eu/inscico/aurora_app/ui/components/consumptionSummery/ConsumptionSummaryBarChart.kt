package eu.inscico.aurora_app.ui.components.consumptionSummery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.axisGuidelineComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.endAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptionSummary.ConsumptionSummary
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.ui.theme.heatingRed
import eu.inscico.aurora_app.ui.theme.mobilityBlue
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.services.shared.UnitService
import org.koin.androidx.compose.get
import java.util.*
import kotlin.math.roundToInt

@Composable
fun ConsumptionSummaryBarChart(
    summary: ConsumptionSummary,
    barChartData: ChartEntryModel,
    isCarbonEmission: Boolean,
    unitService: UnitService = get()
){

    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val xAxisValueFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom> =
        AxisValueFormatter { value, _ ->
            val month = Calendar.getInstance()
            month.set(Calendar.MONTH, (value - 1F).toInt())
            CalendarUtils.toDateString(month, "MMM")
        }

    val yAxisValueFormatter: AxisValueFormatter<AxisPosition.Vertical.End> =
        AxisValueFormatter { value, _ ->
            val convertedValue = if(isCarbonEmission){
                unitService.getWeightInUserPreferredUnit(configuration, weightInKg = value.toDouble()).toFloat()
            } else {
                value
            }

            if(convertedValue > 2F){
                String.format("%d", convertedValue.roundToInt())
            } else if(convertedValue > 1F){
                String.format("%.1f", convertedValue)
            } else if(convertedValue > 0.1){
                String.format("%.2f", convertedValue)
            } else if(convertedValue == 0F){
                String.format("%.0f", convertedValue)
            }
            else {
                String.format("%.3f", convertedValue)
            }
        }

    val yAxisName = if(isCarbonEmission){
        context.getString(
            R.string.home_your_carbon_emissions_bar_chart_label_carbon_emission_title,
            unitService.getUserPreferredWeightUnit(configuration),
        )
    } else {
        stringResource(id = R.string.home_your_carbon_emissions_bar_chart_label_energy_expended_title)
    }

    val labelTextComponent = TextComponent.Builder()
    labelTextComponent.color = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    labelTextComponent.padding = MutableDimensions(horizontalDp = 4F, verticalDp = 0F)
    labelTextComponent.textSizeSp = 9F

    val axisTextComponent = TextComponent.Builder()
    axisTextComponent.color = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    axisTextComponent.padding = MutableDimensions(horizontalDp = 4F, verticalDp = 0F)
    axisTextComponent.textSizeSp = 9F


    val guidelineTextComponent = TextComponent.Builder()
    guidelineTextComponent.color = MaterialTheme.colorScheme.outlineVariant.toArgb()


    Column(Modifier.padding(bottom = 24.dp)) {


        Chart(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            chart = columnChart(
                spacing = 1.dp,
                columns = listOf(
                    LineComponent(thicknessDp = 8F, color = heatingRed.toArgb()),
                    LineComponent(electricityYellow.toArgb(), thicknessDp = 8F),
                    LineComponent(mobilityBlue.toArgb(), thicknessDp = 8F,)
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

        ConsumptionSummaryBarChartLegend()
    }
}