package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.add_investment

import java.util.Calendar

data class AddPVInvestmentScreenState(
    val isLoading: Boolean = false,
    val shareField: String = "",
    val investmentDateField: Calendar = Calendar.getInstance(),
    val noteField: String = "",
    val isSaveValid: Boolean = false,
    val creationResult: Result<Boolean>? = null,
)
