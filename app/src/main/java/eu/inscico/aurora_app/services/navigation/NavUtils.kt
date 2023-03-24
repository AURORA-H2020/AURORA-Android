package eu.inscico.aurora_app.services.navigation

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import eu.inscico.aurora_app.ui.screens.home.HomeTab
import eu.inscico.aurora_app.ui.screens.photovoltaic.PhotovoltaicTab
import eu.inscico.aurora_app.ui.screens.settings.SettingsTab

object NavUtils {

    @OptIn(ExperimentalComposeUiApi::class)
    fun getNavGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.apply {
            NavGraphDirections::class.sealedSubclasses.forEach { kClass ->
                val route = (kClass.objectInstance as? NavGraphDirections) ?: return@forEach

                if (false) {
                    dialog(
                        route = route.getRouteWithArgs(),
                        arguments = route.args.map {
                            navArgument(
                                name = it.name,
                            ) {
                                type = it.type
                            }
                        },
                        dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        route.content.invoke(it)
                    }
                } else {
                    composable(
                        route = route.getRouteWithArgs(),
                        arguments = route.args.map {
                            navArgument(
                                name = it.name,
                            ) {
                                type = it.type
                            }
                        }
                    ) {
                        route.content.invoke(it)
                    }
                }
            }
        }
    }

    fun getTabNavGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.apply {
            composable(NavTab.Home.route) { HomeTab() }
            composable(NavTab.Photovoltaic.route) { PhotovoltaicTab() }
            composable(NavTab.Settings.route) { SettingsTab() }
        }
    }

}