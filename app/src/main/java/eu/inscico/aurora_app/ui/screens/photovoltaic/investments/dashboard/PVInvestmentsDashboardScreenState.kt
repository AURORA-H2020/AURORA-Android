package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard

import eu.inscico.aurora_app.model.City
import eu.inscico.aurora_app.model.country.Country
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.pvPlant.PVPlantData
import eu.inscico.aurora_app.model.user.PVInvestment

data class PVInvestmentsDashboardScreenState(
    val isLoading: Boolean = false,
    val userInvestments: List<PVInvestment>? = null,
    val pvPlantForUserCity: PVPlant? = null,
    val isPVPlantActive: Boolean = false,
    val userCountry: Country? = null,
    val userCity: City? = null,
    val latestPVInvestment: PVInvestment? = null,
    val userProductionLast30Days: Double = 0.0,
    val totalProductionLast30Days: Double = 0.0,
    val plantData: List<PVPlantData>? = null
)
