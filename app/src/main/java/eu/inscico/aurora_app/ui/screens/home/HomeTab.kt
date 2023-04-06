package eu.inscico.aurora_app.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.services.navigation.NavUtils
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.screens.login.CreateProfileScreen
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme
import org.koin.androidx.compose.get

@Composable
fun HomeTab(
    userService: UserService = get(),
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