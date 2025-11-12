package eu.inscico.aurora_app.ui.screens.recommendations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.recommendations.Recommendation
import eu.inscico.aurora_app.services.firebase.RecommendationsService
import eu.inscico.aurora_app.utils.TypedResult

class RecommendationDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val _recommendationsService: RecommendationsService
) : ViewModel() {

    private val selectedRecommendationId: String = savedStateHandle["id"] ?: ""

    val selectedRecommendation = getRecommendationById(selectedRecommendationId)

    private fun getRecommendationById(id: String): LiveData<Recommendation?> {
        val recommendation = _recommendationsService.userRecommendationsLive.value?.firstOrNull {
            it.id == id
        }
        val liveData = MutableLiveData<Recommendation?>()
        liveData.postValue(recommendation)
        return liveData as LiveData<Recommendation?>
    }

    suspend fun deleteRecommendation(recommendation: Recommendation): TypedResult<Any, Any> {
        return _recommendationsService.deleteRecommendation(recommendation)
    }

    suspend fun toggleReadStatus(recommendation: Recommendation): TypedResult<Boolean, String> {
        val updatedRecommendation = recommendation.copy(isRead = !recommendation.isRead)
        return _recommendationsService.updateRecommendation(updatedRecommendation)
    }
}
