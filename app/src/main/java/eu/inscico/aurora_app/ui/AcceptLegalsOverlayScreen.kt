package eu.inscico.aurora_app.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.user.*
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.dialogs.ReenterPasswordAndDeleteAccountDialog
import eu.inscico.aurora_app.utils.ExternalUtils
import eu.inscico.aurora_app.utils.LINK_PRIVACY_POLICY
import eu.inscico.aurora_app.utils.LINK_TERMS_OF_SERVICE
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AcceptLegalsOverlayScreen(
    userFeedbackService: UserFeedbackService = get(),
    navigationService: NavigationService = get(),
    viewModel: AcceptLegalsOverlayViewModel = koinViewModel()
) {

    val context = LocalContext.current

    val showAcceptLegalsDialog = remember { mutableStateOf(false) }

    val showReenterPasswordDialog = remember { mutableStateOf(false) }

    val showConfirmDeleteAccountDialog = remember {
        mutableStateOf(false)
    }

    val textStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 20.sp
    )


    val reenteredPasswordCallback: (String) -> Unit = { password ->
        userFeedbackService.showLoadingDialog()
        viewModel.reAuthenticateAndDeleteUser(
            activity = context as Activity,
            password = password,
            resultCallback = { isSuccessful, _ ->
                userFeedbackService.hideLoadingDialog()
                if (isSuccessful) {
                    viewModel.userLogout(activity = context)
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        userFeedbackService.showSnackbar(
                            message = context.getString(R.string.settings_delete_account_fail_message)
                        )
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

    val callback = object : OnLegalsAcceptClicked {
        override fun onAccept() {
            val updatedUser = UserResponse(
                city = viewModel.user.value?.city,
                country = viewModel.user.value?.country,
                firstName = viewModel.user.value?.firstName,
                lastName = viewModel.user.value?.lastName,
                gender = Gender.parseGenderToString(viewModel.user.value?.gender),
                yearOfBirth = viewModel.user.value?.yearOfBirth,
                isMarketingConsentAllowed = viewModel.user.value?.isMarketingConsentAllowed,
                householdProfile = HouseholdProfileEnum.parseHouseholdProfileToString(viewModel.user.value?.householdProfile),
                homeEnergyLabel = HomeEnergyLabel.parseHomeLabelToString(viewModel.user.value?.homeEnergyLabel),
                acceptedLegalDocumentVersion = viewModel.acceptedLegalDocumentsVersion
            )
            CoroutineScope(Dispatchers.IO).launch {
                val result = viewModel.updateUser(updatedUser)
                when (result) {
                    is TypedResult.Failure -> {
                        userFeedbackService.showSnackbar(R.string.settings_update_profile_fail_message)
                    }
                    is TypedResult.Success -> {
                        withContext(Dispatchers.Main) {
                            navigationService.navControllerTabSettings?.popBackStack()
                        }
                    }
                }
            }
        }

        override fun onReject() {

            if (viewModel.userSignInType == UserSignInType.EMAIL) {
                showReenterPasswordDialog.value = true
            } else {
                userFeedbackService.showLoadingDialog()
                viewModel.reAuthenticateAndDeleteUser(
                    activity = context as Activity,
                    password = null,
                    resultCallback = { isSuccessful, _ ->
                        userFeedbackService.hideLoadingDialog()
                        if (isSuccessful) {
                            viewModel.userLogout(activity = context)
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                userFeedbackService.showSnackbar(
                                    message = context.getString(R.string.settings_delete_account_fail_message)
                                )
                            }
                        }
                    })
            }

            showAcceptLegalsDialog.value = false
        }

    }

    AuroraScaffold(
        snackbarHost = {
            userFeedbackService.getSnackbar()
        },
        bottomBar = {

        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(id = R.string.legal_consent_dialog_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(id = R.string.legal_consent_dialog_description_full_text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = {
                    ExternalUtils.openBrowser(context, LINK_TERMS_OF_SERVICE)
                }) {
                    Text(
                        stringResource(id = R.string.settings_legal_information_terms_of_service_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(onClick = {
                    ExternalUtils.openBrowser(context, LINK_PRIVACY_POLICY)
                }) {
                    Text(
                        stringResource(id = R.string.settings_legal_information_privacy_policy_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    callback.onAccept()
                }) {
                    Text(
                        stringResource(id = R.string.legal_consent_dialog_button_confirm),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(onClick = {
                    showConfirmDeleteAccountDialog.value = true
                }) {
                    Text(
                        stringResource(id = R.string.legal_consent_dialog_button_reject),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (showConfirmDeleteAccountDialog.value) {
            userFeedbackService.showDialog(
                message = context.getString(R.string.legal_consent_rejected_dialog_description),
                confirmButtonText = context.getString(R.string.legal_consent_rejected_dialog_button_delete_forever),
                dismissButtonText = context.getString(R.string.legal_consent_rejected_dialog_button_go_back),
                confirmButtonCallback = {
                    callback.onReject()
                    showConfirmDeleteAccountDialog.value = false
                },
                dismissButtonCallback = {
                    showConfirmDeleteAccountDialog.value = false
                }
            )
        }
    }
}

interface OnLegalsAcceptClicked {
    fun onAccept()
    fun onReject()
}