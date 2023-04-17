package eu.inscico.aurora_app.model.push

enum class PushNotificationType(val key: String) {
    REMINDER_ELECTRICITY("aurora_reminder_electricity"),
    REMINDER_MOBILITY("aurora_reminder_mobility"),
    REMINDER_HEATING("aurora_reminder_heating"),
    DEFAULT("DEFAULT");

    companion object {

        fun getPushNotificationType(typeString: String?): PushNotificationType {
            return when (typeString) {
                "aurora_reminder_electricity" -> REMINDER_ELECTRICITY
                "aurora_reminder_mobility" -> REMINDER_MOBILITY
                "aurora_reminder_heating" -> REMINDER_HEATING
                else -> DEFAULT
            }
        }

        fun getPushNotificationConfig(pushNotificationType: PushNotificationType): PushNotificationConfig {
            return when (pushNotificationType) {
                DEFAULT -> {
                    PushNotificationConfig(
                        type = DEFAULT,
                        id = 1234,
                        channelId = "aurora_channel_default",
                        channelName = "Allgemein"
                    )
                }
                REMINDER_HEATING -> {
                    PushNotificationConfig(
                        type = REMINDER_HEATING,
                        id = 2345,
                        channelId = "aurora_channel_reminder_heating",
                        channelName = "reminder_heating"
                    )
                }
                REMINDER_MOBILITY -> {
                    PushNotificationConfig(
                        type = REMINDER_MOBILITY,
                        id = 3456,
                        channelId = "aurora_channel_reminder_mobility",
                        channelName = "reminder_mobility"
                    )
                }
                REMINDER_ELECTRICITY -> {
                    PushNotificationConfig(
                        type = REMINDER_ELECTRICITY,
                        id = 4567,
                        channelId = "aurora_channel_reminder_mobility",
                        channelName = "reminder_mobility"
                    )
                }
            }
        }
    }
}