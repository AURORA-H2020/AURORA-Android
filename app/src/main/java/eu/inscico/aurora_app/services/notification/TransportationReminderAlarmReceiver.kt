package eu.inscico.aurora_app.services.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.push.PushNotificationType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class TransportationReminderAlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val _notificationCreationService: NotificationCreationService by inject()
    private val _notificationService: NotificationService by inject()
    override fun onReceive(context: Context, intent: Intent) {

        val title = context.getString(R.string.settings_notifications_mobility_alarm_notification_title)
        val description = context.getString(R.string.settings_notifications_mobility_alarm_notification_description)
        _notificationCreationService.showNotification(title, description, PushNotificationType.REMINDER_MOBILITY)

        val transportationReminderTime = _notificationService.mobilityReminder
        if(transportationReminderTime != null){
            val nextNotificationTime = _notificationService.getNextNotificationTime(transportationReminderTime)
            _notificationService.updateNotificationAlarm(ConsumptionType.TRANSPORTATION, nextNotificationTime)
        }
    }
}