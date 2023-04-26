package eu.inscico.aurora_app.ui.screens.login

import android.app.Activity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.SignInButton
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    navigationService: NavigationService = get()
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

        Column(
            Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Image(
                        painterResource(id = R.drawable.aurora_logo),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentDescription = "",
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                            viewModel.loginWithApple(context as Activity)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                        }

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "By continuing, you agree to AURORA's Terms of Service and Privacy Policy",
                                modifier = Modifier.padding(horizontal = 32.dp),
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }

                    }
                }
            }
        }
    }
}