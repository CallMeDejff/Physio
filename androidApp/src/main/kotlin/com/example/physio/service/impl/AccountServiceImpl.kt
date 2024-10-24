package com.example.physio.service.impl

import android.util.Log
import com.example.physio.models.StorageResult
import com.example.physio.models.User
import com.example.physio.service.UserPreferences
import com.example.physio.service.services.AccountService
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private var userPreferences: UserPreferences
) : AccountService {

    private val firestore = FirebaseFirestore.getInstance()
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    override var currentUserId: String = ""
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    init {
        Firebase.auth.addAuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            _currentUser.value = firebaseUser?.let { User(it.uid, it.displayName.toString()) }
            currentUserId = firebaseUser?.uid.orEmpty()
        }
    }

    override suspend fun getUsersList(): List<User> {
        val userList = mutableListOf<User>()
        try {
            val querySnapshot = firestore.collection(USERS_COLLECTION)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val user = document.toObject(User::class.java)
                user?.let {
                    userList.add(
                        User(
                            uid = it.uid,
                            name = it.name,
                            lastname = it.lastname,
                            email = it.email
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(ACCOUNT_SERVICE_TAG, "getUsersList:Error getting users: ", e)
        }
        return userList
    }

    override suspend fun getUserInfo(userId: String): User? {
        return try {
            val documentSnapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val fetchedUser = documentSnapshot.toObject(User::class.java)?.also {
                    if (userId == currentUserId) {
                        userPreferences.setUser(
                            it.uid,
                            it.name,
                            it.lastname,
                            it.licenseNumber,
                            it.userType
                        )
                        Log.d(ACCOUNT_SERVICE_TAG, "User set to shared preferences: $it")
                    }
                }
                fetchedUser
            } else {
                Log.e(ACCOUNT_SERVICE_TAG, "Document for userId $userId does not exist")
                null
            }
        } catch (e: Exception) {
            Log.e(ACCOUNT_SERVICE_TAG, "Error getting user info", e)
            null
        }
    }

    override suspend fun createUser(user: User) {
        try {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(user.uid)
            userDocRef.set(user).await()
            userPreferences.setUser(
                user.uid,
                user.name,
                user.lastname,
                user.licenseNumber,
                user.userType
            )
            Log.d(ACCOUNT_SERVICE_TAG, "createUser: $user")
        } catch (e: Exception) {
            Log.e(ACCOUNT_SERVICE_TAG, "createUser: Error creating user:", e)
        }
    }

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val signInResult = Firebase.auth.signInWithEmailAndPassword(email, password).await()
            if (signInResult.user != null) {
                val userId = signInResult.user?.uid
                Log.d(ACCOUNT_SERVICE_TAG, "signInWithEmailAndPassword:success:$userId")

                currentUserId = userId ?: ""

                Log.d(
                    ACCOUNT_SERVICE_TAG,
                    "signInWithEmailAndPassword:Fetching user info for userId: $currentUserId"
                )

                val userInfo = getUserInfo(currentUserId)
                if (userInfo != null) {
                    Log.d(ACCOUNT_SERVICE_TAG, "Fetched user info: $userInfo")
                    Result.success(Unit)
                } else {
                    Log.e(ACCOUNT_SERVICE_TAG, "Failed to fetch user info")
                    Result.failure(Exception("Nie udało się pobrać informacji o użytkowniku"))
                }
            } else {
                Log.e(ACCOUNT_SERVICE_TAG, "signInWithEmailAndPassword:failure - no user returned")
                Result.failure(Exception("Logowanie nie powiodło się"))
            }
        } catch (e: FirebaseNetworkException) {
            Log.e(ACCOUNT_SERVICE_TAG, "signInWithEmailAndPassword:failure", e)
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Log.e(ACCOUNT_SERVICE_TAG, "signInWithEmailAndPassword:failure", e)
            Result.failure(e)
        }
    }


    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(firebaseCredential)

        val userDocRef = firestore.collection(USERS_COLLECTION).document(currentUserId)
        val documentSnapshot = userDocRef.get().await()

        if (documentSnapshot.exists()) {
            Log.d(ACCOUNT_SERVICE_TAG, "createUser: User already exists.")
            return
        }

        val newUser = User(
            uid = Firebase.auth.currentUser?.uid ?: "",
            name = Firebase.auth.currentUser?.displayName ?: "",
            email = Firebase.auth.currentUser?.email ?: "",
        )
        createUser(newUser)
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            val signUpResult = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            if (signUpResult.user != null) {
                Firebase.auth.signInWithEmailAndPassword(email, password).await()
                Log.d(
                    ACCOUNT_SERVICE_TAG,
                    "createUserWithEmail:success:${Firebase.auth.currentUser?.uid}"
                )
                Result.success(Unit)
            } else {
                Log.e(ACCOUNT_SERVICE_TAG, "createUserWithEmail:failure - no user returned")
                Result.failure(Exception("Rejestracja nie powiodła się"))
            }
        } catch (e: FirebaseNetworkException) {
            Log.e(ACCOUNT_SERVICE_TAG, "signUp:failure", e)
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Log.e(ACCOUNT_SERVICE_TAG, "createUserWithEmail:failure", e)
            Result.failure(e)
        }
    }

    override suspend fun toggleFavoritePackage(packageId: String): StorageResult {
        return try {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(currentUserId)

            val result = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userDocRef)
                val user = snapshot.toObject(User::class.java)

                user?.let {
                    val updatedFavorites = it.favoritePackages.toMutableList()

                    return@runTransaction if (updatedFavorites.contains(packageId)) {
                        updatedFavorites.remove(packageId)
                        transaction.update(userDocRef, "favoritePackages", updatedFavorites)
                        Log.d(ACCOUNT_SERVICE_TAG, "$packageId removed from favorites.")
                        StorageResult.Removed(packageId)
                    } else {
                        updatedFavorites.add(packageId)
                        transaction.update(userDocRef, "favoritePackages", updatedFavorites)
                        Log.d(ACCOUNT_SERVICE_TAG, "$packageId added to favorites.")
                        StorageResult.Added(packageId)
                    }
                } ?: throw Exception("User document not found")
            }.await()
            result
        } catch (e: Exception) {
            Log.e(
                ACCOUNT_SERVICE_TAG,
                "toggleFavoritePackage: Error toggling packageId: $packageId",
                e
            )
            StorageResult.Failure(e)
        }
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val ACCOUNT_SERVICE_TAG = "AccountService"
    }
}
