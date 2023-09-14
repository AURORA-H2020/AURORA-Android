package eu.inscico.aurora_app.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.consumptionSummery.DashboardConsumptionSummaryLabel
import eu.inscico.aurora_app.ui.components.consumptions.ConsumptionListItem
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.utils.ExternalUtils
import eu.inscico.aurora_app.utils.LINK_AURORA_PROJECT_DESCRIPTION
import eu.inscico.aurora_app.utils.LocaleUtils.updateLocale
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.*

@Composable
fun HomeScreen(
    language: Locale = Locale.getDefault(),
    viewModel: HomeViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get()
) {

    val context = LocalContext.current

    val consumptions = viewModel.userConsumptions.observeAsState()
    val consumptionSummaries = viewModel.consumptionSummaries.observeAsState()

    val latestConsumptionSummary = consumptionSummaries.value?.maxByOrNull { it.year }

    // set language, only necessary for screenshot ui tests
    updateLocale(context, language)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            title = stringResource(id = R.string.home_dashboard_title),
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

                latestConsumptionSummary?.let {
                    val carbonValue = it.carbonEmission.total
                    val carbonLabel = it.carbonEmission.label ?: return@let
                    val energyValue = it.energyExpended.total
                    val energyLabel = it.energyExpended.label ?: return@let

                    DashboardConsumptionSummaryLabel(
                        carbonValue = carbonValue,
                        carbonLabel = carbonLabel,
                        energyValue = energyValue,
                        energyLabel = energyLabel
                    ) {
                        navigationService.toConsumptionSummary(it)
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {

                    ListItem(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                navigationService.toConsumptionSummary()
                            },
                        headlineContent = { Text(stringResource(id = R.string.home_energy_lables_title)) },
                        leadingContent = {
                            Image(
                                painterResource(id = R.drawable.outline_bar_chart_24),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                alignment = Alignment.CenterEnd
                            )
                        }
                    )
                    Divider()
                    ListItem(
                        modifier = Modifier
                            .testTag("Add_a_consumption")
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                navigationService.toAddConsumption()
                            },
                        headlineContent = { Text(stringResource(id = R.string.home_add_consumption_button_title)) },
                        leadingContent = {
                            Image(
                                painterResource(id = R.drawable.outline_add_circle_outline_24),
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
                                       navigationService.toRecurringConsumptions()
                            },
                        headlineContent = { Text(stringResource(id = R.string.home_recurring_consumptions_button_title)) },
                        leadingContent = {
                            Image(
                                painterResource(id = R.drawable.outline_settings_backup_restore_24),
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
                                ExternalUtils.openBrowser(
                                    context = context,
                                    url = LINK_AURORA_PROJECT_DESCRIPTION
                                )
                            },
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
                }


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

                val latestItemsCallback: (Consumption) -> Unit = {
                    val id = when (it) {
                        is Consumption.ElectricityConsumption -> it.id
                        is Consumption.HeatingConsumption -> it.id
                        is Consumption.TransportationConsumption -> it.id
                    }
                    navigationService.toConsumptionDetails(id)
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {

                    if (firstLatestConsumption != null) {
                        ConsumptionListItem(
                            consumption = firstLatestConsumption,
                            callback = latestItemsCallback
                        )
                        if (secondLatestConsumption != null) {
                            Divider()
                            ConsumptionListItem(
                                consumption = secondLatestConsumption,
                                callback = latestItemsCallback
                            )
                        }
                        if (thirdLatestConsumption != null) {
                            Divider()
                            ConsumptionListItem(
                                consumption = thirdLatestConsumption,
                                callback = latestItemsCallback
                            )
                        }
                    }
                }

                if ((sortedConsumptions?.size ?: 0) > 3) {

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
                if (sortedConsumptions?.isEmpty() == true) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Image(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(vertical = 16.dp).clickable {
                                    navigationService.toAddConsumption()
                                },
                            painter = painterResource(id = R.drawable.outline_add_circle_outline_24),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
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
                    }
                }
            }
        }
    }
}