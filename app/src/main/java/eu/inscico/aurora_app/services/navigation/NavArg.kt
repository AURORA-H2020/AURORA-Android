package eu.inscico.aurora_app.services.navigation

import androidx.navigation.*

data class NavArg(
    val name: String,
    val type: NavType<*>
)