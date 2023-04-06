package eu.inscico.aurora_app.services.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import eu.inscico.aurora_app.ui.screens.home.HomeScreen
import eu.inscico.aurora_app.ui.screens.login.CreateProfileScreen
import eu.inscico.aurora_app.ui.screens.login.AuthScreen
import eu.inscico.aurora_app.ui.screens.photovoltaic.PhotovoltaicCalculatorScreen
import eu.inscico.aurora_app.ui.screens.settings.EditProfileScreen
import eu.inscico.aurora_app.ui.screens.settings.SettingsScreen

sealed class NavGraphDirections(
    protected val route: String,
    val args: List<NavArg> = emptyList(),
    val content: @Composable (NavBackStackEntry) -> Unit
) {

    fun getRouteWithArgs(): String {
        var routeString = route

        args.forEach {
            routeString += "/{${it.name}}"
        }

        return routeString
    }

    // region: Login
    // ---------------------------------------------------------------------------------------------

    object Login : NavGraphDirections(
        route = "login",
        content = {
            AuthScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object CreateProfile : NavGraphDirections(
        route = "create_profile",
        content = {
            CreateProfileScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    // endregion

    // region: HomeTab
    // ---------------------------------------------------------------------------------------------

    object Home : NavGraphDirections(
        route = "home",
        content = {
            HomeScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    // endregion


    // region: Photovoltaic
    // ---------------------------------------------------------------------------------------------

    object Photovoltaic : NavGraphDirections(
        route = "photovoltaic",
        content = {
            PhotovoltaicCalculatorScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }
    // endregion


    // region: Settings
    // ---------------------------------------------------------------------------------------------

    object Settings : NavGraphDirections(
        route = "settings",
        content = {
            SettingsScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object EditProfile : NavGraphDirections(
        route = "editProfile",
        content = {
            EditProfileScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    // endregion
}