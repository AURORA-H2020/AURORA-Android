package eu.inscico.aurora_app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavTab
import eu.inscico.aurora_app.services.navigation.NavUtils
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme
import org.koin.androidx.compose.get

@Composable
fun AuroraApp(
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get()
){

    val tabItems = if(false){
        listOf(
            NavTab.Home,
            NavTab.Photovoltaic,
            NavTab.Settings,
        )
    } else {
        listOf(
            NavTab.Home,
            NavTab.Settings
        )
    }

    AURORAEnergyTrackerTheme {
        val navControllerApp = rememberNavController()
        navigationService.navControllerApp = navControllerApp

        val navControllerAuth = rememberNavController()
        navigationService.navControllerAuth = navControllerAuth

        val navControllerTabs = rememberNavController()
        navigationService.navControllerTabs = navControllerTabs

        NavHost(navController = navControllerApp, startDestination = "app") {
            composable("app") {
                AuroraScaffold(
                    snackBarHost = {},
                    bottomBar = {
                        //if (isAuthenticatedState.value && !isKeyboardOpen) {
                            NavigationBar {
                                val navBackStackEntry by navControllerTabs.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination

                                tabItems.forEach { tab ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = tab.iconRes),
                                                contentDescription = null
                                            )
                                        },
                                        label = { Text(stringResource(tab.titleRes)) },
                                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                                        onClick = {
                                            navControllerTabs.navigate(tab.route) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                // on the back stack as users select items
                                                popUpTo(navControllerTabs.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                // Avoid multiple copies of the same destination when
                                                // reselecting the same item
                                                launchSingleTop = true
                                                // Restore state when reselecting a previously selected item
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            //}
                        }
                    }
                ) { innerPadding ->
                    if (true) {
                        // Tabs NavHost
                        NavHost(
                            navController = navControllerTabs,
                            startDestination = NavTab.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            NavUtils.getTabNavGraph(this)
                        }
                    } else {
                        // Auth NavHost
                        NavHost(
                            navController = navControllerAuth,
                            startDestination = NavGraphDirections.Login.getNavRoute(),
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            NavUtils.getNavGraph(this)
                        }

                    }

                }
            }

            // Builder for fullscreen dialogs
            NavUtils.getNavGraph(this)
        }

        val showDialog = remember { userFeedbackService._showDialog }
        if (showDialog.value) {
            userFeedbackService.getDialog()
        }

    }

}