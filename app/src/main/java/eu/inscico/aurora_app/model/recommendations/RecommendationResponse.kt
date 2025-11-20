package eu.inscico.aurora_app.model.recommendations

import com.google.firebase.Timestamp

class RecommendationResponse(
    var id: String? = null,

    var type: String? = null,
    var createdAt: Timestamp? = null,
    var updatedAt: Timestamp? = null,
    var notifyAt: Timestamp? = null,
    var title: String? = null,
    var message: String? = null,
    var rationale: String? = null,
    var priority: Int? = null,
    var link: String? = null
) {
    @JvmField
    var isRead: Boolean? = null
    
    companion object {
        fun from(item: Recommendation): RecommendationResponse {
            val response = RecommendationResponse(
                id = item.id,
                type = item.type,
                createdAt = Timestamp(item.createdAt.time),
                updatedAt = if (item.updatedAt?.time != null) Timestamp(item.updatedAt.time) else null,
                notifyAt = if (item.notifyAt?.time != null) Timestamp(item.notifyAt.time) else null,
                title = item.title,
                message = item.message,
                rationale = item.rationale,
                priority = item.priority,
                link = item.link
            )
            response.isRead = item.isRead
            return response
        }
    }
}
