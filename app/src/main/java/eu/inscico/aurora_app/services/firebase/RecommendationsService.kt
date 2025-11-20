package eu.inscico.aurora_app.services.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import eu.inscico.aurora_app.model.recommendations.Recommendation
import eu.inscico.aurora_app.model.recommendations.RecommendationResponse
import eu.inscico.aurora_app.services.network.NetworkService
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.tasks.await

class RecommendationsService(
    private val _networkService: NetworkService,
    private val _firestore: FirebaseFirestore,
    private val _firebaseAuth: FirebaseAuth
) {
    val collectionName = "recommendations"
    val usersCollectionName = "users"

    val userRecommendationsLive = MutableLiveData<List<Recommendation>?>()

    private var _listener: ListenerRegistration? = null

    suspend fun loadRecommendationsOfUser(userCollectionName: String, userId: String): TypedResult<List<Recommendation>, Any> {
        try {
            // Get recommendations
            val recommendationsSnapshot = _firestore.collection(userCollectionName).document(userId)
                .collection(collectionName).get().await()
            val recommendations = recommendationsSnapshot.mapNotNull {
                try {
                    val recommendationResponse = it.toObject<RecommendationResponse>() ?: return@mapNotNull null
                    recommendationResponse.id = it.id
                    Recommendation.from(recommendationResponse)
                } catch (e: Exception) {
                    null
                }
            }

            // Update recommendations
            userRecommendationsLive.postValue(recommendations)

            return TypedResult.Success(recommendations)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    fun setRecommendationsListener(userCollectionName: String, userId: String) {
        _listener?.remove()

        _listener = _firestore.collection(userCollectionName).document(userId)
            .collection(collectionName)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    val recommendations = value.mapNotNull {
                        try {
                            val recommendationResponse = it.toObject<RecommendationResponse>() ?: return@mapNotNull null
                            recommendationResponse.id = it.id
                            Recommendation.from(recommendationResponse)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    userRecommendationsLive.postValue(recommendations)
                }
            }
    }

    suspend fun updateRecommendation(recommendation: Recommendation): TypedResult<Boolean, String> {
        try {
            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")
            
            val updates = mapOf(
                "isRead" to recommendation.isRead,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )

            val request = _firestore.collection(usersCollectionName).document(authId)
                .collection(collectionName).document(recommendation.id).update(updates)

            if (_networkService.isNetworkAvailable()) {
                request.await()
            } else {
                return TypedResult.Failure("NO_INTERNET")
            }

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    suspend fun deleteRecommendation(recommendation: Recommendation): TypedResult<Any, Any> {
        val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")

        try {
            _firestore.collection(usersCollectionName).document(authId)
                .collection(collectionName).document(recommendation.id)
                .delete()

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    fun deleteData() {
        userRecommendationsLive.postValue(null)
        _listener?.remove()
    }
}
