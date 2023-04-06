package eu.inscico.aurora_app.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavTab
import eu.inscico.aurora_app.services.navigation.NavUtils
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme
import eu.inscico.aurora_app.ui.theme.iconColorMedium
import eu.inscico.aurora_app.ui.theme.semiTransparent
import eu.inscico.aurora_app.utils.KeyboardState
import eu.inscico.aurora_app.utils.keyboardAsState
import org.koin.androidx.compose.get

@Composable
fun AuroraApp(
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get(),
    authService: AuthService = get(),
    userService: UserService = get()
) {

    val tabItems = if (false) {
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

    val keyboardState by keyboardAsState()
    val isKeyboardOpen = keyboardState == KeyboardState.Opened

    val isAuthenticatedState =
        authService.isAuthenticatedLive.observeAsState(authService.isAuthenticated)

    AURORAEnergyTrackerTheme {
        val navControllerApp = rememberNavController()
        navigationService.navControllerApp = navControllerApp

        val navControllerAuth = rememberNavController()
        navigationService.navControllerAuth = navControllerAuth

        val navControllerTabs = rememberNavController()
        navigationService.navControllerTabs = navControllerTabs

        val user = userService.userLive.observeAsState()

        NavHost(navController = navControllerApp, startDestination = "app") {
            composable("app") {
                AuroraScaffold(
                    snackBarHost = {},
                    bottomBar = {
                        if (user.value != null && isAuthenticatedState.value && !isKeyboardOpen) {
                            NavigationBar(
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.surface
                            ) {
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
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSecondary,
                                            indicatorColor = semiTransparent,
                                        ),
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
                            }
                        }
                    }
                ) { innerPadding ->

                    Log.d("AuthHelp", "user not null, ${user.value != null}")
                    if (user.value != null) {
                        Log.d("AuthHelp", "navHost tabs")
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
                        //val isAuthenticated = authService.isAuthenticatedLive.observeAsState()
                        //Log.d("AuthHelp", "is authenticated ${isAuthenticated.value}")

                        NavHost(
                            navController = navControllerAuth,
                            startDestination = //if (isAuthenticated.value == true) {
                                //NavGraphDirections.CreateProfile.getNavRoute()
                            //} else {
                                NavGraphDirections.Login.getNavRoute()
                            //}
                    ,
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