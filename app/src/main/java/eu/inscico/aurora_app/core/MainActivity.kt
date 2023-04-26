package eu.inscico.aurora_app.core

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.services.auth.AuthService.Companion.GOOGLE_LOGIN_RESULT_CODE
import eu.inscico.aurora_app.services.navigation.NavGraphDirections
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.notification.NotificationPermissionHandler
import eu.inscico.aurora_app.services.notification.NotificationService
import eu.inscico.aurora_app.ui.AuroraApp
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    private val _authService: AuthService by inject()
    private val _notificationService: NotificationService by inject()
    private val _firebaseAuth: FirebaseAuth by inject()
    private val _navigationService: NavigationService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init notification permission handler
        _notificationService.notificationPermissionHandler =
            NotificationPermissionHandler(_activity = this)

        if (_notificationService.electricityReminderActivePrefs
            || _notificationService.heatingReminderActivePrefs
            || _notificationService.mobilityReminderActivePrefs
        ) {
            try {
                _notificationService.notificationPermissionHandler?.checkAndHandleNotificationPermission()
            } catch (e: Exception) {
            }
        }

        setContent {
            AURORAEnergyTrackerTheme {
                AuroraApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // check google sign in active
        val account = GoogleSignIn.getLastSignedInAccount(this)
        _authService.googleAccount = account
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GOOGLE_LOGIN_RESULT_CODE -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleGoogleSignInResult(task)
                } catch (e: ApiException) {
                    // ...
                }
            }
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            _authService.googleAccount = account

            val firebaseCredential = GoogleAuthProvider.getCredential(account.idToken, null)
            _firebaseAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        _navigationService.navControllerAuth?.popBackStack(
                            route = NavGraphDirections.Auth.getNavRoute(),
                            inclusive = false
                        )
                    } else {
                        // If sign in fails, display a message to the user.

                    }
                }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

}