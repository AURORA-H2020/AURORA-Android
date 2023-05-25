package eu.inscico.aurora_app.services.firebase

import android.R
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import eu.inscico.aurora_app.model.user.User
import eu.inscico.aurora_app.model.user.UserResponse
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.reflect.full.declaredMemberProperties


class UserService(
    private val _firestore: FirebaseFirestore,
    private val _firebaseAuth: FirebaseAuth,
    private val _countryService: CountriesService,
    private val _consumptionsService: ConsumptionsService,
    private val _consumptionSummariesService: ConsumptionSummaryService
) {

    private val collectionName = "users"

    private val _userLive = MutableLiveData<User?>()
    val userLive: LiveData<User?> = _userLive

    private var _listener: ListenerRegistration? = null

    init {
        val userId = _firebaseAuth.currentUser?.uid
        userId?.let {
            CoroutineScope(Dispatchers.IO).launch {
                getUserByAuthId(userId)
            }
        }
    }

    suspend fun getUserByAuthId(authId: String): TypedResult<User, Boolean> {
        // Get user
        try {
            val userSnapshot = _firestore.collection(collectionName).document(authId).get().await()
            userSnapshot.let {
                if (it != null) {
                    val userResponse =
                        it.toObject<UserResponse>() ?: return TypedResult.Failure(true)
                    val user = User.from(userResponse)
                    if (user != null) {
                        _userLive.postValue(user)
                        setUserListener(authId)

                        _consumptionSummariesService.setConsumptionSummariesListener(authId, collectionName)

                        _countryService.getUserCountryById(user.country)
                        if (user.city != null) {
                            _countryService.getUserCityById(user.country, user.city)
                        }

                        _consumptionsService.setConsumptionsListener(collectionName, authId)

                        return TypedResult.Success(user)
                    }
                    return TypedResult.Failure(true)
                }
            }
            return TypedResult.Failure(true)
        } catch (e: FirebaseFirestoreException) {
            return TypedResult.Failure(true)
        }
    }

    private fun setUserListener(authId: String) {
        _listener?.remove()

        _listener = _firestore.collection(collectionName).document(authId)
            .addSnapshotListener { value, error ->

                if (value != null) {
                    val userResponse = value.toObject<UserResponse>()
                    val user = User.from(userResponse)
                    if (user != null) {
                        _userLive.postValue(user)
                    }

                }
            }
    }

    suspend fun createUser(user: UserResponse): TypedResult<Boolean, String> {
        try {

            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")
            val userAsMap = parseUserToMap(user)

            // Create user doc
            _firestore.collection(collectionName).document(authId).set(userAsMap).await()

            getUserByAuthId(authId)

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    suspend fun updateUser(
        user: UserResponse
    ): TypedResult<Boolean, String> {
        try {
            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")

            val userAsMap = parseUserToMap(user)
            userAsMap.remove("id")
            _firestore.collection(collectionName).document(authId).set(userAsMap).await()

            getUserByAuthId(authId)
            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    private fun parseUserToMap(user: UserResponse): MutableMap<String,Any?>{

        val userAsMap = mutableMapOf<String, Any?>()

        user.javaClass.kotlin.declaredMemberProperties.forEach {
            val value = it.getValue(user, it)
            if(value != null){
                    userAsMap[it.name] = value
                }
        }
        return userAsMap
    }

    fun deleteUser( resultCallback: (Boolean, AccountDeletionErrorType?)-> Unit) {
        val user = _firebaseAuth.currentUser ?: return

        user.delete().addOnCompleteListener {task ->
            if (task.isSuccessful) {
                resultCallback.invoke(true, null)
            } else {
                if (!task.isSuccessful) {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        resultCallback.invoke(false, AccountDeletionErrorType.REAUTHENTICATION)
                    } catch (e: java.lang.Exception) {
                        resultCallback.invoke(false, AccountDeletionErrorType.OTHER)
                    }
                }
            }
        }
        /*
        _firestore.collection(collectionName).document(user.uid).delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    resultCallback.invoke(true)
                } else {
                    resultCallback.invoke(false)
                }
            }

         */
/*
        try {
            val user = _firebaseAuth.currentUser ?: return

            val email = user.email ?: return

            val credential = EmailAuthProvider
                .getCredential(email, password)

            user.reauthenticate(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    _firestore.collection(collectionName).document(user.uid).delete()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                resultCallback.invoke(true)
                            } else {
                                resultCallback.invoke(false)
                            }
                        }
                } else {
                    resultCallback.invoke(false)
                }
            }
        }catch (e: Exception) {
            resultCallback.invoke(false)
        }

 */
    }

    fun updateUserPassword(
        oldPassword: String,
        newPassword: String,
        callback: ((isSuccessful: Boolean) -> Unit)? = null
    ) {
        val user = _firebaseAuth.currentUser ?: return

        val email = user.email ?: return

        val credential = EmailAuthProvider
            .getCredential(email, oldPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener {
                        callback?.invoke(it.isSuccessful)
                    }
                } else {
                    callback?.invoke(false)
                }
            }
    }

    fun updateUserEmail(
        newEmail: String,
        password: String,
        callback: ((isSuccessful: Boolean) -> Unit)? = null
    ) {
        val user = _firebaseAuth.currentUser ?: return

        val email = user.email ?: return

        val credential = EmailAuthProvider
            .getCredential(email, password)

        user.reauthenticate(credential)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    user.updateEmail(newEmail).addOnCompleteListener {
                        sendEmailVerification()
                        callback?.invoke(it.isSuccessful)
                    }
                } else {
                    callback?.invoke(false)
                }
            }
    }

    fun sendEmailVerification(
        callback: ((isSuccessful: Boolean) -> Unit)? = null
    ){
        val user = _firebaseAuth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback?.invoke(true)
                } else {
                    callback?.invoke(false)
                }
            }

    }

    fun logout() {
        _userLive.postValue(null)
        _consumptionsService.deleteData()
        _countryService.deleteCountriesData()
        _consumptionSummariesService.deleteData()
    }
}

enum class AccountDeletionErrorType{
    REAUTHENTICATION,
    OTHER
}