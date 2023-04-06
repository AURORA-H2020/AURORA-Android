package eu.inscico.aurora_app.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import eu.inscico.aurora_app.model.Country
import eu.inscico.aurora_app.model.CountryResponse
import eu.inscico.aurora_app.model.User
import eu.inscico.aurora_app.model.UserResponse
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserService(
    private val _firestore: FirebaseFirestore,
    private val _firebaseAuth: FirebaseAuth
) {

    private val collectionName = "users"

    private val _userLive = MutableLiveData<User?>()
    val userLive: LiveData<User?> = _userLive

    private var _isUserProfileCreatedLive = MutableLiveData<Boolean>()
    val isUserProfileCreatedLive: LiveData<Boolean> = _isUserProfileCreatedLive

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
                        _isUserProfileCreatedLive.postValue(true)
                        return TypedResult.Success(user)
                    }
                    _isUserProfileCreatedLive.postValue(false)
                    return TypedResult.Failure(true)
                }
            }
            return TypedResult.Failure(true)
        } catch (e: FirebaseFirestoreException) {
            return TypedResult.Failure(true)
        }
    }

    suspend fun createUser(user: UserResponse): TypedResult<Any, String> {
        try {

            val authId = _firebaseAuth.currentUser?.uid ?: return TypedResult.Failure("")

            // Create user doc
            _firestore.collection(collectionName).document(authId).set(user).await()

            getUserByAuthId(authId)

            return TypedResult.Success(true)
        } catch (e: Exception) {
            return TypedResult.Failure(e.message ?: "")
        }
    }

    fun logout(){
        _userLive.postValue(null)
    }
}