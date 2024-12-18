package com.dawidkubica.physio.service.impl

import android.content.Context
import android.util.Log
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.models.Provider
import com.dawidkubica.physio.models.User
import com.dawidkubica.physio.service.AuthError
import com.dawidkubica.physio.service.UserPreferences
import com.dawidkubica.physio.service.authErrors
import com.dawidkubica.physio.service.services.AuthenticationService
import com.facebook.login.LoginManager
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FacebookAuthProvider
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

    override suspend fun changePassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("User is not logged in"))

            user.updatePassword(newPassword).await()
            Log.d(AUTHENTICATION_SERVICE_TAG, "changePassword: Password updated successfully")
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "changePassword: Error updating password", e)
            Result.failure(e)
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

    override suspend fun signInWithEmailVerification(
        email: String,
        password: String,
        context: Context,
        requireEmailVerification: Boolean
    ): Result<Unit> {
        return try {
            val signInResult = auth.signInWithEmailAndPassword(email, password).await()
            val currentUser = signInResult.user

            if (currentUser != null) {
                if (requireEmailVerification && !currentUser.isEmailVerified) {
                    Result.failure(AuthError("Adres e-mail nie został zweryfikowany. Sprawdź swoją skrzynkę e-mail."))
                } else {
                    val userInfo = setUserInfo(currentUser.uid)
                    if (userInfo != null) {
                        updateEmailVerificationStatusInFirestore(currentUser.uid, currentUser.isEmailVerified)
                        Result.success(Unit)
                    } else {
                        Result.failure(AuthError("Nie udało się pobrać informacji o użytkowniku."))
                    }
                }
            } else {
                Result.failure(AuthError("Logowanie nie powiodło się. Użytkownik nie został znaleziony."))
            }
        } catch (e: FirebaseAuthException) {
            val errorCode = e.errorCode
            val errorMessage =
                authErrors[errorCode]?.let { context.getString(it) } ?: e.message ?: "Nieznany błąd"
            Log.e(AUTHENTICATION_SERVICE_TAG, "signInWithEmailVerification:failure", e)
            Result.failure(AuthError(errorMessage, errorCode))
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "signInWithEmailVerification:unknown error", e)
            Result.failure(AuthError("Wystąpił nieoczekiwany błąd.", null))
        }
    }

    private suspend fun updateEmailVerificationStatusInFirestore(userId: String, isEmailVerified: Boolean) {
        try {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(userId)
            userDocRef.update("emailVerified", isEmailVerified).await()
            Log.d(AUTHENTICATION_SERVICE_TAG, "Email verification status updated for user $userId")
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "Failed to update email verification status for user $userId", e)
        }
    }

    override suspend fun signInWithFacebook(
        context: Context,
        token: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val credential = FacebookAuthProvider.getCredential(token)
        try {
            val signInResult = auth.signInWithCredential(credential).await()
            currentUserId = signInResult.user?.uid ?: ""

            fetchUserLoginInfo(Provider.Facebook)
            onSuccess()
        } catch (e: FirebaseAuthException) {
            val errorCode = e.errorCode
            val errorMessage = authErrors[errorCode]?.let { context.getString(it) } ?: e.message
            Log.e(AUTHENTICATION_SERVICE_TAG, "signInWithFacebook:failure", e)
            LoginManager.getInstance().logOut()
            onFailure(AuthError(errorMessage ?: "Nieznany błąd", errorCode))
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "signInWithFacebook:unknown error", e)
            LoginManager.getInstance().logOut()
            onFailure(AuthError("Wystąpił nieoczekiwany błąd.", null))
        }
    }

    override suspend fun signInWithGoogle(
        context: Context,
        token: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        try {
            val signInResult = auth.signInWithCredential(credential).await()
            Log.d(AUTHENTICATION_SERVICE_TAG, "signInWithGoogle:success:${signInResult.user?.uid}")
            currentUserId = signInResult.user?.uid ?: ""

            fetchUserLoginInfo(Provider.Google)
            onSuccess()
        } catch (e: FirebaseAuthException) {
            val errorCode = e.errorCode
            val errorMessage = authErrors[errorCode]?.let { context.getString(it) } ?: e.message
            Log.e(AUTHENTICATION_SERVICE_TAG, "signInWithGoogle:failure", e)
            onFailure(AuthError(errorMessage ?: "Nieznany błąd", errorCode))
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "signInWithGoogle:unknown error", e)
            onFailure(AuthError("Wystąpił nieoczekiwany błąd.", null))
        }
    }

    override suspend fun createUser(user: User) {
        try {
            saveUser(user)
            Log.d(AUTHENTICATION_SERVICE_TAG, "createUser: User created successfully: $user")
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "createUser: Error creating user", e)
        }
    }

    override suspend fun setUserInfo(userId: String): User? {
        return fetchAndProcessUser(userId) { user ->
            if (userId == currentUserId) {
                userPreferences.setUser(
                    user.uid,
                    user.name,
                    user.lastname,
                    user.licenseNumber,
                    user.userType,
                    user.provider
                )
                Log.d(AUTHENTICATION_SERVICE_TAG, "User info updated in preferences")
            }
        }
    }

    private suspend fun fetchUserLoginInfo(provider: Provider) {
        fetchAndProcessUser(currentUserId) { user ->
            Log.d(AUTHENTICATION_SERVICE_TAG, "Fetched user info: $user")
            userPreferences.setUser(
                user.uid,
                user.name,
                user.lastname,
                user.licenseNumber,
                user.userType,
                user.provider
            )
        } ?: run {
            val newUser = User(
                uid = auth.currentUser?.uid.orEmpty(),
                name = auth.currentUser?.displayName.orEmpty(),
                email = auth.currentUser?.email.orEmpty(),
                provider = provider.providerId,
                emailVerified = auth.currentUser?.isEmailVerified ?: false
            )
            saveUser(newUser)
            Log.d(AUTHENTICATION_SERVICE_TAG, "Created new user: $newUser")
                setUserInfo(currentUserId)

        }
    }



    private suspend fun fetchAndProcessUser(
        userId: String,
        onUserFetched: (User) -> Unit
    ): User? {
        return try {
            val documentSnapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                user?.let { onUserFetched(it) }
                user
            } else {
                Log.e(AUTHENTICATION_SERVICE_TAG, "User document does not exist for ID: $userId")
                null
            }
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "Error fetching user", e)
            null
        }
    }

    private suspend fun saveUser(user: User) {
        firestore.collection(USERS_COLLECTION)
            .document(user.uid)
            .set(user)
            .await()

        userPreferences.setUser(
            user.uid,
            user.name,
            user.lastname,
            user.licenseNumber,
            user.userType,
            user.provider
        )
    }

    override suspend fun signUp(email: String, password: String, context: Context): Result<Unit> {
        return try {
            val signUpResult = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            if (signUpResult.user != null) {
                Firebase.auth.signInWithEmailAndPassword(email, password).await()
                Result.success(Unit)
            } else {
                Result.failure(AuthError("Rejestracja nie powiodła się"))
            }
        } catch (e: FirebaseAuthException) {
            val errorCode = e.errorCode
            val errorMessage =
                authErrors[errorCode]?.let { context.getString(it) } ?: e.message ?: "Nieznany błąd"
            Log.e(AUTHENTICATION_SERVICE_TAG, "createUserWithEmail:failure", e)
            Result.failure(AuthError(errorMessage, errorCode))
        } catch (e: Exception) {
            Log.e(AUTHENTICATION_SERVICE_TAG, "createUserWithEmail:unknown error", e)
            Result.failure(AuthError("Wystąpił nieoczekiwany błąd.", null))
        }
    }

    override suspend fun signOut() {
        if (userPreferences.getAccountProvider() == Provider.Facebook.providerId) {
            LoginManager.getInstance().logOut()
        }
        userPreferences.clearData()
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

    override suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(AUTHENTICATION_SERVICE_TAG, "resetPassword: Password reset email sent.")
                } else {
                    Log.e(AUTHENTICATION_SERVICE_TAG, "resetPassword: Password reset email failed.")
                }
            }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val AUTHENTICATION_SERVICE_TAG = "AuthenticationService"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercise_packages"
    }
}