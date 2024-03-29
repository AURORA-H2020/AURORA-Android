package eu.inscico.aurora_app.services.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import eu.inscico.aurora_app.ui.AcceptLegalsOverlayScreen
import eu.inscico.aurora_app.ui.screens.home.consumptions.AllConsumptionsListScreen
import eu.inscico.aurora_app.ui.screens.home.HomeScreen
import eu.inscico.aurora_app.ui.screens.home.consumptionSummary.ConsumptionSummaryScreen
import eu.inscico.aurora_app.ui.screens.home.consumptions.ConsumptionDetailScreen
import eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption.AddConsumptionScreen
import eu.inscico.aurora_app.ui.screens.home.recurringConsumptions.AddOrUpdateRecurringConsumptionScreen
import eu.inscico.aurora_app.ui.screens.home.recurringConsumptions.RecurringConsumptionsListScreen
import eu.inscico.aurora_app.ui.screens.login.createProfile.CreateProfileScreen
import eu.inscico.aurora_app.ui.screens.login.AuthScreen
import eu.inscico.aurora_app.ui.screens.login.LoginScreen
import eu.inscico.aurora_app.ui.screens.login.signInEmail.SignInWithEmailScreen
import eu.inscico.aurora_app.ui.screens.photovoltaic.PhotovoltaicCalculatorScreen
import eu.inscico.aurora_app.ui.screens.settings.profile.EditProfileScreen
import eu.inscico.aurora_app.ui.screens.settings.SettingsScreen
import eu.inscico.aurora_app.ui.screens.settings.featurePreview.FeaturePreviewScreen
import eu.inscico.aurora_app.ui.screens.settings.notifications.ElectricityBillNotificationScreen
import eu.inscico.aurora_app.ui.screens.settings.notifications.HeatingBillNotificationScreen
import eu.inscico.aurora_app.ui.screens.settings.notifications.MobilityNotificationScreen
import eu.inscico.aurora_app.ui.screens.settings.profile.SelectRegionScreen
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

    object AcceptLegals : NavGraphDirections(
        route = "acceptLegals",
        content = {
            AcceptLegalsOverlayScreen()
        },
        args = listOf(
            NavArg(name = "legalsVersion", type = NavType.LongType)
        )
    ) {
        fun getNavRoute(legalsVersion: Long = 1L): String {
            return "acceptLegals/${legalsVersion}"
        }
    }

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

    object RecurringConsumptionsList : NavGraphDirections(
        route = "recurringConsumptionsList",
        content = {
            RecurringConsumptionsListScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    object AddRecurringConsumption : NavGraphDirections(
        route = "addRecurringConsumption",
        content = {
            AddOrUpdateRecurringConsumptionScreen()
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

    object ConsumptionSummary : NavGraphDirections(
        route = "consumptionSummary",
        content = {
            ConsumptionSummaryScreen()
        },
        args = listOf(
            NavArg(name = "toEnergyExpendedTab", type = NavType.BoolType)
        )
    ) {
        fun getNavRoute(toEnergyExpendedTab: Boolean = false): String {
            return "consumptionSummary/${toEnergyExpendedTab}"
        }
    }

    object RecurringConsumptionDetails : NavGraphDirections(
        route = "recurringConsumptionDetails",
        content = {
            AddOrUpdateRecurringConsumptionScreen()
        },
        args = listOf(
            NavArg(name = "id", type = NavType.StringType)
        )
    ) {
        fun getNavRoute(id: String): String {
            return "recurringConsumptionDetails/${id}"
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

    object SelectRegion : NavGraphDirections(
        route = "selectRegion",
        content = {
            SelectRegionScreen()
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

    object FeaturePreview : NavGraphDirections(
        route = "featurePreview",
        content = {
            FeaturePreviewScreen()
        }
    ) {
        fun getNavRoute(): String {
            return route
        }
    }

    // endregion
}