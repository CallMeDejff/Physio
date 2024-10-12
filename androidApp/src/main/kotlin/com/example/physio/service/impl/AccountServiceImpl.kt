package com.example.physio.service.impl

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.physio.R
import com.example.physio.core.Constants.DISPLAY_NAME
import com.example.physio.core.Constants.EMAIL
import com.example.physio.models.User
import com.example.physio.service.authErrors
import com.example.physio.service.services.AccountService
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AccountService {

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

    override suspend fun updateCurrentUser(newUser: FirebaseUser?) {
        _currentUser.value = newUser?.let { User(it.uid, it.displayName.toString()) }
    }

    override suspend fun clearCurrentUser() {
        _currentUser.value = null
    }

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val signInResult = Firebase.auth.signInWithEmailAndPassword(email, password).await()
            if (signInResult.user != null) {
                val userId = signInResult.user?.uid
                Log.d("AccountService", "signInWithEmailAndPassword:success:$userId")

                currentUserId = userId ?: ""

                Result.success(Unit)
            } else {
                Log.e("AccountService", "signInWithEmailAndPassword:failure - no user returned")
                Result.failure(Exception("Logowanie nie powiodło się"))
            }
        } catch (e: FirebaseNetworkException) {
            Log.e("AccountService", "signInWithEmailAndPassword:failure", e)
            Toast.makeText(
                context,
                context.getString(R.string.error_network_error),
                Toast.LENGTH_LONG
            ).show()
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Log.e("AccountService", "signInWithEmailAndPassword:failure", e)
            val errorCode = e.errorCode
            val errorMessage = authErrors[errorCode] ?: R.string.error_login_default_error
            Toast.makeText(context, context.getString(errorMessage), Toast.LENGTH_LONG).show()
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            val signUpResult = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            if (signUpResult.user != null) {
                Firebase.auth.signInWithEmailAndPassword(email, password).await()
                Log.d(
                    "AccountService",
                    "createUserWithEmail:success:${Firebase.auth.currentUser?.uid}"
                )
                Result.success(Unit)
            } else {
                Log.e("AccountService", "createUserWithEmail:failure - no user returned")
                Result.failure(Exception("Rejestracja nie powiodła się"))
            }
        } catch (e: FirebaseNetworkException) {
            Log.e("AccountService", "signUp:failure", e)
            Toast.makeText(
                context,
                context.getString(R.string.error_network_error),
                Toast.LENGTH_LONG
            ).show()
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Log.e("AccountService", "createUserWithEmail:failure", e)
            val errorCode = e.errorCode
            val errorMessage = authErrors[errorCode] ?: R.string.error_login_default_error
            Toast.makeText(context, context.getString(errorMessage), Toast.LENGTH_LONG).show()

            Result.failure(e)
        }
    }

    private suspend fun addUserToFirestore() {
        Firebase.auth.currentUser?.apply {
            val user = toUser()
            Result.success(Unit)
        }
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }
}

fun FirebaseUser.toUser() = mapOf(
    DISPLAY_NAME to displayName,
    EMAIL to email,
)