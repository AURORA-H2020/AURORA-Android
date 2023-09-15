package eu.inscico.aurora_app.ui.screens.login

import android.app.Activity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.SignInButton
import eu.inscico.aurora_app.utils.ExternalUtils
import eu.inscico.aurora_app.utils.ExternalUtils.sendMail
import eu.inscico.aurora_app.utils.LINK_EUROPEAN_HORIZON_RESEARCH_PROGRAM
import eu.inscico.aurora_app.utils.LINK_PRIVACY_POLICY
import eu.inscico.aurora_app.utils.LINK_TERMS_OF_SERVICE
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get()
) {

    val context = LocalContext.current

    val gradientColor = if (isSystemInDarkTheme()) {
        Color.Black
    } else {
        Color.White
    }

    val isGoogleButtonLoading = remember {
        mutableStateOf(false)
    }

    val isAppleButtonLoading = remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painterResource(R.drawable.login_screen_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                // Workaround to enable alpha compositing
                .graphicsLayer { alpha = 1f }
                .drawWithContent {
                    val colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        gradientColor,
                        gradientColor
                    )
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(colors),
                        blendMode = BlendMode.SrcOver
                    )
                }
        )
    }
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painterResource(id = R.drawable.aurora_logo),
                    modifier = Modifier
                        .padding(32.dp),
                    contentDescription = "",
                )

                Text(
                    text = stringResource(id = R.string.login_aurora_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.login_aurora_description),
                    modifier = Modifier.padding(horizontal = 32.dp),
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                Spacer(modifier = Modifier.height(50.dp))

                SignInButton(
                    shape = RoundedCornerShape(16.dp),
                    text = stringResource(id = R.string.login_google_sign_in_button_text),
                    loadingText = stringResource(id = R.string.login_sign_in_button_loading_text),
                    isLoading = false,
                    icon = painterResource(id = com.google.firebase.appcheck.interop.R.drawable.googleg_standard_color_18),
                    onClick = {
                        isGoogleButtonLoading.value = true
                        viewModel.loginWithGoogle(context as Activity)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SignInButton(
                    text = stringResource(id = R.string.login_email_sign_in_button_text),
                    loadingText = stringResource(id = R.string.login_sign_in_button_loading_text),
                    isLoading = false,
                    icon = painterResource(id = R.drawable.outline_email_24),
                    iconColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        navigationService.toSignInWithEmail()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SignInButton(
                    text = stringResource(id = R.string.login_apple_sign_in_button_text),
                    loadingText = stringResource(id = R.string.login_sign_in_button_loading_text),
                    isLoading = false,
                    icon = painterResource(id = R.drawable.apple_logo_black),
                    iconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        isAppleButtonLoading.value = true
                        viewModel.loginWithApple(context as Activity) {
                            isAppleButtonLoading.value = false
                            if (!it) {
                                userFeedbackService.showSnackbar(context.getString(R.string.login_email_fail_message))
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                val textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight(500),
                    fontSize = 14.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 0.75.sp
                )

                val annotatedString = buildAnnotatedString {

                    append(stringResource(id = R.string.login_privacy_policy_consent_text_first_part))
                    append(" ")

                    pushStringAnnotation(
                        tag = stringResource(id = R.string.login_privacy_policy_consent_text_second_part),
                        annotation = LINK_EUROPEAN_HORIZON_RESEARCH_PROGRAM
                    )
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(id = R.string.login_privacy_policy_consent_text_second_part))
                    }
                    pop()

                    append(" ")
                    append(stringResource(id = R.string.login_privacy_policy_consent_text_third_part))
                    append(" ")
                    pushStringAnnotation(
                        tag = stringResource(id = R.string.login_privacy_policy_consent_text_fourth_part),
                        annotation = LINK_PRIVACY_POLICY
                    )
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(id = R.string.login_privacy_policy_consent_text_fourth_part))
                    }

                    pop()

                    append(" ")
                    append(stringResource(id = R.string.login_privacy_policy_consent_text_fiths_part))
                    append(" ")

                    pushStringAnnotation(
                        tag = stringResource(id = R.string.login_privacy_policy_consent_text_terms_of_service_text),
                        annotation = LINK_TERMS_OF_SERVICE
                    )
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(id = R.string.login_privacy_policy_consent_text_terms_of_service_text))
                    }

                    pop()

                    append(" ")
                    append(stringResource(id = R.string.login_privacy_policy_consent_text_sixth_part))
                    append(" ")

                    pushStringAnnotation(
                        tag = stringResource(id = R.string.login_privacy_policy_consent_text_privacy_policy_text),
                        annotation = LINK_PRIVACY_POLICY
                    )
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(id = R.string.login_privacy_policy_consent_text_privacy_policy_text))
                    }

                    pop()
                    append(".")
                }

                ClickableText(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .align(Alignment.CenterHorizontally),
                    text = annotatedString,
                    style = textStyle,
                    onClick = { offset ->

                        annotatedString.getStringAnnotations(
                            tag = context.getString(R.string.login_privacy_policy_consent_text_second_part),
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            ExternalUtils.openBrowser(
                                context = context,
                                url = it.item
                            )
                        }

                        annotatedString.getStringAnnotations(
                            tag = context.getString(R.string.login_privacy_policy_consent_text_fourth_part),
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            context.sendMail(context.getString(R.string.login_privacy_policy_consent_text_fourth_part))
                        }

                        annotatedString.getStringAnnotations(
                            tag = context.getString(R.string.login_privacy_policy_consent_text_terms_of_service_text),
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            ExternalUtils.openBrowser(
                                context = context,
                                url = it.item
                            )
                        }

                        annotatedString.getStringAnnotations(
                            tag = context.getString(R.string.login_privacy_policy_consent_text_privacy_policy_text),
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            ExternalUtils.openBrowser(
                                context = context,
                                url = it.item
                            )
                        }
                    })

                Spacer(modifier = Modifier.height(5.dp))
            }
        }

    }
}