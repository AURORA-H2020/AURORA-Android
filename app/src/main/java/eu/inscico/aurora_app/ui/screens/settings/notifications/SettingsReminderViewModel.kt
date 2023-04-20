package eu.inscico.aurora_app.ui.screens.settings.notifications

import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.reminder.ReminderTime
import eu.inscico.aurora_app.services.notification.NotificationService

class SettingsReminderViewModel(
    private val _notificationService: NotificationService
): ViewModel() {

    var electricityReminderActive = _notificationService.electricityReminderActivePrefs
    var heatingReminderActive = _notificationService.heatingReminderActivePrefs
    var mobilityReminderActive = _notificationService.mobilityReminderActivePrefs


    fun updateElectricityReminderActive(isActive: Boolean){
        _notificationService.electricityReminderActivePrefs = isActive
    }

    fun updateElectricityReminder(electricityReminder: ReminderTime){
        _notificationService.electricityReminder = electricityReminder
    }

    fun updateHeatingReminderActive(isActive: Boolean){
        _notificationService.heatingReminderActivePrefs = isActive
    }

    fun updateHeatingReminder(heatingReminder: ReminderTime){
        _notificationService.heatingReminder = heatingReminder
    }

    fun updateMobilityReminderActive(isActive: Boolean){
        _notificationService.mobilityReminderActivePrefs = isActive
    }

    fun updateMobilityReminder(mobilityReminder: ReminderTime){
        _notificationService.mobilityReminder = mobilityReminder
    }

    fun updateNotificationAlarm(isEnabled: Boolean, type: ConsumptionType){
        val electricityReminderTime = _notificationService.electricityReminder
        if(electricityReminderTime != null){
            val nextNotificationTime = _notificationService.getNextNotificationTime(electricityReminderTime)
            _notificationService.updateNotificationAlarm(isEnabled = isEnabled, notificationType = type, nextNotificationTime = nextNotificationTime)
        }
    }


}