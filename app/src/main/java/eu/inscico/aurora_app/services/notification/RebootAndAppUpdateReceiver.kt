package eu.inscico.aurora_app.services.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RebootAndAppUpdateReceiver : BroadcastReceiver(), KoinComponent {

    private val _notificationService: NotificationService by inject()

    override fun onReceive(context: Context, intent: Intent) {

        val electricityReminderTime = _notificationService.electricityReminder
        if(electricityReminderTime != null){
            val nextNotificationTime = _notificationService.getNextNotificationTime(electricityReminderTime)
            _notificationService.updateNotificationAlarm(ConsumptionType.ELECTRICITY, nextNotificationTime)
        }

        val heatingReminderTime = _notificationService.heatingReminder
        if(heatingReminderTime != null){
            val nextNotificationTime = _notificationService.getNextNotificationTime(heatingReminderTime)
            _notificationService.updateNotificationAlarm(ConsumptionType.HEATING, nextNotificationTime)
        }

        val transportationReminderTime = _notificationService.mobilityReminder
        if(transportationReminderTime != null){
            val nextNotificationTime = _notificationService.getNextNotificationTime(transportationReminderTime)
            _notificationService.updateNotificationAlarm(ConsumptionType.TRANSPORTATION, nextNotificationTime)
        }

        val behaviourChangePopupTime = _notificationService.behaviourChangeReminder
        val behaviourChangePopupActive = _notificationService.behaviourChangesPopupActivePrefs
        if (behaviourChangePopupTime != null){
            _notificationService.updateBehaviourChangePopup(time = behaviourChangePopupTime, isEnabled = behaviourChangePopupActive)
        }
    }
}