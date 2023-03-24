package eu.inscico.aurora_app.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import eu.inscico.aurora_app.services.navigation.NavUtils
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme
import org.koin.androidx.compose.get

@Composable
fun HomeTab(
    navigationService: NavigationService = get()
) {
    val homeNavController = rememberNavController()
    navigationService.navControllerTabHome = homeNavController

    AURORAEnergyTrackerTheme {
        NavHost(homeNavController, startDestination = NavGraphDirections.Home.getNavRoute()) {
            NavUtils.getNavGraph(this)
        }
    }
}