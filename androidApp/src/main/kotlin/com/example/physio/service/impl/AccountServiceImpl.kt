package com.example.physio.service.impl

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.physio.service.services.AccountService
import com.example.physio.service.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.example.physio.R
import com.example.physio.service.authErrors
import com.google.firebase.FirebaseNetworkException
import dagger.hilt.android.qualifiers.ApplicationContext

class AccountServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AccountService {

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) })
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val signInResult = Firebase.auth.signInWithEmailAndPassword(email, password).await()
            if (signInResult.user != null) {
                Log.d("AccountService", "signInWithEmailAndPassword:success:${signInResult.user?.uid}")
                Result.success(Unit)
            } else {
                Log.e("AccountService", "signInWithEmailAndPassword:failure - no user returned")
                Result.failure(Exception("Logowanie nie powiodło się"))
            }
        } catch (e: FirebaseNetworkException) {
            Log.e("AccountService", "signInWithEmailAndPassword:failure", e)
            Toast.makeText(context, context.getString(R.string.error_network_error), Toast.LENGTH_LONG).show()
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
            if (signUpResult.user!= null) {
                Firebase.auth.signInWithEmailAndPassword(email, password).await()
                Log.d("AccountService", "createUserWithEmail:success:${Firebase.auth.currentUser?.uid}")
                Result.success(Unit)
            } else {
                Log.e("AccountService", "createUserWithEmail:failure - no user returned")
                Result.failure(Exception("Rejestracja nie powiodła się"))
            }
        } catch (e: FirebaseNetworkException) {
            Log.e("AccountService", "signUp:failure", e)
            Toast.makeText(context, context.getString(R.string.error_network_error), Toast.LENGTH_LONG).show()
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Log.e("AccountService", "createUserWithEmail:failure", e)
            val errorCode = e.errorCode
            val errorMessage = authErrors[errorCode] ?: R.string.error_login_default_error
            Toast.makeText(context, context.getString(errorMessage), Toast.LENGTH_LONG).show()

            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }
}