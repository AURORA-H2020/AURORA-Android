package eu.inscico.aurora_app.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import eu.inscico.aurora_app.model.consumptionSummary.ConsumptionSummaryResponse
import eu.inscico.aurora_app.model.consumptionSummary.ConsumptionSummary

class ConsumptionSummaryService(
    private val _firestore: FirebaseFirestore
) {
    val collectionName = "consumption-summaries"

    private val _consumptionSummaries = MutableLiveData<List<ConsumptionSummary>?>()
    val consumptionSummariesLive: LiveData<List<ConsumptionSummary>?> = _consumptionSummaries

    private var _listener: ListenerRegistration? = null

    fun setConsumptionSummariesListener(authId: String, usersCollectionName: String) {
        _listener?.remove()

        _listener =
            _firestore.collection(usersCollectionName).document(authId).collection(collectionName)
                .addSnapshotListener { value, error ->

                    if (value != null) {
                        val consumptionSummaries = value.mapNotNull {
                            try {
                                val consumptionSummaryResponse =
                                    it.toObject<ConsumptionSummaryResponse>()
                                        ?: return@mapNotNull null
                                ConsumptionSummary.from(consumptionSummaryResponse)

                            } catch (e: Exception) {

                                null
                            }
                        }
                        _consumptionSummaries.postValue(consumptionSummaries)

                    }
                }
    }
}
