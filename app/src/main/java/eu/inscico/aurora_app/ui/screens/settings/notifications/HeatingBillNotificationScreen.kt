package eu.inscico.aurora_app.ui.screens.settings.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.services.notification.NotificationService
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.SwitchWithLabel
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.ui.components.reminder.ReminderSelector
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun HeatingBillNotificationScreen(
    viewModel: SettingsReminderViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    notificationService: NotificationService = get()
) {
    val enabledSwitch = remember { mutableStateOf(viewModel.heatingReminderActive) }

    Column(
        Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        AppBar(
            title = stringResource(id = R.string.settings_notifications_heating_bill_reminder_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabSettings?.popBackStack()
            }
        )

        ScrollableContent(
            background = MaterialTheme.colorScheme.background
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                SwitchWithLabel(
                    label = stringResource(id = R.string.settings_notifications_enabled_title),
                    state = enabledSwitch.value,
                    onStateChange = {
                        viewModel.updateHeatingReminderActive(it)
                        enabledSwitch.value = it
                        viewModel.updateNotificationAlarm(enabledSwitch.value, ConsumptionType.HEATING)
                    })

                Text(
                    text = stringResource(id = R.string.settings_notifications_heating_description),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary)


                Spacer(Modifier.height(32.dp))


                if(enabledSwitch.value){

                    val reminder = notificationService.heatingReminder
                    ReminderSelector(reminder){
                        //notificationReminder.value = it
                        viewModel.updateHeatingReminder(it)
                        viewModel.updateNotificationAlarm(enabledSwitch.value, ConsumptionType.HEATING)
                    }

                }

            }
        }
    }
}