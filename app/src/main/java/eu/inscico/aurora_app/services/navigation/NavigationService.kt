package eu.inscico.aurora_app.services.navigation

import androidx.activity.OnBackPressedDispatcherOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import kotlinx.coroutines.*

class NavigationService {

    var navControllerApp: NavController? = null
    var navControllerAuth: NavController? = null
    var navControllerTabs: NavController? = null

    var navControllerTabHome: NavController? = null
    var navControllerTabSettings: NavController? = null
    var navControllerTabPhotovoltaic: NavController? = null

    fun popBackStack(onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner) {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher?.onBackPressed()
    }

    fun toHome(){
        switchTabTo(NavTab.Home)
    }

    fun toPhotovoltaic(){
        switchTabTo(NavTab.Photovoltaic)
    }

    fun toSettings(){
        switchTabTo(NavTab.Settings)
    }

    private fun switchTabTo(navigationTab: NavTab, then: () -> Unit = {}) {
        navControllerTabs?.let {
            it.navigate(navigationTab.route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(it.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(1)
            withContext(Dispatchers.Main) {
                then.invoke()
            }
        }
    }

    // region: Auth
    // ---------------------------------------------------------------------------------------------

    fun toLogin() {
        val navDirection = NavGraphDirections.Login.getNavRoute()
        navControllerAuth?.navigate(navDirection)
    }

    fun toSignInWithEmail(){
        val navDirection = NavGraphDirections.SignInWithEmail.getNavRoute()
        navControllerAuth?.navigate(navDirection)
    }

    fun toCreateProfile() {
        val navDirection = NavGraphDirections.CreateProfile.getNavRoute()
        navControllerAuth?.navigate(navDirection)
    }
    // endregion

    // region: Settings
    // ---------------------------------------------------------------------------------------------

    fun toEditProfile() {
        val navDirection = NavGraphDirections.EditProfile.getNavRoute()
        navControllerTabSettings?.navigate(navDirection)
    }

    fun toUpdateUserPassword() {
        val navDirection = NavGraphDirections.UpdateUserPassword.getNavRoute()
        navControllerTabSettings?.navigate(navDirection)
    }

    fun toUpdateUserEmail() {
        val navDirection = NavGraphDirections.UpdateUserEmail.getNavRoute()
        navControllerTabSettings?.navigate(navDirection)
    }

    fun toElectricityBillReminder() {
        val navDirection = NavGraphDirections.ElectricityBillReminder.getNavRoute()
        navControllerTabSettings?.navigate(navDirection)
    }

    fun toHeatingBillReminder() {
        val navDirection = NavGraphDirections.HeatingBillReminder.getNavRoute()
        navControllerTabSettings?.navigate(navDirection)
    }

    fun toMobilityReminder() {
        val navDirection = NavGraphDirections.MobilityReminder.getNavRoute()
        navControllerTabSettings?.navigate(navDirection)
    }

    fun toFeaturePreview() {
        val navDirection = NavGraphDirections.FeaturePreview.getNavRoute()
        navControllerTabSettings?.navigate(navDirection)
    }

    // endregion

    // region: Home
    // ---------------------------------------------------------------------------------------------

    fun toConsumptionsList() {
        val navDirection = NavGraphDirections.ConsumptionsList.getNavRoute()
        navControllerTabHome?.navigate(navDirection)
    }

    fun toRecurringConsumptions() {
        val navDirection = NavGraphDirections.RecurringConsumptionsList.getNavRoute()
        navControllerTabHome?.navigate(navDirection)
    }

    fun toAddRecurringConsumption() {
        val navDirection = NavGraphDirections.AddRecurringConsumption.getNavRoute()
        navControllerTabHome?.navigate(navDirection)
    }

    fun toAddConsumption() {
        val navDirection = NavGraphDirections.AddConsumption.getNavRoute()
        navControllerTabHome?.navigate(navDirection)
    }

    fun toConsumptionDetails(id: String) {
        val navDirection = NavGraphDirections.ConsumptionDetails.getNavRoute(id)
        navControllerTabHome?.navigate(navDirection)
    }

    fun toConsumptionSummary(toEnergyExpendedTab: Boolean = false) {
        val navDirection = NavGraphDirections.ConsumptionSummary.getNavRoute(toEnergyExpendedTab)
        navControllerTabHome?.navigate(navDirection)
    }

    fun toRecurringConsumptionDetails(id: String) {
        val navDirection = NavGraphDirections.RecurringConsumptionDetails.getNavRoute(id)
        navControllerTabHome?.navigate(navDirection)
    }
    // endregion
}