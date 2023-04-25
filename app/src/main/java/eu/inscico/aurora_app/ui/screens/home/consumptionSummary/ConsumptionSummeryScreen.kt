package eu.inscico.aurora_app.ui.screens.home.consumptionSummary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptionSummary.ConsumptionSummary
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.consumptionSummery.ConsumptionSummaryLabel
import eu.inscico.aurora_app.ui.components.consumptionSummery.ConsumptionSummaryBarChart
import eu.inscico.aurora_app.ui.components.consumptionSummery.OverallConsumptionSummaryLabel
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import eu.inscico.aurora_app.ui.theme.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ConsumptionSummaryScreen(
    navigationService: NavigationService = get(),
    viewModel: ConsumptionSummaryViewModel = koinViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)

    val allConsumptionSummaries = viewModel.allConsumptionSummariesLive.observeAsState()

    val selectedConsumptionSummary = remember {
        mutableStateOf(allConsumptionSummaries.value?.sortedByDescending { it.year }?.firstOrNull())
    }


    val allYearEntries = allConsumptionSummaries.value?.sortedByDescending { it.year }?.map {
        SpinnerItem.Entry(name = it.year.toString(), data = it)
    }


    val isCarbonEmissions = remember {
        mutableStateOf(true)
    }

    val isBarChartVisible = remember {
        mutableStateOf(false)
    }

    if (isCarbonEmissions.value) {
        isBarChartVisible.value = true
    } else {
        isBarChartVisible.value = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            title = stringResource(id = R.string.home_energy_lables_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabHome?.popBackStack()
            }
        )

        allYearEntries?.let {
            SpinnerFormEntry(
                title = stringResource(id = R.string.home_your_carbon_emissions_labels_year_title),
                allEntries = it,
                selectedEntry = it.firstOrNull(),
                isRoundedDesign = false,
                callback = { entry, section ->
                    selectedConsumptionSummary.value = entry.data as? ConsumptionSummary
                })
        }

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = {
                    isCarbonEmissions.value = true
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = {
                    Text(text = stringResource(id = R.string.home_your_carbon_emissions_labels_tab_carbon_emission_title))
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = {
                    isCarbonEmissions.value = false
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = {
                    Text(text = stringResource(id = R.string.home_your_carbon_emissions_labels_tab_energy_used_title))
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

        Column(

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ScrollableContent() {

                Spacer(modifier = Modifier.height(16.dp))

                selectedConsumptionSummary.value?.let { summary ->

                    ConsumptionSummaryBarChart(
                        summary,
                        isCarbonEmission = isCarbonEmissions.value,
                        barChartData = viewModel.getBarChartData(
                            summary,
                            isCarbonEmissions.value
                        )
                    )

                    val overallLabel = if (isCarbonEmissions.value) {
                        summary.carbonEmission.label
                    } else {
                        summary.energyExpended.label
                    }

                    val overallValue = if (isCarbonEmissions.value) {
                        summary.carbonEmission.total
                    } else {
                        summary.energyExpended.total
                    }

                    OverallConsumptionSummaryLabel(
                        label = overallLabel,
                        value = overallValue,
                        year = summary.year,
                        isCarbonEmissions = isCarbonEmissions.value
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    val heatingLabel = if (isCarbonEmissions.value) {
                        summary.categories.find { it.category == ConsumptionType.HEATING }?.carbonEmission?.label
                    } else {
                        summary.categories.find { it.category == ConsumptionType.HEATING }?.energyExpended?.label
                    }

                    val heatingValue = if (isCarbonEmissions.value) {
                        summary.categories.find { it.category == ConsumptionType.HEATING }?.carbonEmission?.total
                    } else {
                        summary.categories.find { it.category == ConsumptionType.HEATING }?.energyExpended?.total
                    }


                    ConsumptionSummaryLabel(
                        year = summary.year,
                        value = heatingValue,
                        consumptionType = ConsumptionType.HEATING,
                        label = heatingLabel,
                        isCarbonEmission = isCarbonEmissions.value
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    val electricityLabel = if (isCarbonEmissions.value) {
                        summary.categories.find { it.category == ConsumptionType.ELECTRICITY }?.carbonEmission?.label
                    } else {
                        summary.categories.find { it.category == ConsumptionType.ELECTRICITY }?.energyExpended?.label
                    }

                    val electricityValue = if (isCarbonEmissions.value) {
                        summary.categories.find { it.category == ConsumptionType.ELECTRICITY }?.carbonEmission?.total
                    } else {
                        summary.categories.find { it.category == ConsumptionType.ELECTRICITY }?.energyExpended?.total
                    }


                    ConsumptionSummaryLabel(
                        year = summary.year,
                        value = electricityValue,
                        consumptionType = ConsumptionType.ELECTRICITY,
                        label = electricityLabel,
                        isCarbonEmission = isCarbonEmissions.value
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    val transportationLabel = if (isCarbonEmissions.value) {
                        summary.categories.find { it.category == ConsumptionType.TRANSPORTATION }?.carbonEmission?.label
                    } else {
                        summary.categories.find { it.category == ConsumptionType.TRANSPORTATION }?.energyExpended?.label
                    }

                    val transportationValue = if (isCarbonEmissions.value) {
                        summary.categories.find { it.category == ConsumptionType.TRANSPORTATION }?.carbonEmission?.total
                    } else {
                        summary.categories.find { it.category == ConsumptionType.TRANSPORTATION }?.energyExpended?.total
                    }


                    ConsumptionSummaryLabel(
                        year = summary.year,
                        value = transportationValue,
                        consumptionType = ConsumptionType.TRANSPORTATION,
                        label = transportationLabel,
                        isCarbonEmission = isCarbonEmissions.value
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.home_your_carbon_emissions_info_title),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.home_your_carbon_emissions_info_description),
                        style = MaterialTheme.typography.labelSmall,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}