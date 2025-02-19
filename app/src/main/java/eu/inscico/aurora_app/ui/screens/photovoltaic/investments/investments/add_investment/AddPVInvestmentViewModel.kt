package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.add_investment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionResponse
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType
import eu.inscico.aurora_app.model.user.PVInvestmentResponse
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.firebase.PVPlantsService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.utils.FormFieldsUtils
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

class AddPVInvestmentViewModel(
    val unitService: UnitService,
    val countriesService: CountriesService,
    val pvPlantService: PVPlantsService,
    val userService: UserService
) : ViewModel() {

    val state = MutableStateFlow(AddPVInvestmentScreenState())

    val userCity = countriesService.userCityFlow
    val userCountry = countriesService.userCountryFlow
    val userPVPlant = pvPlantService.pvPlantForUserCityFlow

    private fun updateSharesField(shares: String) {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    shareField = shares
                )
            )
        }
    }

    fun updateInvestmentDateField(investmentDate: Calendar) {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    investmentDateField = investmentDate
                )
            )
        }
    }

    fun updateNoteField(note: String) {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    noteField = note
                )
            )
        }
    }

    fun checkIfFormIsReadyToSend(){
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    isSaveValid = isSaveValid()
                )
            )
        }
    }

    fun isShareFieldValid(shareInput: String): Boolean {
        // display new value if format correct
        val isValueInCorrectFormat = FormFieldsUtils.isDecimalInputValid(shareInput)
        if (isValueInCorrectFormat || shareInput.isEmpty()) {
            updateSharesField(unitService.getValueWithLocalDecimalPoint(shareInput))
        }
        // check if input is valid
        val valueAsDouble = unitService.getValueStringAsDouble(shareInput)
        return isValueInCorrectFormat && valueAsDouble != null
    }

    private fun isSaveValid(): Boolean {
        return state.value.shareField.isNotEmpty()
    }

    private fun getPVInvestmentBody(): PVInvestmentResponse {
        val pricePerShare = userPVPlant.value?.pricePerShare
        val investmentPrice = if(pricePerShare != null){
            pricePerShare * (unitService.getValueStringAsDouble(state.value.shareField) ?: 0.0)
        } else {
            null
        }

        val kwPerShare = userPVPlant.value?.kwPerShare
        val investmentCapacity = if(kwPerShare != null){
            kwPerShare * (unitService.getValueStringAsDouble(state.value.shareField) ?: 0.0)
        } else {
            null
        }

        return PVInvestmentResponse(
            id = null,
            city = userCity.value?.id,
            share = unitService.getValueStringAsDouble(state.value.shareField),
            updatedAt = Timestamp.now(),
            pvPlant = userPVPlant.value?.plantId,
            investmentDate =  Timestamp(state.value.investmentDateField.time),
            investmentPrice = investmentPrice,
            investmentCapacity = investmentCapacity,
            note = state.value.noteField

        )
    }

    fun createPVInvestment() {
        viewModelScope.launch {
            val body = getPVInvestmentBody()

            val result: Result<Boolean> = userService.createPVInvestment(body)
            state.emit(
                state.value.copy(
                    creationResult = result
                )
            )
        }
    }
}