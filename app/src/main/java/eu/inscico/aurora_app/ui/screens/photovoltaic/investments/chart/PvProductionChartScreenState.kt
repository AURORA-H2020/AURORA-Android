package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart

import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.pvPlant.PVPlantData
import eu.inscico.aurora_app.model.pvProductionChart.ChartTimeRangeType
import eu.inscico.aurora_app.model.pvProductionChart.DisplayOption
import eu.inscico.aurora_app.model.user.PVInvestment
import eu.inscico.aurora_app.utils.CalendarUtils
import java.util.Calendar

data class PvProductionChartScreenState(
    val isLoading: Boolean = false,
    val selectedDisplayOption: DisplayOption = DisplayOption.TOTAL_PRODUCTION,
    val selectedChartTimeRange: ChartTimeRangeType = ChartTimeRangeType.PAST_30_DAYS,
    val pvPlant: PVPlant? = null,
    val userInvestments: List<PVInvestment>? = null,
    val plantData: List<PVPlantData>? = null,
    val summedProductionValue: Double = 0.0,
    val firstProductionValueDate: Calendar = Calendar.getInstance()
)
