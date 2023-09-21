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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(id = R.string.login_privacy_policy_consent_text_full_text),
                    style = textStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.clickable {
                            ExternalUtils.openBrowser(context, LINK_EUROPEAN_HORIZON_RESEARCH_PROGRAM)
                        },
                        text = stringResource(id = R.string.login_privacy_policy_consent_button_project),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        modifier = Modifier.clickable {
                            context.sendMail(context.getString(R.string.login_privacy_policy_consent_text_support_email))
                        },
                        text = stringResource(id = R.string.login_privacy_policy_consent_button_email),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.clickable {
                            ExternalUtils.openBrowser(context, LINK_TERMS_OF_SERVICE)
                        },
                        text = stringResource(id = R.string.settings_legal_information_terms_of_service_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        modifier = Modifier.clickable {
                            ExternalUtils.openBrowser(context, LINK_PRIVACY_POLICY)
                        },
                        text = stringResource(id = R.string.settings_legal_information_privacy_policy_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Spacer(modifier = Modifier.height(5.dp))
            }
        }

    }
}