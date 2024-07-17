package eu.inscico.aurora_app.ui.screens.home.recurringConsumptions

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.*
import eu.inscico.aurora_app.model.consumptions.TransportationType.Companion.getDisplayName
import eu.inscico.aurora_app.model.consumptions.TransportationTypeSection.Companion.getDisplayName
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumption
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumptionResponse
import eu.inscico.aurora_app.services.firebase.RecurringConsumptionsService
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import eu.inscico.aurora_app.utils.TypedResult

class AddOrUpdateRecurringConsumptionViewModel(
    savedStateHandle: SavedStateHandle,
    private val _recurringConsumptionService: RecurringConsumptionsService
): ViewModel() {

    private val allRecurringConsumptions = _recurringConsumptionService.recurringConsumptionsLive

    val initialValues: RecurringConsumption? = allRecurringConsumptions.value?.firstOrNull {
        (savedStateHandle["id"] ?: "") == it.id
    }

    val consumptionType: ConsumptionType? = ConsumptionType.TRANSPORTATION//ConsumptionType.parseStringToConsumptionType(savedStateHandle["consumptionType"])

    val allConsumptionTypes = ConsumptionType.getConsumptionTypeList()

    val allPublicVehicleOccupancyTypes = PublicVehicleOccupancy.getAll()

    private fun getSpinnerSectionsOfTransportation(context: Context): List<SpinnerItem.Section<TransportationType>> {
        return listOf(
            SpinnerItem.Section(section = TransportationTypeSection.CARS_AND_MOTORCYCLES, name = TransportationTypeSection.CARS_AND_MOTORCYCLES.getDisplayName(context), entries = listOf(
                TransportationType.FUEL_CAR, TransportationType.ELECTRIC_CAR, TransportationType.HYBRID_CAR, TransportationType.MOTORCYCLE, TransportationType.ELECTRIC_MOTORCYCLE)),
            SpinnerItem.Section(section = TransportationTypeSection.BUSSES, name = context.getString(
                R.string.home_add_consumption_transportation_type_section_busses_title), entries = listOf(
                TransportationType.ELECTRIC_BUS, TransportationType.HYBRID_ELECTRIC_BUS, TransportationType.ALTERNATIVE_FUEL_BUS, TransportationType.DIESEL_BUS, TransportationType.OTHER_BUS)),
            SpinnerItem.Section(section = TransportationTypeSection.TRAINS_AND_TRAMS, name = context.getString(
                R.string.home_add_consumption_transportation_type_section_trains_and_trams_title), entries = listOf(
                TransportationType.METRO_TRAM_OR_URBAN_LIGHT_TRAIN, TransportationType.ELECTRIC_PASSENGER_TRAIN, TransportationType.DIESEL_PASSENGER_TRAIN, TransportationType.HIGH_SPEED_TRAIN)),
            SpinnerItem.Section(section = TransportationTypeSection.AVIATION, name = context.getString(
                R.string.home_add_consumption_transportation_type_section_aviation_title), entries = listOf(
                TransportationType.PLANE, TransportationType.PLANE_INTRA_EU, TransportationType.PLANE_EXTRA_EU )),
            SpinnerItem.Section(section = TransportationTypeSection.OTHER, name = context.getString(
                R.string.home_add_consumption_transportation_type_section_other_title), entries = listOf(
                TransportationType.ELECTRIC_BIKE, TransportationType.ELECTRIC_SCOOTER, TransportationType.BIKE, TransportationType.WALKING)),
        )
    }

    fun getSpinnerItemsListOfTransportationType(context: Context): List<SpinnerItem>{
        val sections = getSpinnerSectionsOfTransportation(context)
        val spinnerItems = mutableListOf<SpinnerItem>()
        sections.forEach { section ->
            spinnerItems.add(section)
            section.entries?.forEach { item->
                spinnerItems.add(
                    SpinnerItem.Entry(name = item.getDisplayName(context), data = item)
                )
            }
        }

        return spinnerItems
    }

    fun getSectionForSelectedTransportType(context: Context, item: TransportationType?): TransportationTypeSection? {
        if(item == null) return null
        val allSections = getSpinnerSectionsOfTransportation(context)

        allSections.forEach { section ->
            section.entries?.forEach {entry ->
                if(entry == item){
                    return section.section as TransportationTypeSection
                }

            }
        }
        return null
    }

    fun isDecimalInputValid(input: String): Boolean {
        return input.matches(Regex("^(\\d+(?:,\\d{1,2})?)")) || input.matches(Regex("^(\\d+,)")) || input.matches(Regex("^(\\d+(?:.\\d{1,2})?)")) || input.matches(Regex("^(\\d+.)"))
    }

    suspend fun createRecurringConsumption(recurringConsumptionResponse: RecurringConsumptionResponse): TypedResult<Boolean, String> {
        return _recurringConsumptionService.createRecurringConsumption(recurringConsumptionResponse)
    }

    suspend fun
            updateRecurringConsumption(recurringConsumptionResponse: RecurringConsumptionResponse): TypedResult<Boolean, String> {
        return _recurringConsumptionService.updateConsumption(recurringConsumptionResponse)
    }

    suspend fun deleteRecurringConsumption(recurringConsumptionId: String): TypedResult<Any, Any> {
        return _recurringConsumptionService.deleteConsumption(recurringConsumptionId)
    }

}