package eu.inscico.aurora_app.services.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import eu.inscico.aurora_app.model.City
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ConsumptionResponse
import eu.inscico.aurora_app.model.country.CityResponse
import eu.inscico.aurora_app.model.user.PVInvestment
import eu.inscico.aurora_app.model.user.PVInvestmentResponse
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
    private val _consumptionSummariesService: ConsumptionSummaryService,
    private val _recurringConsumptionsService: RecurringConsumptionsService
) {

    private val userCollectionName = "users"
    private val pvInvestmentsCollectionName = "pv-investments"

    private val _userLive = MutableLiveData<User?>()
    val userLive: LiveData<User?> = _userLive
    private var _userListener: ListenerRegistration? = null

    private val _pvInvestmentsForUserLive = MutableLiveData<List<PVInvestment>?>()
    val pvInvestmentsForUserLive: LiveData<List<PVInvestment>?> = _pvInvestmentsForUserLive
    private var _pvInvestmentsListener: ListenerRegistration? = null

    init {
        val userId = _firebaseAuth.currentUser?.uid
        userId?.let {
            CoroutineScope(Dispatchers.IO).launch {
                getUserByAuthId(userId)
                setPVInvestmentsListener(userId)
            }
        }
    }

    // region: User
    // ---------------------------------------------------------------------------------------------

    suspend fun getUserByAuthId(authId: String): TypedResult<User, Boolean> {
        // Get user
        try {
            val userSnapshot =
                _firestore.collection(userCollectionName).document(authId).get().await()
            userSnapshot.let {
                if (it != null) {
                    val userResponse =
                        it.toObject<UserResponse>() ?: return TypedResult.Failure(true)
                    val user = User.from(userResponse)
                    if (user != null) {
                        _userLive.postValue(user)
                        setUserListener(authId)

                        _consumptionSummariesService.setConsumptionSummariesListener(
                            authId,
                            userCollectionName
                        )

                        _countryService.getUserCountryById(user.country)
                        if (user.city != null) {
                            _countryService.getUserCityById(user.country, user.city)
                        }

                        _consumptionsService.setConsumptionsListener(userCollectionName, authId)
                        _recurringConsumptionsService.setRecurringConsumptionsListener(
                            userCollectionName,
                            authId
                        )


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
        _userListener?.remove()

        _userListener = _firestore.collection(userCollectionName).document(authId)
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
            _firestore.collection(userCollectionName).document(authId).set(userAsMap).await()

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
            _firestore.collection(userCollectionName).document(authId).set(userAsMap).await()

            getUserByAuthId(authId)
            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    private fun parseUserToMap(user: UserResponse): MutableMap<String, Any?> {

        val userAsMap = mutableMapOf<String, Any?>()

        user.javaClass.kotlin.declaredMemberProperties.forEach {
            val value = it.getValue(user, it)
            if (value != null) {
                userAsMap[it.name] = value
            }
        }
        return userAsMap
    }

    fun deleteUser(resultCallback: (Boolean, AccountDeletionErrorType?) -> Unit) {
        val user = _firebaseAuth.currentUser ?: return

        user.delete().addOnCompleteListener { task ->
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
    ) {
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

    // endregion: User

    // region: PVInvestments
    // ---------------------------------------------------------------------------------------------

    suspend fun loadPVInvestmentsForUser(userId: String): TypedResult<List<PVInvestment>, Any> {
        _pvInvestmentsForUserLive.postValue(null)
        try {
            // Get countries
            val pvInvestmentsSnapshot = _firestore.collection(userCollectionName).document(userId)
                .collection(pvInvestmentsCollectionName).get().await()
            val pvInvestments = pvInvestmentsSnapshot.mapNotNull {
                try {
                    val pvInvestmentsResponse =
                        it.toObject<PVInvestmentResponse>() ?: return@mapNotNull null
                    PVInvestment.from(pvInvestmentsResponse)
                } catch (e: Exception) {
                    val x = e
                    null
                }
            }

            // Update countries
            _pvInvestmentsForUserLive.postValue(pvInvestments)

            return TypedResult.Success(pvInvestments)
        } catch (e: Exception) {
            return TypedResult.Failure(e.toString())
        }
    }

    private fun setPVInvestmentsListener(userId: String) {
        _pvInvestmentsListener?.remove()

        _pvInvestmentsListener = _firestore.collection(userCollectionName).document(userId)
            .collection(pvInvestmentsCollectionName)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val pvInvestments = value.mapNotNull {
                        try {
                            val pvInvestmentsResponse =
                                it.toObject<PVInvestmentResponse>() ?: return@mapNotNull null
                            PVInvestment.from(pvInvestmentsResponse)
                        } catch (e: Exception) {

                            null
                        }
                    }
                    _pvInvestmentsForUserLive.postValue(pvInvestments)
                }
            }
    }

    // endregion: PVInvestments
}
enum class AccountDeletionErrorType {
    REAUTHENTICATION,
    OTHER
}