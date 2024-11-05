package com.example.physio.service.services

import com.example.physio.models.Reminder
import com.example.physio.models.StorageResult
import com.example.physio.models.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String
    suspend fun getUserInfo(): User?
    suspend fun getUsersList(): List<User>
    suspend fun toggleFavoritePackage(packageId: String): StorageResult
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun addReminderForUser(reminder: Reminder): String?
    suspend fun getRemindersForUser(): List<Reminder>
    suspend fun deleteReminderForUser(reminderId: String)
}