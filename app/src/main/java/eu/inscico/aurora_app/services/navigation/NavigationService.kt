package eu.inscico.aurora_app.services.navigation

import androidx.activity.OnBackPressedDispatcherOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
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
}