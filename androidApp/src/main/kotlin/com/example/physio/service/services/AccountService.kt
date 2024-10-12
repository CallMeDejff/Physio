package com.example.physio.service.services

import com.example.physio.models.Response
import com.example.physio.models.User
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

typealias OneTapSignInResponse = Response<BeginSignInResult>
typealias SignInWithGoogleResponse = Response<Boolean>

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signOut()
    suspend fun clearCurrentUser()
    suspend fun updateCurrentUser(newUser: FirebaseUser?)
    suspend fun deleteAccount()
}