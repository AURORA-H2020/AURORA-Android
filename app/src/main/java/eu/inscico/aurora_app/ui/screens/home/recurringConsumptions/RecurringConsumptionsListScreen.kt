package eu.inscico.aurora_app.ui.screens.home.recurringConsumptions

import android.app.Activity
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.consumptions.ConsumptionListItem
import eu.inscico.aurora_app.ui.components.recurringConsumptions.RecurringConsumptionListItem
import org.koin.androidx.compose.get

@Composable
fun RecurringConsumptionsListScreen(
    navigationService: NavigationService = get(),
    viewModel: RecurringConsumptionsListViewModel = get()
){
    val context = LocalContext.current

    val recurringConsumptions = viewModel.recurringConsumptionsLive.observeAsState()
    val state = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            title = stringResource(id = R.string.home_recurring_consumptions_button_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabHome?.popBackStack()
            },
            actionButton = {
                Row(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            navigationService.toAddRecurringConsumption()
                        }
                ){
                    Image(
                        painter = painterResource(id = R.drawable.outline_add_24),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 7.dp),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        )

        if(recurringConsumptions.value.isNullOrEmpty()) {
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(vertical = 16.dp)
                        .clickable {
                            navigationService.toAddRecurringConsumption()
                        },
                    painter = painterResource(id = R.drawable.outline_settings_backup_restore_24),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
                )

                Text(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    text = stringResource(id = R.string.home_recurring_consumptions_button_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    text = stringResource(id = R.string.home_recurring_consumptions_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    onClick = {
                        navigationService.toAddRecurringConsumption()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                ) {
                    Text(text = stringResource(id = R.string.home_recurring_consumptions_add_button_title))
                }

            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                recurringConsumptions.value?.sortedByDescending { it.createdAt }?.let { recurringConsumptions ->
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(shape = RoundedCornerShape(16.dp)),
                        state = state,
                    ) {
                        items(recurringConsumptions) { item ->

                            RecurringConsumptionListItem(recurringConsumption = item) {
                                val id = item.id
                                navigationService.toRecurringConsumptionDetails(id)
                            }
                            if (recurringConsumptions.indexOf(item) != recurringConsumptions.lastIndex) {
                                Divider()
                            }
                        }
                    }
                }
            }
        }

    }
}