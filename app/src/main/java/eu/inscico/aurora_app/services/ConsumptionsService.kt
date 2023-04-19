package eu.inscico.aurora_app.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import eu.inscico.aurora_app.model.UserResponse
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ConsumptionResponse
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.tasks.await

class ConsumptionsService(
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

            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")

            _firestore.collection(usersCollectionName).document(authId)
                .collection(collectionName).document().set(consumptionResponse).await()

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    suspend fun updateConsumption(consumptionResponse: ConsumptionResponse): TypedResult<Boolean, String> {
        try {

            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")
            val docId = consumptionResponse.id ?: return TypedResult.Failure("")

            _firestore.collection(usersCollectionName).document(authId)
                .collection(collectionName).document(docId).set(consumptionResponse).await()

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
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