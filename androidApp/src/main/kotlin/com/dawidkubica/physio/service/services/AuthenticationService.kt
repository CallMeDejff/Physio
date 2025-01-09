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
    suspend fun signInWithEmailVerification(
        email: String,
        password: String,
        context: Context,
        requireEmailVerification: Boolean = false
    ): Result<Unit>

    suspend fun signInWithGoogle(
        context: Context,
        token: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun signInWithFacebook(
        context: Context,
        token: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun signUp(email: String, password: String, context: Context): Result<Unit>
    suspend fun signOut()
    suspend fun updateEmail(email: String)
    suspend fun verifyEmail()
    suspend fun resetPassword(email: String)
}