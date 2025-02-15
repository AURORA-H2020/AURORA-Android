package eu.inscico.aurora_app.services.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PVPlantsService(
    private val _firestore: FirebaseFirestore
) {
    private val pvPlantsCollectionName = "pv-plants"
    private val pvPlantDataCollectionName = "data"

    private val _pvPlantsLive = MutableLiveData<List<PVPlant>>()
    val pvPlantsLive: LiveData<List<PVPlant>> = _pvPlantsLive

    private val _dataFromPvPlantLive = MutableLiveData<List<PVPlantData>?>()
    val dataFromPvPlantLive: LiveData<List<PVPlantData>?> = _dataFromPvPlantLive

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadPVPlants()
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
            _pvPlantsLive.postValue(pvPlants)

            return TypedResult.Success(pvPlants)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    suspend fun loadDataForPVPlant(plantId: String): TypedResult<List<PVPlantData>, Any> {
        _dataFromPvPlantLive.postValue(null)
        try {
            // Get data
            val dataSnapshot = _firestore.collection(pvPlantDataCollectionName).document(plantId)
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
            _dataFromPvPlantLive.postValue(data)

            return TypedResult.Success(data)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    fun getUserPVPlantIfActive(userCity: City): PVPlant? {
        val pvPlantForCity = getPVPlantForCity(userCity.id) ?: return null
        return if(isPVPlantActive(pvPlantForCity)){
            pvPlantForCity
        } else {
            null
        }
    }

    fun getPVPlantForCity(cityId: String): PVPlant? {
        val allPVPlants = _pvPlantsLive.value ?: return null

        return allPVPlants.find {
            it.city == cityId
        }
    }

    fun isPVPlantActive(pvPlant: PVPlant): Boolean {
        return pvPlant.active
    }
}