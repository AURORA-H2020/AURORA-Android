package eu.inscico.aurora_app.services.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ConsumptionResponse
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumption
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumptionResponse
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.tasks.await
import kotlin.reflect.full.declaredMemberProperties

class RecurringConsumptionsService(
    private val _firestore: FirebaseFirestore,
    private val _firebaseAuth: FirebaseAuth
) {

    val collectionName = "recurring-consumptions"
    val usersCollectionName = "users"

    val recurringConsumptionsLive = MutableLiveData<List<RecurringConsumption>?>()

    private var _listener: ListenerRegistration? = null

    suspend fun loadRecurringConsumptionsOfUser(userCollectionName: String, userId: String): TypedResult<List<RecurringConsumption>, Any> {

        try {
            // Get recurring consumptions
            val recurringConsumptionsSnapshot = _firestore.collection(userCollectionName).document(userId)
                .collection(collectionName).get().await()
            val recurringConsumptions = recurringConsumptionsSnapshot.mapNotNull {
                try {
                    val recurringConsumptionsResponse = it.toObject<RecurringConsumptionResponse>() ?: return@mapNotNull null
                    RecurringConsumption.from(recurringConsumptionsResponse)
                } catch (e: Exception) {
                    null
                }
            }

            // Update recurring consumptions
            recurringConsumptionsLive.postValue(recurringConsumptions)

            return TypedResult.Success(recurringConsumptions)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    fun setRecurringConsumptionsListener(userCollectionName: String, userId: String) {
        _listener?.remove()

        _listener = _firestore.collection(userCollectionName).document(userId)
            .collection(collectionName)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val recurringConsumptions = value.mapNotNull {
                        try {
                            val recurringConsumptionResponse = it.toObject<RecurringConsumptionResponse>() ?: return@mapNotNull null
                            RecurringConsumption.from(recurringConsumptionResponse)
                        } catch (e: Exception) {

                            null
                        }
                    }
                    recurringConsumptionsLive.postValue(recurringConsumptions)
                }
            }
    }

    suspend fun createRecurringConsumption(recurringConsumptionResponse: RecurringConsumptionResponse): TypedResult<Boolean, String> {
        try {

            recurringConsumptionResponse.isEnabled = true
            val consumptionResponseAsMap = parseRecurringConsumptionResponseToMap(recurringConsumptionResponse)

            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")

            _firestore.collection(usersCollectionName).document(authId)
                .collection(collectionName).document().set(consumptionResponseAsMap).await()

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    suspend fun updateConsumption(recurringConsumptionResponse: RecurringConsumptionResponse): TypedResult<Boolean, String> {
        try {


            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")
            val docId = recurringConsumptionResponse.id ?: return TypedResult.Failure("")
            val consumptionResponseAsMap = parseRecurringConsumptionResponseToMap(recurringConsumptionResponse).toMutableMap()
            consumptionResponseAsMap.remove("id")

            _firestore.collection(usersCollectionName).document(authId)
                .collection(collectionName).document(docId).set(consumptionResponseAsMap).await()

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    private fun parseRecurringConsumptionResponseToMap(recurringConsumptionResponse: RecurringConsumptionResponse): Map<String, Any?>{
        val recurringConsumptionAsMap = mutableMapOf<String, Any?>()
        val recurringConsumptionDataAsMap = mutableMapOf<String, Any?>()
        val recurringConsumptionFrequencyAsMap = mutableMapOf<String, Any?>()

        recurringConsumptionResponse.javaClass.kotlin.declaredMemberProperties.forEach {
            val value = it.getValue(recurringConsumptionResponse, it)
            if(value != null){
                if(it.name == "heating" || it.name == "electricity" || it.name == "transportation"){
                    value.javaClass.kotlin.declaredMemberProperties.forEach { data ->
                        val dataValue = data.getValue(value, data)
                        if(dataValue != null){
                            recurringConsumptionDataAsMap[data.name] = dataValue
                        }
                    }
                    recurringConsumptionAsMap[it.name] = recurringConsumptionDataAsMap
                } else if(it.name == "frequency") {
                    value.javaClass.kotlin.declaredMemberProperties.forEach { data ->
                        val dataValue = data.getValue(value, data)
                        if(dataValue != null){
                            recurringConsumptionFrequencyAsMap[data.name] = dataValue
                        }
                    }
                    recurringConsumptionAsMap[it.name] = recurringConsumptionFrequencyAsMap
                }
                else {
                    recurringConsumptionAsMap[it.name] = value
                }
            }
        }
        return recurringConsumptionAsMap
    }

    suspend fun deleteConsumption(recurringConsumptionId: String): TypedResult<Any, Any> {
        val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")

        try {
            _firestore.collection(usersCollectionName).document(authId)
                .collection(collectionName).document(recurringConsumptionId)
                .delete()

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    fun deleteData(){
        recurringConsumptionsLive.postValue(null)
        _listener?.remove()
    }
}