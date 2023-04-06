package eu.inscico.aurora_app.core

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.ui.AuroraApp
import eu.inscico.aurora_app.ui.screens.login.CreateProfileScreen
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    private val _authService: AuthService by inject()
    private val _userService: UserService by inject()
    private val _firebaseAuth: FirebaseAuth by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AURORAEnergyTrackerTheme {
                AuroraApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        _authService.googleAccount = account
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            62443 -> {
                try {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleSignInResult(task)
                } catch (e: ApiException) {
                    // ...
                }
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            _authService.googleAccount = account

            val firebaseCredential = GoogleAuthProvider.getCredential(account.idToken, null)
            _firebaseAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val authId = task.result.user?.uid
                            if(authId != null){
                                _userService.getUserByAuthId(authId)
                            }
                        }
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