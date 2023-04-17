package eu.inscico.aurora_app.model.push

data class PushNotificationConfig(
    val type: PushNotificationType,
    val id: Int,
    val channelId: String,
    val channelName: String
)