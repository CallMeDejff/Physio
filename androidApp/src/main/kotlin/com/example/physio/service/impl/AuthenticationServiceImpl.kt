package com.example.physio.service.impl

import android.util.Log
import com.example.physio.models.ExercisePackage
import com.example.physio.models.Provider
import com.example.physio.models.User
import com.example.physio.service.UserPreferences
import com.example.physio.service.services.AuthenticationService
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationServiceImpl @Inject constructor(
    private var userPreferences: UserPreferences
) : AuthenticationService {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    override var currentUserId: String = ""
        get() = Firebase.auth.currentUser?.uid.orEmpty()


    override suspend fun createUser(user: User) {
        try {
            val userDocRef = firestore.collection(USERS_COLLECTION)
                .document(user.uid)

            userDocRef
                .set(user)
                .await()

            userPreferences.setUser(
                user.uid,
                user.name,
                user.lastname,
                user.licenseNumber,
                user.userType,
            )
            Log.d(AUTHENTICATION_SERVICE_TAG, "createUser: $user")
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "createUser: Error creating user:", e)
        }
    }

    override suspend fun deleteAccount() {
        val userDocRef = firestore.collection(USERS_COLLECTION).document(currentUserId)
        val userSnapshot = userDocRef.get().await()
        val user = userSnapshot.toObject(User::class.java)

        if (user == null) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "User not found with ID: $currentUserId")
            return
        }

        for (packageId in user.assignedPackages) {
            val exercisePackageDocRef =
                firestore.collection(EXERCISE_PACKAGES_COLLECTION).document(packageId)
            val exercisePackageSnapshot = exercisePackageDocRef.get().await()
            val exercisePackage = exercisePackageSnapshot.toObject(ExercisePackage::class.java)

            if (exercisePackage != null) {
                val updatedAssignedTo = exercisePackage.assignedTo.filter { it != currentUserId }
                exercisePackageDocRef.update("assignedTo", updatedAssignedTo).await()
                Log.d(
                    AUTHENTICATION_SERVICE_TAG,
                    "Removed user $currentUserId from package $packageId"
                )
            } else {
                Log.w(AUTHENTICATION_SERVICE_TAG, "ExercisePackage not found with ID: $packageId")
            }
        }
        userDocRef.delete().await()
        Log.d(AUTHENTICATION_SERVICE_TAG, "Deleted user document for $currentUserId")
        auth.currentUser!!.delete().await()
        Log.d(AUTHENTICATION_SERVICE_TAG, "Deleted user account for $currentUserId")
        userPreferences.clearData()
        Log.d(AUTHENTICATION_SERVICE_TAG, "Cleared user preferences")
    }

    override suspend fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun setUserInfo(userId: String): User? {
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
                        Log.d(AUTHENTICATION_SERVICE_TAG, "User set to shared preferences: $it")
                    }
                }
                fetchedUser
            } else {
                Log.e(AUTHENTICATION_SERVICE_TAG, "Document for userId $userId does not exist")
                null
            }
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "Error getting user info", e)
            null
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val signInResult = Firebase.auth.signInWithEmailAndPassword(email, password).await()
            if (signInResult.user != null) {
                val userId = signInResult.user?.uid
                Log.d(AUTHENTICATION_SERVICE_TAG, "signInWithEmailAndPassword:success:$userId")

                currentUserId = userId ?: ""

                Log.d(
                    AUTHENTICATION_SERVICE_TAG,
                    "signInWithEmailAndPassword:Fetching user info for userId: $currentUserId"
                )

                val userInfo = setUserInfo(currentUserId)
                if (userInfo != null) {
                    Log.d(AUTHENTICATION_SERVICE_TAG, "Fetched user info: $userInfo")
                    Result.success(Unit)
                } else {
                    Log.e(AUTHENTICATION_SERVICE_TAG, "Failed to fetch user info")
                    Result.failure(Exception("Nie udało się pobrać informacji o użytkowniku"))
                }
            } else {
                Log.e(
                    AUTHENTICATION_SERVICE_TAG,
                    "signInWithEmailAndPassword:failure - no user returned"
                )
                Result.failure(Exception("Logowanie nie powiodło się"))
            }
        } catch (e: FirebaseNetworkException) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "signInWithEmailAndPassword:failure", e)
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "signInWithEmailAndPassword:failure", e)
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val signInResult = Firebase.auth.signInWithCredential(firebaseCredential).await()

        val userId = signInResult.user?.uid
        Log.d(AUTHENTICATION_SERVICE_TAG, "signInWithGoogle:success:$userId")
        currentUserId = userId ?: ""

        val userDocRef = firestore.collection(USERS_COLLECTION).document(currentUserId)
        val documentSnapshot = userDocRef.get().await()

        if (documentSnapshot.exists()) {
            Log.d(AUTHENTICATION_SERVICE_TAG, "createUser: User already exists.")

            val userInfo = setUserInfo(currentUserId)
            if (userInfo != null) {
                Log.d(AUTHENTICATION_SERVICE_TAG, "Fetched user info: $userInfo")
                Result.success(Unit)
            } else {
                Log.e(AUTHENTICATION_SERVICE_TAG, "Failed to fetch user info")
                Result.failure(Exception("Nie udało się pobrać informacji o użytkowniku"))
            }
            return
        }

        val newUser = User(
            uid = Firebase.auth.currentUser?.uid ?: "",
            name = Firebase.auth.currentUser?.displayName ?: "",
            email = Firebase.auth.currentUser?.email ?: "",
            provider = Provider.Google.providerId
        )
        createUser(newUser)
        Log.d(AUTHENTICATION_SERVICE_TAG, "createUser: $newUser")
        setUserInfo(currentUserId)
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            val signUpResult = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            if (signUpResult.user != null) {
                Firebase.auth.signInWithEmailAndPassword(email, password).await()
                Log.d(
                    AUTHENTICATION_SERVICE_TAG,
                    "createUserWithEmail:success:${Firebase.auth.currentUser?.uid}"
                )
                Result.success(Unit)
            } else {
                Log.e(AUTHENTICATION_SERVICE_TAG, "createUserWithEmail:failure - no user returned")
                Result.failure(Exception("Rejestracja nie powiodła się"))
            }
        } catch (e: FirebaseNetworkException) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "signUp:failure", e)
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "createUserWithEmail:failure", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun updateEmail(email: String) {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(
                    AUTHENTICATION_SERVICE_TAG,
                    "updateEmail: current user is null, can't update email"
                )
                throw FirebaseAuthException("USER_NULL", "Current user is not logged in.")
            }
            Log.d(AUTHENTICATION_SERVICE_TAG, "updateEmail: running for email: $email")

            currentUser.verifyBeforeUpdateEmail(email).await()
            Log.d(AUTHENTICATION_SERVICE_TAG, "updateEmail: email update initiated, logging out")
            signOut()
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "updateEmail: recent login required - ${e.message}")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "updateEmail: invalid credentials - ${e.message}")
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "updateEmail: unknown error - ${e.message}")
        }
    }

    override suspend fun verifyEmail() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(AUTHENTICATION_SERVICE_TAG, "verifyEmail: Email verification sent.")
                } else {
                    Log.e(AUTHENTICATION_SERVICE_TAG, "verifyEmail: Email verification failed.")
                }
            }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val AUTHENTICATION_SERVICE_TAG = "AuthenticationService"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercise_packages"
    }
}