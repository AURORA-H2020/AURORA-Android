package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.DecoratedHeadline
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components.PvProductionInfoDialog
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard.components.LatestInvestmentsWidget
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard.components.PVPlantProductionInfoWidget
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard.components.PVPlantProductionWidget
import eu.inscico.aurora_app.utils.ExternalUtils
import org.koin.androidx.compose.get

@Composable
fun PVInvestmentsDashboardScreen(
    viewModel: PVInvestmentsDashboardViewModel = get(),
    navigationService: NavigationService = get()
) {

    val state = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchUserCountryAndCity()
        viewModel.fetchPvPlantForUserCity()
        viewModel.fetchInvestmentsForUser()
    }

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(R.string.solar_power_title),
                hasBackNavigation = false,
            )
        },
        content = { padding ->
            Column {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    if (state.pvPlantForUserCity != null && state.isPVPlantActive) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {

                            Spacer(Modifier.height(8.dp))

                            DecoratedHeadline(
                                headline = stringResource(R.string.solar_power_production_chart_title),
                                supportingText = stringResource(R.string.solar_power_production_chart_time_range_past_30_days_title),
                                leadingIconRes = R.drawable.outline_assessment_24,
                                actionIconRes = R.drawable.baseline_question_mark_24,
                                onActionClicked = {
                                    showDialog.value = true
                                }
                            )
                            PvProductionInfoDialog(
                                plantId = state.pvPlantForUserCity.plantId,
                                showDialog = showDialog
                            )
                            Spacer(Modifier.height(16.dp))

                            if(state.userInvestments != null &&  state.plantData != null) {
                                PVPlantProductionWidget(
                                    yourProduction = state.userProductionLast30Days,
                                    pvPlantProduction = state.totalProductionLast30Days,
                                    pvPlant = state.pvPlantForUserCity,
                                    pvPlantData = state.plantData,
                                    userInvestments = state.userInvestments,
                                    onClick = {
                                        navigationService.toPhotovoltaicProductionChart()
                                    }
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            val latestInvestment = state.latestPVInvestment
                            LatestInvestmentsWidget(
                                latestInvestment = latestInvestment,
                                onEditClicked = {
                                    if(latestInvestment != null){
                                        navigationService.toPhotovoltaicEditInvestment(latestInvestment.id)
                                    }
                                },
                                onAddInvestmentClicked = {
                                    navigationService.toPhotovoltaicAddInvestment()
                                },
                                userCountry = state.userCountry
                            )

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    navigationService.toPhotovoltaicAllInvestmentsList()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(R.string.solar_power_production_see_all_investments_title),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            PVPlantProductionInfoWidget(
                                pvPlant = state.pvPlantForUserCity,
                                userCity = state.userCity,
                                userCountry = state.userCountry
                            )

                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            ExternalUtils.openBrowser(context = context, url = state.pvPlantForUserCity.infoURL)
                                        },
                                    text = stringResource(R.string.solar_power_add_investment_how_to_invest_button_title),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    } else {
                        if (state.isLoading) {
                            Column(Modifier.fillMaxSize()) {
                                CircularProgressIndicator()
                            }
                        } else {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.solar_power_feature_not_available_yet_title),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp),
                            onClick = {
                                navigationService.toPhotovoltaicsCalculator()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            androidx.compose.material3.Text(
                                text = stringResource(R.string.userdialog_estimate_savings_button_title),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )

                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    )
}