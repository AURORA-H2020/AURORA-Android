package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.investments.edit_investment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import eu.inscico.aurora_app.model.user.PVInvestmentResponse
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.firebase.PVPlantsService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.utils.FormFieldsUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class EditPVInvestmentViewModel(
    savedStateHandle: SavedStateHandle,
    val userService: UserService,
    val unitService: UnitService,
    val pvPlantService: PVPlantsService,
    val countryService: CountriesService
) : ViewModel() {

    val state = MutableStateFlow(EditPVInvestmentScreenState())

    val investmentId: String = savedStateHandle["id"] ?: ""

    val userPVPlant = pvPlantService.pvPlantForUserCityFlow
    val userCountry = countryService.userCountryFlow

    fun fetchInvestment(){
        viewModelScope.launch {

            val currentInvestment = userService.pvInvestmentsForUserLive.value?.firstOrNull {
                investmentId == it.id
            }

            state.emit(
                state.value.copy(
                    pvInvestmentToEdit = currentInvestment,
                    shareField = unitService.getValueWithLocalDecimalPoint(currentInvestment?.share.toString()),
                    noteField = currentInvestment?.note ?: "",
                    investmentDateField = currentInvestment?.investmentDate ?: Calendar.getInstance()
                )
            )
        }
    }

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

    fun checkIfFormIsReadyToSend() {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    isSaveValid = isSaveValid()
                )
            )
        }
    }

    private fun getPVInvestmentBody(): PVInvestmentResponse {
        val pricePerShare = userPVPlant.value?.pricePerShare
        val investmentPrice = if (pricePerShare != null) {
            pricePerShare * (unitService.getValueStringAsDouble(state.value.shareField) ?: 0.0)
        } else {
            null
        }

        val kwPerShare = userPVPlant.value?.kwPerShare
        val investmentCapacity = if (kwPerShare != null) {
            kwPerShare * (unitService.getValueStringAsDouble(state.value.shareField) ?: 0.0)
        } else {
            null
        }

        val createdAt = if(state.value.pvInvestmentToEdit?.createdAt != null){
            Timestamp(state.value.pvInvestmentToEdit!!.createdAt!!.time)
        } else {
            Timestamp.now()
        }

        return PVInvestmentResponse(
            id = state.value.pvInvestmentToEdit?.id,
            city = state.value.pvInvestmentToEdit?.city,
            pvPlant = state.value.pvInvestmentToEdit?.pvPlant,
            investmentPrice = state.value.pvInvestmentToEdit?.investmentPrice,
            investmentCapacity = state.value.pvInvestmentToEdit?.investmentCapacity,
            updatedAt = Timestamp.now(),
            createdAt =createdAt,
            share = unitService.getValueStringAsDouble(state.value.shareField),
            investmentDate = Timestamp(state.value.investmentDateField.time),
            note = state.value.noteField

        )
    }

    fun updatePVInvestment() {
        viewModelScope.launch {
            val body = getPVInvestmentBody()

            val result: Result<Boolean> = userService.updatePVInvestment(body)
            state.emit(
                state.value.copy(
                    editResult = result
                )
            )
        }
    }

    fun deletePVInvestment() {
        viewModelScope.launch {
            val pvInvestment = state.value.pvInvestmentToEdit
            val result = userService.deleteInvestment(pvInvestment)
            state.emit(
                state.value.copy(
                    editResult = result
                )
            )
        }
    }
}