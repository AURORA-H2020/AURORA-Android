package eu.inscico.aurora_app.services.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import eu.inscico.aurora_app.R

sealed class NavTab(
    val route: String,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int
) {

    object Home : NavTab("tab-home", R.string.bottom_bar_tab_home, R.drawable.outline_space_dashboard_24)

    object Photovoltaic : NavTab("tab-photovoltaic", R.string.bottom_bar_tab_photovoltaic, R.drawable.outline_solar_power_24)

    object Settings : NavTab("tab-settings", R.string.bottom_bar_tab_settings, R.drawable.outline_settings_24)

}