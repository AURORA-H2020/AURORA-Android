package eu.inscico.aurora_app.services.firebase

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.OnNetworkActiveListener
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ConsumptionResponse
import eu.inscico.aurora_app.services.network.NetworkService
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.reflect.full.declaredMemberProperties

class ConsumptionsService(
    private val _networkService: NetworkService,
    private val _firestore: FirebaseFirestore,
    private val _firebaseAuth: FirebaseAuth
) {
    val collectionName = "consumptions"
    val usersCollectionName = "users"

    val userConsumptionsLive = MutableLiveData<List<Consumption>?>()

    private var _listener: ListenerRegistration? = null

    suspend fun loadConsumptionsOfUser(userCollectionName: String, userId: String): TypedResult<List<Consumption>, Any> {

        try {
            // Get consumptions
            val consumptionsSnapshot = _firestore.collection(userCollectionName).document(userId)
                .collection(collectionName).get().await()
            val consumptions = consumptionsSnapshot.mapNotNull {
                try {
                    val consumptionResponse = it.toObject<ConsumptionResponse>() ?: return@mapNotNull null
                    Consumption.from(consumptionResponse)
                } catch (e: Exception) {
                    null
                }
            }

            // Update consumptions
            userConsumptionsLive.postValue(consumptions)

            return TypedResult.Success(consumptions)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    fun setConsumptionsListener(userCollectionName: String, userId: String) {
        _listener?.remove()

        _listener = _firestore.collection(userCollectionName).document(userId)
            .collection(collectionName)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val consumptions = value.mapNotNull {
                        try {
                            val consumptionResponse = it.toObject<ConsumptionResponse>() ?: return@mapNotNull null
                            Consumption.from(consumptionResponse)
                        } catch (e: Exception) {

                            null
                        }
                    }
                    userConsumptionsLive.postValue(consumptions)
                }
            }
    }

    suspend fun createConsumption(consumptionResponse: ConsumptionResponse): TypedResult<Boolean, String> {
        try {

            val consumptionResponseAsMap = parseConsumptionResponseToMap(consumptionResponse)

            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")

            val request =
                _firestore.collection(usersCollectionName).document(authId)
                    .collection(collectionName).document().set(consumptionResponseAsMap)

            if(_networkService.isNetworkAvailable()){
                request.await()
            } else {
                return TypedResult.Failure("NO_INTERNET")
            }

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    suspend fun updateConsumption(consumptionResponse: ConsumptionResponse): TypedResult<Boolean, String> {
        try {


            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")
            val docId = consumptionResponse.id ?: return TypedResult.Failure("")
            val consumptionResponseAsMap = parseConsumptionResponseToMap(consumptionResponse).toMutableMap()
            consumptionResponseAsMap.remove("id")

            val request = _firestore.collection(usersCollectionName).document(authId)
                .collection(collectionName).document(docId).set(consumptionResponseAsMap)

            if(_networkService.isNetworkAvailable()){
                request.await()
            } else {
                return TypedResult.Failure("NO_INTERNET")
            }

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    private fun parseConsumptionResponseToMap(consumptionResponse: ConsumptionResponse): Map<String, Any?>{
        val consumptionAsMap = mutableMapOf<String, Any?>()
        val consumptionDataAsMap = mutableMapOf<String, Any?>()

        consumptionResponse.javaClass.kotlin.declaredMemberProperties.forEach {
            val value = it.getValue(consumptionResponse, it)
            if(value != null){
                if(it.name == "heating" || it.name == "electricity" || it.name == "transportation"){
                    value.javaClass.kotlin.declaredMemberProperties.forEach { data ->
                        val dataValue = data.getValue(value, data)
                        if(dataValue != null){
                            consumptionDataAsMap[data.name] = dataValue
                        }
                    }
                    consumptionAsMap[it.name] = consumptionDataAsMap
                } else {
                    consumptionAsMap[it.name] = value
                }
            }
        }
        return consumptionAsMap
    }

    suspend fun deleteConsumption(consumption: Consumption): TypedResult<Any, Any> {
        val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")
        val consumptionId = when(consumption){
            is Consumption.ElectricityConsumption -> consumption.id
            is Consumption.HeatingConsumption -> consumption.id
            is Consumption.TransportationConsumption -> consumption.id
        }

        try {
            _firestore.collection(usersCollectionName).document(authId)
                .collection(collectionName).document(consumptionId)
                .delete()

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    fun deleteData(){
        userConsumptionsLive.postValue(null)
        _listener?.remove()
    }
}