package eu.inscico.aurora_app.model.recommendations

import java.util.Calendar

data class Recommendation(
    val id: String,
    val type: String,
    val createdAt: Calendar,
    val updatedAt: Calendar? = null,
    val notifyAt: Calendar? = null,
    val title: String? = null,
    val message: String,
    val rationale: String,
    val priority: Int,
    val link: String? = null,
    val isRead: Boolean = false
) {
    companion object {
        fun from(item: RecommendationResponse): Recommendation? {
            val id = item.id ?: return null
            val type = item.type ?: return null
            val message = item.message ?: return null
            val rationale = item.rationale ?: return null
            val priority = item.priority ?: return null

            val createdAt = if (item.createdAt != null) {
                Calendar.getInstance().apply {
                    time = item.createdAt!!.toDate()
                }
            } else {
                return null
            }

            val updatedAt = if (item.updatedAt != null) {
                Calendar.getInstance().apply {
                    time = item.updatedAt!!.toDate()
                }
            } else {
                null
            }

            val notifyAt = if (item.notifyAt != null) {
                Calendar.getInstance().apply {
                    time = item.notifyAt!!.toDate()
                }
            } else {
                null
            }

            return Recommendation(
                id = id,
                type = type,
                createdAt = createdAt,
                updatedAt = updatedAt,
                notifyAt = notifyAt,
                title = item.title,
                message = message,
                rationale = rationale,
                priority = priority,
                link = item.link,
                isRead = item.isRead ?: false
            )
        }
    }
}
