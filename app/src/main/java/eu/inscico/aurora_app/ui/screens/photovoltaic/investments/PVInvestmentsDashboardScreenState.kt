package eu.inscico.aurora_app.ui.screens.photovoltaic.investments

import eu.inscico.aurora_app.model.City
import eu.inscico.aurora_app.model.country.Country
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.user.PVInvestment

data class PVInvestmentsDashboardScreenState(
    val isLoading: Boolean = false,
    val userInvestments: List<PVInvestment>? = null,
    val pvPlantForUserCity: PVPlant? = null,
    val isPVPlantActive: Boolean = false,
    val userCountry: Country? = null,
    val userCity: City? = null
)
