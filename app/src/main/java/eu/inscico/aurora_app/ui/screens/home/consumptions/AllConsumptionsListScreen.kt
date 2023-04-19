package eu.inscico.aurora_app.ui.screens.home.consumptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.AuroraSearchBar
import eu.inscico.aurora_app.ui.components.consumptions.ConsumptionListItem
import eu.inscico.aurora_app.ui.screens.home.HomeViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllConsumptionsListScreen(
    viewModel: AllConsumptionsListViewModel = koinViewModel(),
    navigationService: NavigationService = get()
) {

    val context = LocalContext.current

    val userConsumptions = viewModel.searchResults.observeAsState(emptyList())
    val state = rememberLazyListState()

    viewModel.searchForResults(context)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            title = stringResource(id = R.string.home_consumptions_all_entries_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabHome?.popBackStack()
            },
            actionButton = {
                Row() {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        modifier = Modifier
                            .size(42.dp)
                            .padding(horizontal = 7.dp),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            },
            actionButtonCallback = {
                navigationService.toAddConsumption()
            }
        )


        AuroraSearchBar {
            viewModel.searchForResults(context, it)
        }
        Spacer(modifier = Modifier.height(16.dp))

        userConsumptions.value?.let { consumptions ->
            LazyColumn(
                Modifier
                    .fillMaxSize(),
                state = state,
            ) {
                items(consumptions) { item ->
                    Divider()
                    ConsumptionListItem(consumption = item) {
                        val id = when(it){
                            is Consumption.ElectricityConsumption -> it.id
                            is Consumption.HeatingConsumption -> it.id
                            is Consumption.TransportationConsumption -> it.id
                        }
                        navigationService.toConsumptionDetails(id)
                    }
                    if (consumptions.indexOf(item) == consumptions.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }
}