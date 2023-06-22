package eu.inscico.aurora_app.services.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.push.PushNotificationType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ElectricityReminderAlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val _notificationCreationService: NotificationCreationService by inject()
    private val _notificationService: NotificationService by inject()
    override fun onReceive(context: Context, intent: Intent) {

        val title = context.getString(R.string.settings_notifications_electricity_alarm_notification_title)
        val description = context.getString(R.string.settings_notifications_electricity_alarm_notification_description)
        _notificationCreationService.showNotification(title, description, PushNotificationType.REMINDER_ELECTRICITY)

        val electricityReminderTime = _notificationService.electricityReminder
        if(electricityReminderTime != null){
            val nextNotificationTime = _notificationService.getNextNotificationTime(electricityReminderTime)
            _notificationService.updateNotificationAlarm(ConsumptionType.ELECTRICITY, nextNotificationTime)
        }
    }
}