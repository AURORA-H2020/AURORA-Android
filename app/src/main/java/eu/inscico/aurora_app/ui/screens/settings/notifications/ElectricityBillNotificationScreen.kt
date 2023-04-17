package eu.inscico.aurora_app.ui.screens.settings.notifications

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.NotificationService
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.SwitchWithLabel
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.ui.components.reminder.ReminderFrequencySelector
import eu.inscico.aurora_app.ui.components.reminder.ReminderSelector
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun ElectricityBillNotificationScreen(
    viewModel: SettingsReminderViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    notificationService: NotificationService = get()
) {

    val enabledSwitch = remember { mutableStateOf(viewModel.electricityReminderActive) }

    Column(
        Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        AppBar(
            title = stringResource(id = R.string.settings_notifications_electricity_bill_reminder_title),
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
                        viewModel.updateElectricityReminderActive(it)
                        enabledSwitch.value = it
                })

                Text(
                    text = stringResource(id = R.string.settings_notifications_electricity_description),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary)


                Spacer(Modifier.height(32.dp))


                if(enabledSwitch.value){

                    val reminder = notificationService.electricityReminder
                    ReminderSelector(reminder){
                        //notificationReminder.value = it
                        viewModel.updateElectricityReminder(it)
                    }

                }

            }
        }
    }
}