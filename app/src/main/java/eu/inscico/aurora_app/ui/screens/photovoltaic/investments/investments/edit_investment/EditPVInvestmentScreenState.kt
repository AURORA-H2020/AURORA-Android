package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.edit_investment

import eu.inscico.aurora_app.model.country.Country
import eu.inscico.aurora_app.model.user.PVInvestment
import java.util.Calendar

data class EditPVInvestmentScreenState(
    val isLoading: Boolean = false,
    val pvInvestmentToEdit: PVInvestment? = null,
    val shareField: String = "",
    val investmentDateField: Calendar = Calendar.getInstance(),
    val noteField: String = "",
    val isSaveValid: Boolean = false,
    val userCountry: Country? = null,
    val editResult: Result<Boolean>? = null,
    val deleteResult: Result<Boolean>? = null,
)
