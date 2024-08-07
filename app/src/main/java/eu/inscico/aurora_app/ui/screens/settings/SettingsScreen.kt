package eu.inscico.aurora_app.ui.screens.settings

import android.app.Activity
import android.content.Intent
import android.text.Html
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.user.UserSignInType
import eu.inscico.aurora_app.services.firebase.AccountDeletionErrorType
import eu.inscico.aurora_app.services.notification.NotificationCreationService
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.notification.NotificationService.Companion.BEHAVIOUR_CHANGED_POPUP_INTERVAL
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.ActionEntry
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.SwitchWithLabel
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.ui.components.dialogs.ReenterPasswordAndDeleteAccountDialog
import eu.inscico.aurora_app.ui.screens.settings.notifications.SettingsReminderViewModel
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.ui.theme.heatingRed
import eu.inscico.aurora_app.ui.theme.mobilityBlue
import eu.inscico.aurora_app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.*

@Composable
fun SettingsScreen(
    language: Locale = Locale.getDefault(),
    viewModel: SettingsViewModel = koinViewModel(),
    reminderViewModel: SettingsReminderViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get(),
    _notificationCreationService: NotificationCreationService = get()
) {

    val context = LocalContext.current

    // set language, only necessary for screenshot ui tests
    LocaleUtils.updateLocale(context, language)

    val showDeleteAccountDialog = remember { mutableStateOf(false) }
    val showReenterPasswordDialog = remember { mutableStateOf(false) }

    val behaviourChangeSwitch = reminderViewModel.behaviourChangesPopupActiveLive.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        val context = LocalContext.current


        AppBar(
            title = stringResource(id = R.string.settings_app_bar_title),
            hasBackNavigation = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            ScrollableContent(
                background = MaterialTheme.colorScheme.background
            ) {

                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(id = R.string.settings_profile_section_title),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))) {

                    ActionEntry(
                        title = stringResource(id = R.string.settings_profile_edit_profile_title),
                        iconRes = R.drawable.outline_account_circle_24,
                        isNavigation = true,
                        callback = {
                            navigationService.toEditProfile()
                        }
                    )

                    Divider()

                    ActionEntry(
                        title = stringResource(id = R.string.settings_profile_select_number_formatting_title),
                        iconRes = R.drawable.outline_public_24,
                        isNavigation = true,
                        callback = {
                            navigationService.toSelectRegion()
                        }
                    )

                    Divider()

                    if (viewModel.userSignInType == UserSignInType.EMAIL) {
                        ActionEntry(
                            title = stringResource(id = R.string.settings_profile_change_email_title),
                            iconRes = R.drawable.baseline_mail_outline_24,
                            isNavigation = true,
                            callback = {
                                navigationService.toUpdateUserEmail()
                            }
                        )

                        Divider()

                        ActionEntry(
                            title = stringResource(id = R.string.settings_profile_change_password_title),
                            iconRes = R.drawable.outline_vpn_key_24,
                            isNavigation = true,
                            callback = {
                                navigationService.toUpdateUserPassword()
                            }
                        )

                        Divider()

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
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(id = R.string.settings_notifications_section_title),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))) {

                    ActionEntry(
                        title = stringResource(id = R.string.settings_notifications_electricity_bill_reminder_title),
                        iconRes = R.drawable.outline_electric_bolt_24,
                        isNavigation = true,
                        iconColor = electricityYellow,
                        hasIconBackground = true,
                        callback = {
                            navigationService.toElectricityBillReminder()
                        }
                    )

                    Divider()

                    ActionEntry(
                        title = stringResource(id = R.string.settings_notifications_heating_bill_reminder_title),
                        iconRes = R.drawable.outline_local_fire_department_24,
                        isNavigation = true,
                        iconColor = heatingRed,
                        hasIconBackground = true,
                        callback = {
                            navigationService.toHeatingBillReminder()
                        }
                    )

                    Divider()

                    ActionEntry(
                        title = stringResource(id = R.string.settings_notifications_mobility_reminder_title),
                        iconRes = R.drawable.outline_directions_car_24,
                        isNavigation = true,
                        iconColor = mobilityBlue,
                        hasIconBackground = true,
                        callback = {
                            navigationService.toMobilityReminder()
                        }
                    )

                    Divider()

                    SwitchWithLabel(
                        label = stringResource(id = R.string.settings_recurring_consumptions_changed_behaviour_popup_switch),
                        state = behaviourChangeSwitch.value ?: false,
                        onStateChange = {
                            //behaviourChangeSwitch.value = it

                            if(it) {
                                val now = Calendar.getInstance()
                                val twoWeeksInterval = BEHAVIOUR_CHANGED_POPUP_INTERVAL
                                val nextPopup = now.timeInMillis + twoWeeksInterval
                                reminderViewModel.updateBehaviourChangeTime(nextPopup)
                            }

                            reminderViewModel.updateBehaviourChangePopupActive(it)
                            reminderViewModel.updateBehaviourChangePopupShowing(
                                isEnabled = it
                            )
                        })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(id = R.string.settings_data_privacy_section_title),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))) {

                    ActionEntry(
                        title = stringResource(id = R.string.settings_data_privacy_download_data_title),
                        iconRes = R.drawable.outline_download_24,
                        isNavigation = false,
                        callback = {
                            userFeedbackService.showLoadingDialog()
                            viewModel.downloadUserData{ isSuccess, jsonData ->
                                userFeedbackService.hideLoadingDialog()
                                if(isSuccess && jsonData != null){
                                    ExternalUtils.createJsonFileAndShare(context, jsonData)
                                } else {
                                    userFeedbackService.showSnackbar(R.string.settings_download_data_bulk_fail_message)
                                }
                            }
                        }
                    )

                    Divider()

                    val reenteredPasswordCallback: (String) -> Unit = { password ->
                        userFeedbackService.showLoadingDialog()
                        viewModel.reAuthenticateAndDeleteUser(activity = context as Activity, password = password, resultCallback = { isSuccessful, _ ->
                            userFeedbackService.hideLoadingDialog()
                            if(isSuccessful){
                                viewModel.userLogout(activity = context)
                            } else {
                                CoroutineScope(Dispatchers.Main).launch{
                                    userFeedbackService.showSnackbar(
                                        message = context.getString(R.string.settings_delete_account_fail_message))
                                }
                            }
                        })
                    }

                    ReenterPasswordAndDeleteAccountDialog(
                        showDialog = showReenterPasswordDialog.value,
                        reenteredPasswordCallback = reenteredPasswordCallback,
                        dismissCallback = {
                            showReenterPasswordDialog.value = false
                        })

                    ActionEntry(
                        title = stringResource(id = R.string.settings_data_privacy_delete_account_title),
                        iconRes = R.drawable.outline_delete_outline_24,
                        iconColor = MaterialTheme.colorScheme.error,
                        titleColor = MaterialTheme.colorScheme.error,
                        isNavigation = false,
                        callback = {
                            userFeedbackService.showDialog(
                                title = context.getString(R.string.dialog_account_delete_title),
                                message = context.getString(R.string.dialog_account_delete_reauthenticate_info_title),
                                confirmButtonText = context.getString(R.string.delete),
                                confirmButtonCallback = {
                                    if(viewModel.userSignInType == UserSignInType.EMAIL){
                                        showReenterPasswordDialog.value = true
                                    } else {
                                        userFeedbackService.showLoadingDialog()
                                        viewModel.reAuthenticateAndDeleteUser(activity = context as Activity, password = null, resultCallback = { isSuccessful, _ ->
                                            userFeedbackService.hideLoadingDialog()
                                            if(isSuccessful){
                                                viewModel.userLogout(activity = context)
                                            } else {
                                                CoroutineScope(Dispatchers.Main).launch{
                                                    userFeedbackService.showSnackbar(
                                                        message = context.getString(R.string.settings_delete_account_fail_message))
                                                }
                                            }
                                        })
                                    }
                                }
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(id = R.string.settings_section_support_title),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))) {

                    ActionEntry(
                        title = stringResource(id = R.string.settings_section_support_about_the_app_title),
                        iconRes = R.drawable.outline_layers_24,
                        isNavigation = false,
                        callback = {
                            ExternalUtils.openBrowser(
                                context,
                                LINK_AURORA_APP
                            )
                        }
                    )

                    Divider()

                    ActionEntry(
                        title = stringResource(id = R.string.settings_section_support_contact_title),
                        iconRes = R.drawable.baseline_question_mark_24,
                        isNavigation = false,
                        callback = {
                            if (viewModel.getSupportUrl() != null) {
                                ExternalUtils.openBrowser(context, viewModel.getSupportUrl())
                            } else {
                                // TODO:
                            }
                        }
                    )

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(id = R.string.settings_legal_information_section_title),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))) {

                    ActionEntry(
                        title = stringResource(id = R.string.settings_legal_information_imprint_title),
                        iconRes = R.drawable.outline_info_24,
                        isNavigation = false,
                        callback = {
                            ExternalUtils.openBrowser(
                                context,
                                LINK_IMPRINT
                            )
                        }
                    )

                    Divider()

                    ActionEntry(
                        title = stringResource(id = R.string.settings_legal_information_privacy_policy_title),
                        iconRes = R.drawable.outline_lock_24,
                        isNavigation = false,
                        callback = {
                            ExternalUtils.openBrowser(
                                context,
                                LINK_PRIVACY_POLICY
                            )
                        }
                    )

                    Divider()

                    ActionEntry(
                        title = stringResource(id = R.string.settings_legal_information_terms_of_service_title),
                        iconRes = R.drawable.outline_text_snippet_24,
                        isNavigation = false,
                        callback = {
                            ExternalUtils.openBrowser(
                                context,
                                LINK_TERMS_OF_SERVICE
                            )
                        }
                    )

                    Divider()

                    ActionEntry(
                        title = stringResource(id = R.string.settings_legal_information_licenses_title),
                        iconRes = R.drawable.outline_receipt_24,
                        isNavigation = false,
                        callback = {
                            OssLicensesMenuActivity.setActivityTitle(context.getString(R.string.settings_legal_information_licenses_title))
                            context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                        }
                    )
                }

                Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(horizontal = 16.dp)) {

                    Image(
                        painterResource(id = R.drawable.european_flag),
                        modifier = Modifier.padding(top = 4.dp),
                        contentDescription = "",
                    )
                    Row(modifier = Modifier.padding(start = 4.dp)) {

                        val annotatedString = buildAnnotatedString {

                            pushStringAnnotation(tag = stringResource(id = R.string.settings_section_project_text), annotation = LINK_AURORA_PROJECT_DESCRIPTION)
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append(stringResource(id = R.string.settings_section_project_text))
                            }
                            pop()

                            append(" ")

                            append(stringResource(id = R.string.settings_section_project_info_text))

                            pushStringAnnotation(tag = stringResource(id = R.string.settings_section_project_number_text), annotation = LINK_EUROPEAN_HORIZON_RESEARCH_PROGRAM)
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append(stringResource(id = R.string.settings_section_project_number_text))
                            }

                            pop()

                            append(".")
                        }

                        val textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            textAlign = TextAlign.Start,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight(500),
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                letterSpacing = 0.75.sp
                        )

                        ClickableText(
                            modifier = Modifier.align(Alignment.Top),
                            text = annotatedString,
                            style = textStyle,
                            onClick = { offset ->
                            annotatedString.getStringAnnotations(tag = context.getString(R.string.settings_section_project_text), start = offset, end = offset).firstOrNull()?.let {
                                ExternalUtils.openBrowser(
                                    context = context,
                                    url = it.item
                                )
                            }

                            annotatedString.getStringAnnotations(tag = context.getString(R.string.settings_section_project_number_text), start = offset, end = offset).firstOrNull()?.let {
                                ExternalUtils.openBrowser(
                                    context = context,
                                    url = it.item
                                )
                            }
                        })
                    }
                }
            }
        }
    }
}