package com.dawidkubica.physio.service.services

import android.content.Context
import com.dawidkubica.physio.models.User

interface AuthenticationService {
    val currentUserId: String
    suspend fun createUser(user: User)
    suspend fun changePassword(newPassword: String): Result<Unit>
    suspend fun deleteAccount()
    suspend fun hasUser(): Boolean
    suspend fun setUserInfo(userId: String): User?
    suspend fun signIn(email: String, password: String, context: Context): Result<Unit>
    suspend fun signInWithGoogle(
        token: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    suspend fun signInWithFacebook(
        token: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signOut()
    suspend fun updateEmail(email: String)
    suspend fun verifyEmail()
    suspend fun resetPassword(email: String)
}