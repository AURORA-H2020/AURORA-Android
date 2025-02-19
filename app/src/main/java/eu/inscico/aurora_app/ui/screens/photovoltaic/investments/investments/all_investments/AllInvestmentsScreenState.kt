package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.all_investments

import eu.inscico.aurora_app.model.country.Country
import eu.inscico.aurora_app.model.user.PVInvestment

data class AllInvestmentsScreenState(
    val isLoading: Boolean = false,
    val allInvestments: List<PVInvestment>? = null,
    val userCountry: Country? = null
)
