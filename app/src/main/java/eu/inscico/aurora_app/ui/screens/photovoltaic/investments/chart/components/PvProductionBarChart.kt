package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.axis.axisGuidelineComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.scroll.ChartScrollSpec
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.scroll.AutoScrollCondition
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.pvPlant.PVPlantData
import eu.inscico.aurora_app.model.pvProductionChart.ChartTimeRangeType
import eu.inscico.aurora_app.model.pvProductionChart.DisplayOption
import eu.inscico.aurora_app.model.user.PVInvestment
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.ui.theme.investmentResultGreen
import eu.inscico.aurora_app.utils.CalendarUtils
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun PvProductionBarChart(
    pvPlant: PVPlant,
    pvPlantData: List<PVPlantData>,
    userInvestments: List<PVInvestment>,
    timeRangeType: ChartTimeRangeType,
    productionOption: DisplayOption,
    modifier: Modifier = Modifier,
    isSimpleView: Boolean = false,
    unitService: UnitService = get(),
    viewModel: PvProductionBarChartViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val config = LocalConfiguration.current
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.getReferenceTimeForDateParsing(pvPlantData)
        viewModel.updateState(pvPlant = pvPlant, userInvestments = userInvestments, pvPlantData = pvPlantData)
    }

    val xAxisValueFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom> =
        AxisValueFormatter { value, chartValue ->

            val realTimestamp = state.referenceTime + (value * state.millisPerDay).toLong()

            val date = Calendar.getInstance()
            date.timeInMillis = realTimestamp
            val help = CalendarUtils.toDateString(date, unitService.getDateFormat(config, withYear = false))

            val index = value.toInt()
            if(chartValue.chartEntryModel.entries.first().size < 10){
                help
            } else {
                if (index % 4 == 0) {
                    help
                } else {
                    ""
                }
            }
        }

    val yAxisValueFormatter: AxisValueFormatter<AxisPosition.Vertical.Start> =
        AxisValueFormatter { value, _ ->
            val convertedValue = value

            if (convertedValue > 2F) {
                String.format("%d", convertedValue.roundToInt())
            } else if (convertedValue > 1F) {
                String.format("%.1f", convertedValue)
            } else if (convertedValue > 0.1) {
                String.format("%.2f", convertedValue)
            } else if (convertedValue == 0F) {
                String.format("%.0f", convertedValue)
            } else {
                String.format("%.3f", convertedValue)
            }
        }

    val yAxisName =
        stringResource(id = R.string.home_your_carbon_emissions_bar_chart_label_energy_expended_title)

    val labelTextComponent = TextComponent.Builder()
    labelTextComponent.color = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    labelTextComponent.padding = MutableDimensions(horizontalDp = 4F, verticalDp = 0F)
    labelTextComponent.textSizeSp = 9F

    val axisTextComponent = TextComponent.Builder()
    axisTextComponent.color = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    axisTextComponent.padding = MutableDimensions(horizontalDp = 0F, verticalDp = 4F)
    axisTextComponent.textSizeSp = 9f


    val guidelineTextComponent = TextComponent.Builder()
    guidelineTextComponent.color = MaterialTheme.colorScheme.outlineVariant.toArgb()

    Column() {


        val chartScrollSpec = ChartScrollSpec(
            initialScroll = InitialScroll.End,
            isScrollEnabled = true,
            autoScrollCondition = AutoScrollCondition.OnModelSizeIncreased,
            autoScrollAnimationSpec = TweenSpec(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )

        val chart = when (timeRangeType) {
            ChartTimeRangeType.PAST_30_DAYS -> {
                columnChart(
                    spacing = 0.5.dp,
                    columns = listOf(
                        LineComponent(thicknessDp = 1F, color = investmentResultGreen.toArgb()),
                    ),
                )
            }

            ChartTimeRangeType.SINCE_INVESTMENT -> {
                LineChart(
                    lines = listOf(
                        LineChart.LineSpec(
                            point = LineComponent(
                                color = investmentResultGreen.toArgb(),
                                shape = com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape,
                                thicknessDp = 4f
                            ),
                            pointSizeDp = 4f,
                            lineThicknessDp = 2f,
                            lineColor = investmentResultGreen.toArgb()
                        )
                    )
                ).apply {
                    spacingDp = 0f
                }
            }
        }

        if(isSimpleView){
            Chart(
                modifier = modifier,
                chart = chart,
                model = viewModel.getBarChartDataForProductionType(
                    productionType = productionOption,
                    pvPlantData = pvPlantData,
                    timeRangeType = timeRangeType
                )
            )

        } else {

            Chart(
                chartScrollSpec = chartScrollSpec,
                modifier = modifier,
                chart = chart,
                model = viewModel.getBarChartDataForProductionType(
                    productionType = productionOption,
                    pvPlantData = pvPlantData,
                    timeRangeType = timeRangeType
                ),
                startAxis = startAxis(
                    label = labelTextComponent.build(),
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
                    label = axisTextComponent.build().apply { padding = MutableDimensions(horizontalDp = 4f, verticalDp = 0f)},
                    guideline = axisGuidelineComponent(MaterialTheme.colorScheme.outlineVariant),
                    valueFormatter = xAxisValueFormatter,
                    labelRotationDegrees = 90f,
                    sizeConstraint = Axis.SizeConstraint.TextWidth("dd. MMM")
                ),
            )
        }
    }
}