package eu.inscico.aurora_app.services.auth

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.user.UserSignInType
import eu.inscico.aurora_app.services.CountriesService
import eu.inscico.aurora_app.services.UserService
import eu.inscico.aurora_app.utils.PrefsUtils
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthService(
    private val context: Context,
    val _firebaseAuth: FirebaseAuth,
    private val _firestore: FirebaseFirestore,
    private val _userService: UserService,
    private val _countriesService: CountriesService
) {

    companion object {
        const val GOOGLE_LOGIN_RESULT_CODE = 900913
    }

    var userSignInType: UserSignInType? = null

    var wasLoggedIn: Boolean
        get() {
            return PrefsUtils.get(context, "wasLoggedIn", false)
        }
        set(value) {
            PrefsUtils.save(context, "wasLoggedIn", value)
        }

    var currentFirebaseUser: FirebaseUser?
        get() {
            return _currentFirebaseUserLive.value
        }
        set(value) {
            _currentFirebaseUserLive.value = value
        }
    private var _currentFirebaseUserLive: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val currentFirebaseUserLive: LiveData<FirebaseUser?> = _currentFirebaseUserLive

    private var _isAuthenticatedLive = MutableLiveData(
        wasLoggedIn
    )

    val isAuthenticatedLive: LiveData<Boolean> = _isAuthenticatedLive
    var isAuthenticated: Boolean
        private set(value) {
            _isAuthenticatedLive.postValue(value)
        }
        get() {
            return _isAuthenticatedLive.value ?: false
        }

    var googleAccount: GoogleSignInAccount? = null

    init {
        _firebaseAuth.addAuthStateListener {
            val authenticated = it.currentUser != null
            if (authenticated) {
                wasLoggedIn = true
                isAuthenticated = true
                currentFirebaseUser = _firebaseAuth.currentUser
                loadUser(it.currentUser?.uid ?: "")
                CoroutineScope(Dispatchers.IO).launch {
                    _countriesService.loadCountries()
                }

                userSignInType = getUserLoginType()

            } else {
                currentFirebaseUser = null
                _userService.logout()
                wasLoggedIn = false
                isAuthenticated = false
                googleAccount = null
                userSignInType = null
                PrefsUtils.clearAllPrefs(context)
            }

        }
    }

    fun loadUser(authId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _userService.getUserByAuthId(authId)
        }
    }

    fun logout(){
        _firebaseAuth.signOut()
    }

    fun deleteUser(resultCallback: (Boolean)-> Unit){
        _firebaseAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resultCallback.invoke(true)
                    logout()
                } else {
                   resultCallback.invoke(false)
                }
            }
    }

    fun getUserLoginType(): UserSignInType {
        if(googleAccount != null){
            return UserSignInType.GOOGLE
        } else {
            return UserSignInType.EMAIL
        }
        // TODO: add apple login
    }

    // region: Google
    // ---------------------------------------------------------------------------------------------

    fun loginWithGoogle(activity: Activity) {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.applicationContext.getString(R.string.google_auth_server_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        val signInIntent = mGoogleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, GOOGLE_LOGIN_RESULT_CODE)

    }

    fun getLastNameFromGoogle(): String? {
        return googleAccount?.familyName
    }

    fun getFirstNameFromGoogle(): String? {
        return googleAccount?.givenName
    }

    fun googleSignOut(activity: Activity){

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.applicationContext.getString(R.string.google_auth_server_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(activity, OnCompleteListener<Void?> {
                logout()
            })
        mGoogleSignInClient.revokeAccess()
    }

    fun googleRevokeAccess(activity: Activity, resultCallback: (Boolean)-> Unit){

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.applicationContext.getString(R.string.google_auth_server_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(activity, OnCompleteListener<Void?> {
                if(it.isSuccessful){
                    deleteUser(resultCallback)
                } else {
                    resultCallback.invoke(false)
                }
            })
    }

    // endregion

    // region: Email
    // ---------------------------------------------------------------------------------------------
    suspend fun registerWithEmailAndPassword(email: String, password: String): TypedResult<Boolean, String> {
        return try {
            val result = _firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if(result.user != null){
                TypedResult.Success(true)
            } else {
                TypedResult.Success(false)
            }
        } catch (e: Exception) {
            TypedResult.Failure("error")
        }
    }

    suspend fun loginWithEmailAndPassword(email: String, password: String): TypedResult<Boolean, String> {
        return try {
            val result = _firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if(result.user != null){
            TypedResult.Success(true)
            } else {
                TypedResult.Success(false)
            }
        } catch (e: Exception) {
            TypedResult.Failure("${e.message}")
        }
    }

    // endregion
}