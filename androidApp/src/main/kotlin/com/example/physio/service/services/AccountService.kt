package com.example.physio.service.services

import com.example.physio.models.FavoritePackageResult
import com.example.physio.models.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun createUser(user: User)
    suspend fun getUserInfo(userId: String): User?
    suspend fun getUsersList(): List<User>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signInWithGoogle(idToken: String)
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun toggleFavoritePackage(packageId: String): FavoritePackageResult
    suspend fun signOut()
    suspend fun deleteAccount()
}