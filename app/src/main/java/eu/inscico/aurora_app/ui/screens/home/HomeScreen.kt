package eu.inscico.aurora_app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.ElectricityConsumptionData
import eu.inscico.aurora_app.services.ConsumptionsService
import eu.inscico.aurora_app.services.CountriesService
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.consumptions.ConsumptionListItem
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.ui.theme.primary
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navigationService: NavigationService = get()
) {

    val consumptions = viewModel.userConsumptions.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            title = stringResource(id = R.string.bottom_bar_tab_home),
            hasBackNavigation = false
        )
        Column(
            Modifier.background(MaterialTheme.colorScheme.background)
        ) {

            ScrollableContent(background = MaterialTheme.colorScheme.background) {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.home_your_carbon_footprint_title),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Image(
                        painterResource(id = R.drawable.aurora_logo),
                        modifier = Modifier
                            .padding(16.dp)
                            .size(50.dp),
                        contentDescription = "",
                    )
                }

                Divider()
                ListItem(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { },
                    headlineContent = { Text(stringResource(id = R.string.home_energy_lables_title)) },
                    leadingContent = {
                        Image(
                            painterResource(id = R.drawable.baseline_bar_chart_24),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            alignment = Alignment.CenterEnd
                        )
                    }
                )
                Divider()
                ListItem(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            navigationService.toAddConsumption()
                        },
                    headlineContent = { Text(stringResource(id = R.string.home_add_consumption_button_title)) },
                    leadingContent = {
                        Image(
                            Icons.Filled.AddCircle,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            alignment = Alignment.CenterEnd
                        )
                    }
                )
                Divider()
                ListItem(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { },
                    headlineContent = { Text(stringResource(id = R.string.home_learn_more_title)) },
                    leadingContent = {
                        Image(
                            painterResource(id = R.drawable.baseline_question_mark_24),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            alignment = Alignment.CenterEnd
                        )
                    }
                )
                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.home_consumptions_latest_entries_title),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )

                val sortedConsumptions = consumptions.value?.sortedByDescending {
                    when (it) {
                        is Consumption.ElectricityConsumption -> it.updatedAt ?: it.createdAt
                        is Consumption.HeatingConsumption -> it.updatedAt ?: it.createdAt
                        is Consumption.TransportationConsumption -> it.updatedAt ?: it.createdAt
                    }
                }

                val firstLatestConsumption = sortedConsumptions?.firstOrNull()
                val secondLatestConsumption = sortedConsumptions?.getOrNull(1)
                val thirdLatestConsumption = sortedConsumptions?.getOrNull(2)

                if (firstLatestConsumption != null) {
                    Divider()
                    ConsumptionListItem(consumption = firstLatestConsumption){}
                    Divider()
                    if (secondLatestConsumption != null) {
                        ConsumptionListItem(consumption = secondLatestConsumption){}
                        Divider()
                    }
                    if (thirdLatestConsumption != null) {
                        ConsumptionListItem(consumption = thirdLatestConsumption){}
                        Divider()

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    navigationService.toConsumptionsList()
                                          },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                            ) {
                                Text(text = stringResource(id = R.string.home_consumptions_show_all_entries_button_title))
                            }
                        }
                    }

                } else {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Image(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(vertical = 16.dp),
                            painter = painterResource(id = R.drawable.baseline_add_circle_24),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outlineVariant)
                        )

                        Text(
                            modifier = Modifier.padding(horizontal = 32.dp),
                            text = stringResource(id = R.string.home_consumptions_title),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            modifier = Modifier.padding(horizontal = 32.dp),
                            text = stringResource(id = R.string.home_consumptions_add_first_title),
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            modifier = Modifier.padding(vertical = 24.dp),
                            onClick = {
                                navigationService.toAddConsumption()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                        ) {
                            Text(text = stringResource(id = R.string.home_consumptions_add_first_title))
                        }
                    }
                }
            }
        }
    }
}