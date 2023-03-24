package eu.inscico.aurora_app.ui.screens.photovoltaic

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import eu.inscico.aurora_app.services.navigation.NavUtils
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme
import org.koin.androidx.compose.get

@Composable
fun PhotovoltaicTab(
    navigationService: NavigationService = get()
){
    val photovoltaicNavController = rememberNavController()
    navigationService.navControllerTabPhotovoltaic = photovoltaicNavController

    AURORAEnergyTrackerTheme {
        NavHost(photovoltaicNavController, startDestination = NavGraphDirections.Photovoltaic.getNavRoute()) {
            NavUtils.getNavGraph(this)
        }
    }
}