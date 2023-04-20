package eu.inscico.aurora_app.services.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import eu.inscico.aurora_app.ui.screens.home.consumptions.AllConsumptionsListScreen
import eu.inscico.aurora_app.ui.screens.home.HomeScreen
import eu.inscico.aurora_app.ui.screens.home.consumptions.ConsumptionDetailScreen
import eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption.AddConsumptionScreen
import eu.inscico.aurora_app.ui.screens.login.createProfile.CreateProfileScreen
import eu.inscico.aurora_app.ui.screens.login.AuthScreen
import eu.inscico.aurora_app.ui.screens.login.LoginScreen
import eu.inscico.aurora_app.ui.screens.login.signInEmail.SignInWithEmailScreen
import eu.inscico.aurora_app.ui.screens.photovoltaic.PhotovoltaicCalculatorScreen
import eu.inscico.aurora_app.ui.screens.settings.profile.EditProfileScreen
import eu.inscico.aurora_app.ui.screens.settings.SettingsScreen
import eu.inscico.aurora_app.ui.screens.settings.notifications.ElectricityBillNotificationScreen
import eu.inscico.aurora_app.ui.screens.settings.notifications.HeatingBillNotificationScreen
import eu.inscico.aurora_app.ui.screens.settings.notifications.MobilityNotificationScreen
import eu.inscico.aurora_app.ui.screens.settings.profile.UpdateEmailScreen
import eu.inscico.aurora_app.ui.screens.settings.profile.UpdatePasswordScreen

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

    object Auth : NavGraphDirections(
        route = "auth",
        content = {
            AuthScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object Login : NavGraphDirections(
        route = "login",
        content = {
            LoginScreen()
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

    object SignInWithEmail : NavGraphDirections(
        route = "signInWithEmail",
        content = {
            SignInWithEmailScreen()
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

    object ConsumptionsList : NavGraphDirections(
        route = "consumptionsList",
        content = {
            AllConsumptionsListScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object AddConsumption : NavGraphDirections(
        route = "addConsumption",
        content = {
            AddConsumptionScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object ConsumptionDetails : NavGraphDirections(
        route = "consumptionDetails",
        content = {
            ConsumptionDetailScreen()
        },
        args = listOf(
            NavArg(name = "id", type = NavType.StringType)
        )
    ) {
        fun getNavRoute(id: String): String {
            return "consumptionDetails/${id}"
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

    object UpdateUserPassword : NavGraphDirections(
        route = "updateUserPassword",
        content = {
            UpdatePasswordScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object UpdateUserEmail : NavGraphDirections(
        route = "updateUserEmail",
        content = {
            UpdateEmailScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object ElectricityBillReminder : NavGraphDirections(
        route = "electricityBillReminder",
        content = {
            ElectricityBillNotificationScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object HeatingBillReminder : NavGraphDirections(
        route = "heatingBillReminder",
        content = {
            HeatingBillNotificationScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object MobilityReminder : NavGraphDirections(
        route = "mobilityReminder",
        content = {
            MobilityNotificationScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    // endregion
}