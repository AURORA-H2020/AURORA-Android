package eu.inscico.aurora_app.services.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import eu.inscico.aurora_app.model.City
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.model.pvPlant.PVPlantDataResponse
import eu.inscico.aurora_app.model.pvPlant.PVPlantResponse
import eu.inscico.aurora_app.model.pvPlant.PVPlantData
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PVPlantsService(
    private val _firestore: FirebaseFirestore,
    private val _countryService: CountriesService,
) {
    private val pvPlantsCollectionName = "pv-plants"
    private val pvPlantDataCollectionName = "data"

    private val _pvPlantsFlow = MutableStateFlow<List<PVPlant>?>(emptyList())
    val pvPlantsFlow: StateFlow<List<PVPlant>?> = _pvPlantsFlow

    private val _dataFromPvPlantFlow = MutableStateFlow<List<PVPlantData>?>(null)
    val dataFromPvPlantFlow: StateFlow<List<PVPlantData>?> = _dataFromPvPlantFlow

    private val _pvPlantForUserCityFlow = MutableStateFlow<PVPlant?>(null)
    val pvPlantForUserCityFlow = _pvPlantForUserCityFlow

    private val userCityFlow: StateFlow<City?> = _countryService.userCityFlow

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadPVPlants()
            userCityFlow.collect {
                val id = it?.id
                if (id != null) getPVPlantForCity(id)
            }
        }
    }

    suspend fun loadPVPlants(): TypedResult<List<PVPlant>, Any> {
        try {

            // Get pvPlants
            val pvPlantsSnapshot = _firestore.collection(pvPlantsCollectionName).get().await()
            val pvPlants = pvPlantsSnapshot.mapNotNull {
                try {
                    val pvPlantResponse = it.toObject<PVPlantResponse>() ?: return@mapNotNull null
                    PVPlant.from(pvPlantResponse)
                } catch (e: Exception) {
                    val x = e
                    null
                }
            }

            // Update pvPlants
            _pvPlantsFlow.emit(pvPlants)

            return TypedResult.Success(pvPlants)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    suspend fun loadDataForPVPlant(plantId: String): TypedResult<List<PVPlantData>, Any> {
        _dataFromPvPlantFlow.emit(null)
        try {
            // Get data
            val dataSnapshot = _firestore.collection(pvPlantsCollectionName).document(plantId)
                .collection(pvPlantDataCollectionName).get().await()
            val data = dataSnapshot.mapNotNull {
                try {
                    val dataResponse = it.toObject<PVPlantDataResponse>() ?: return@mapNotNull null
                    PVPlantData.from(dataResponse)
                } catch (e: Exception) {
                    val x = e
                    null
                }
            }

            // Update data
            _dataFromPvPlantFlow.emit(data)

            return TypedResult.Success(data)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    suspend fun getUserPVPlantIfActive(userCity: City): PVPlant? {
        val pvPlantForCity = getPVPlantForCity(userCity.id) ?: return null
        return if (isPVPlantActive(pvPlantForCity)) {
            pvPlantForCity
        } else {
            null
        }
    }

    suspend fun getPVPlantForCity(cityId: String): PVPlant? {
        val pvPlantForUserCity = _pvPlantsFlow.first { true }?.find {
            it.city == cityId
        }
        _pvPlantForUserCityFlow.emit(pvPlantForUserCity)
        return pvPlantForUserCity
    }

    fun isPVPlantActive(pvPlant: PVPlant): Boolean {
        return pvPlant.active
    }
}