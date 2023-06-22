package eu.inscico.aurora_app.services.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.push.PushNotificationType
import eu.inscico.aurora_app.services.navigation.NavTab
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.notification.NotificationService.Companion.BEHAVIOUR_CHANGED_POPUP_INTERVAL
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.dialogs.ChangeBehaviourPopupDialog
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class RecurringConsumptionsBehaviourChangedPopupReceiver : BroadcastReceiver(), KoinComponent {

    private val _notificationCreationService: NotificationCreationService by inject()
    private val _notificationService: NotificationService by inject()
    private val navigationService: NavigationService by inject()

    override fun onReceive(context: Context, intent: Intent) {


        _notificationCreationService.showChangeBehaviorDialog(
            onPositiveButtonCallback = {
                navigationService.switchTabTo(NavTab.Home) {
                    navigationService.toRecurringConsumptions()
                }
            },
            onNeutralButtonCallback = {
                _notificationService.behaviourChangesPopupActivePrefs = false
                _notificationService.updateBehaviourChangePopup(time = 0, isEnabled = false)
            })

        val now = Calendar.getInstance()
        val twoWeeksInterval = BEHAVIOUR_CHANGED_POPUP_INTERVAL
        val nextPopup = now.timeInMillis + twoWeeksInterval
        _notificationService.behaviourChangeReminder = nextPopup

        val isEnabled = _notificationService.behaviourChangesPopupActivePrefs
        _notificationService.updateBehaviourChangePopup(time = nextPopup, isEnabled = isEnabled)
    }
}