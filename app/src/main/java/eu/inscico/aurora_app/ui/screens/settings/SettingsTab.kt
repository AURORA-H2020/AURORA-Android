package eu.inscico.aurora_app.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import eu.inscico.aurora_app.services.navigation.NavUtils
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme
import org.koin.androidx.compose.get

@Composable
fun SettingsTab(
    navigationService: NavigationService = get()
) {

    val settingsNavController = rememberNavController()
    navigationService.navControllerTabSettings = settingsNavController

    AURORAEnergyTrackerTheme {
        NavHost(navController = settingsNavController, startDestination = NavGraphDirections.Settings.getNavRoute()) {
            NavUtils.getNavGraph(this)
        }
    }
}