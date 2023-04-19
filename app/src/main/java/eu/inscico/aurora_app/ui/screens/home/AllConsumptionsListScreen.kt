package eu.inscico.aurora_app.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.consumptions.ConsumptionListItem
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AllConsumptionsListScreen(
    homeViewModel: HomeViewModel = koinViewModel(),
    navigationService: NavigationService = get()
) {

    val userConsumptions = homeViewModel.userConsumptions.observeAsState(emptyList())
    val state = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            title = stringResource(id = R.string.home_consumptions_all_entries_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabHome?.popBackStack()
            }
        )

        Spacer(modifier = Modifier.height(31.dp))

        userConsumptions.value?.let { consumptions ->
            LazyColumn(
                Modifier
                    .fillMaxSize(),
                state = state,
            ) {
                items(consumptions) { item ->
                    Divider()
                    ConsumptionListItem(consumption = item) {

                    }
                    if(consumptions.indexOf(item) == consumptions.lastIndex){
                        Divider()
                    }
                }
            }
        }
    }
}