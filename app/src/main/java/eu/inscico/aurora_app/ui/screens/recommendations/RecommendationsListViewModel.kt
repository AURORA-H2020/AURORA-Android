package eu.inscico.aurora_app.ui.screens.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import eu.inscico.aurora_app.services.firebase.RecommendationsService

class RecommendationsListViewModel(
    private val _recommendationsService: RecommendationsService
) : ViewModel() {

    val userRecommendations = _recommendationsService.userRecommendationsLive.map {
        it?.sortedByDescending { recommendation ->
            recommendation.createdAt
        }
    }
}
