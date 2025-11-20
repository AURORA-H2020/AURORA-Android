package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.pvProductionChart.ChartTimeRangeType
import eu.inscico.aurora_app.model.pvProductionChart.DisplayOption
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components.PvProductionBarChart
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components.PvProductionInfoDialog
import eu.inscico.aurora_app.utils.CalendarUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PvProductionChartScreen(
    viewModel: PvProductionChartViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    unitService: UnitService = get()
) {

    val context = LocalContext.current
    val config = LocalConfiguration.current
    val state = viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = if (state.value.selectedDisplayOption == DisplayOption.TOTAL_PRODUCTION) {
            1
        } else {
            0
        }
    )

    val showInfoDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchInvestmentsForUser()
        viewModel.fetchPvPlantForUserCity()
        viewModel.getFirstProductionValueDate()
    }

    LaunchedEffect(state.value.selectedChartTimeRange, state.value.selectedDisplayOption) {
        viewModel.getFirstProductionValueDate()
        delay(300)
        viewModel.getSummedTotalProductionValue()
    }

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.solar_power_production_chart_title),
                hasBackNavigation = true,
                backNavigationCallback = {
                    navigationService.navControllerTabPhotovoltaic?.popBackStack()
                },
                actionButton = {
                    Row(modifier = Modifier.clickable {
                        showInfoDialog.value = true
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_question_mark_24),
                            modifier = Modifier
                                .size(42.dp)
                                .padding(horizontal = 7.dp),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val options = listOf(
                    SpinnerItem.Entry(
                        name = context.getString(R.string.solar_power_production_chart_time_range_past_30_days_title),
                        data = ChartTimeRangeType.PAST_30_DAYS
                    ),
                    SpinnerItem.Entry(
                        name = context.getString(R.string.solar_power_production_chart_time_range_since_investment_title),
                        data = ChartTimeRangeType.SINCE_INVESTMENT
                    )
                )

                SpinnerFormEntry(
                    title = stringResource(id = R.string.solar_power_production_chart_time_range_title),
                    allEntries = options,
                    selectedEntry = options.first(),
                    isRoundedDesign = false,
                    callback = { entry, section ->
                        viewModel.updateChartTimeRange(entry.data as ChartTimeRangeType)
                    })

                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {

                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            viewModel.updateChartDisplayOption(DisplayOption.YOUR_PRODUCTION)
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        text = {
                            Text(text = stringResource(id = R.string.solar_power_production_chart_option_your_production))
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                    )

                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            viewModel.updateChartDisplayOption(DisplayOption.TOTAL_PRODUCTION)
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        text = {
                            Text(text = stringResource(id = R.string.solar_power_production_chart_option_total_production))
                        },
                    )
                }

                HorizontalPager(
                    count = 2,
                    state = pagerState,
                ) { tabIndex ->
                    when (tabIndex) {
                        0 -> {}
                        1 -> {}
                    }
                }

                LazyColumn(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    state = rememberLazyListState(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    item {

                        Spacer(Modifier.height(16.dp))

                        val firstInvestment = state.value.firstProductionValueDate
                        val investmentSum = state.value.summedProductionValue

                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {

                            val label = "${context.getString(R.string.since)} ${CalendarUtils.toDateString(calendarDay = firstInvestment, unitService.getDateFormat(config))}"

                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.onSecondary
                            )

                            Text(
                                text = "${unitService.getValueWithDecimalsAsString(value = investmentSum, decimals = 0, withLocalDecimalPoint = true)} kW",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        if (state.value.pvPlant != null && state.value.plantData != null && state.value.userInvestments != null) {
                            PvProductionBarChart(
                                modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                                pvPlant = state.value.pvPlant!!,
                                pvPlantData = state.value.plantData!!,
                                userInvestments = state.value.userInvestments!!,
                                timeRangeType = state.value.selectedChartTimeRange,
                                productionOption = state.value.selectedDisplayOption,
                            )
                        }
                    }
                }
            }
        }
)
    PvProductionInfoDialog(
        plantId = state.value.pvPlant?.plantId,
        showDialog = showInfoDialog
    )
}