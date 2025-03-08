package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.all_investments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.all_investments.components.InvestmentListItem
import org.koin.androidx.compose.get

@Composable
fun AllInvestmentsScreen(
    viewModel: AllInvestmentsViewModel = get(),
    navigationService: NavigationService = get()
) {

    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchUserInvestments()
        viewModel.fetchUserCountry()
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Your Investments",
                hasBackNavigation = true,
                backNavigationCallback = {
                    navigationService.navControllerTabPhotovoltaic?.popBackStack()
                },
                actionButton = {
                    Row(modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigationService.toPhotovoltaicAddInvestment()
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_add_24),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Add Photovoltaic Investment"
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
            ) {

                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.solar_power_all_investments_disclaimer_text),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                Spacer(Modifier.height(8.dp))



                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface),
                ) {

                    LazyColumn(
                        state = rememberLazyListState(),
                    ) {
                        val investments = state.allInvestments
                        if (investments.isNullOrEmpty()) {

                        } else {
                            itemsIndexed(state.allInvestments) { index, investment ->
                                InvestmentListItem(
                                    investment = investment,
                                    userCountry = state.userCountry
                                ) {
                                    navigationService.toPhotovoltaicEditInvestment(investment.id)
                                }

                                if (index < state.allInvestments.lastIndex) {
                                    Divider(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(padding)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}