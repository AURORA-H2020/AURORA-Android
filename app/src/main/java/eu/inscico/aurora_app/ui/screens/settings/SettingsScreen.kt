package eu.inscico.aurora_app.ui.screens.settings

import android.app.Activity
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.UserSignInType
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.ActionEntry
import eu.inscico.aurora_app.ui.components.AppBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val context = LocalContext.current


        AppBar(
            title = stringResource(id = R.string.settings_app_bar_title),
            hasBackNavigation = false,
            actionButton = {
                androidx.compose.material.Text(text = stringResource(id = R.string.logout_button_title))
            },
            callback = {
                // TODO: logout
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            androidx.compose.material.Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(id = R.string.settings_profile_section_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }

        ActionEntry(
            title = stringResource(id = R.string.settings_profile_edit_profile_title),
            iconRes = R.drawable.baseline_account_circle_24,
            isNavigation = true,
            callback = {
                navigationService.toEditProfile()
            }
        )


        if(viewModel.userSignInType == UserSignInType.EMAIL){
            ActionEntry(
                title = stringResource(id = R.string.settings_profile_change_email_title),
                iconRes = R.drawable.baseline_mail_outline_24,
                titleColor = MaterialTheme.colorScheme.primary,
                isNavigation = false,
                callback = {

                }
            )
            ActionEntry(
                title = stringResource(id = R.string.settings_profile_change_password_title),
                iconRes = R.drawable.outline_vpn_key_24,
                titleColor = MaterialTheme.colorScheme.primary,
                isNavigation = false,
                callback = {

                }
            )

        }

        ActionEntry(
            title = stringResource(id = R.string.settings_profile_logout_title),
            iconRes = R.drawable.baseline_logout_24,
            isNavigation = false,
            iconColor = MaterialTheme.colorScheme.error,
            titleColor = MaterialTheme.colorScheme.error,
            callback = {
                userFeedbackService.showDialog(
                    message = context.getString(R.string.dialog_settings_logout_title),
                    confirmButtonText = context.getString(R.string.logout_button_title),
                    confirmButtonCallback = {
                        viewModel.userLogout(context as Activity)
                    }
                )
            }
        )

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            androidx.compose.material.Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(id = R.string.settings_notifications_section_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }

        ActionEntry(
            title = stringResource(id = R.string.settings_notifications_electricity_bill_reminder_title),
            iconRes = R.drawable.outline_electric_bolt_24,
            isNavigation = true,
            iconColor = MaterialTheme.colorScheme.tertiary
        )

        ActionEntry(
            title = stringResource(id = R.string.settings_notifications_heating_bill_reminder_title),
            iconRes = R.drawable.outline_local_fire_department_24,
            isNavigation = true,
            iconColor = MaterialTheme.colorScheme.error
        )

        ActionEntry(
            title = stringResource(id = R.string.settings_notifications_mobility_reminder_title),
            iconRes = R.drawable.outline_directions_car_24,
            isNavigation = true,
            iconColor = androidx.compose.ui.graphics.Color.Blue
        )

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            androidx.compose.material.Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(id = R.string.settings_data_privacy_section_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }

        ActionEntry(
            title = stringResource(id = R.string.settings_data_privacy_download_data_title),
            iconRes = R.drawable.outline_download_24,
            isNavigation = false
        )

        ActionEntry(
            title = stringResource(id = R.string.settings_data_privacy_delete_account_title),
            iconRes = R.drawable.outline_delete_outline_24,
            iconColor = MaterialTheme.colorScheme.error,
            titleColor = MaterialTheme.colorScheme.error,
            isNavigation = false
        )

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            androidx.compose.material.Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(id = R.string.settings_legal_information_section_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }

        ActionEntry(
            title = stringResource(id = R.string.settings_legal_information_feature_preview_title),
            iconRes = R.drawable.outline_auto_fix_high_24,
            isNavigation = false
        )

        ActionEntry(
            title = stringResource(id = R.string.settings_legal_information_licenses_title),
            iconRes = R.drawable.outline_receipt_24,
            isNavigation = false
        )

    }
}