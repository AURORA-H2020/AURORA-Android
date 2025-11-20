package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components

import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.pvPlant.PVPlantData
import eu.inscico.aurora_app.model.user.PVInvestment
import java.time.ZonedDateTime

data class PvProductionBarChartState(
    val isLoading: Boolean = false,
    val referenceTime: Long = 0L,
    val millisPerDay: Long = 86_400_000L,
    val pvPlant: PVPlant? = null,
    val pvPlantData: List<PVPlantData>? = null,
    val allUserInvestments: List<PVInvestment>? = null,
    val firstInvestmentDate: ZonedDateTime = ZonedDateTime.now(),
    val lastPlantDataProductionDate: ZonedDateTime = ZonedDateTime.now()
)
