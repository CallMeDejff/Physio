package com.example.physio.service.impl

import android.util.Log
import com.example.physio.models.Reminder
import com.example.physio.models.StorageResult
import com.example.physio.models.User
import com.example.physio.service.UserPreferences
import com.example.physio.service.services.AccountService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private var userPreferences: UserPreferences
) : AccountService {

    private val firestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<User?>(null)
    override var currentUser: Flow<User?> = _currentUser.asStateFlow()

    override var currentUserId: String = ""
        get() = Firebase.auth.currentUser?.uid.orEmpty()

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
                            email = it.email,
                            emailVerified = it.emailVerified,
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(ACCOUNT_SERVICE_TAG, "getUsersList:Error getting users: ", e)
        }
        return userList
    }

    override suspend fun getUserInfo(): User? {
        return try {
            val documentSnapshot = firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val fetchedUser = documentSnapshot.toObject(User::class.java)
                fetchedUser
            } else {
                Log.e(ACCOUNT_SERVICE_TAG, "Document for userId $currentUserId does not exist")
                null
            }
        } catch (e: Exception) {
            Log.e(ACCOUNT_SERVICE_TAG, "Error getting user info", e)
            null
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
            return try {
                val userRef = firestore.collection(USERS_COLLECTION)
                    .document(currentUserId)

                val updatedData = mapOf(
                    "name" to user.name,
                    "lastname" to user.lastname,
                    "licenseNumber" to user.licenseNumber,
                    "userType" to user.userType,
                )

                userRef.set(updatedData, SetOptions.merge()).await()

                Log.d(ACCOUNT_SERVICE_TAG, "updateUser: User updated: $user")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(ACCOUNT_SERVICE_TAG, "updateUser: Error when updating user:", e)
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

    override suspend fun addReminderForUser(reminder: Reminder): String? {
        val reminderId = UUID.randomUUID().toString()
        val reminderWithId = reminder.copy(id = reminderId)

        return try {
            firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .update("reminders", FieldValue.arrayUnion(reminderWithId))
                .await()

            reminderId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getRemindersForUser(): List<Reminder> {
        return try {
            val userDoc = firestore
                .collection(USERS_COLLECTION)
                .document(currentUserId)
                .get()
                .await()

            val reminders = userDoc.get("reminders") as? List<Map<String, Any>> ?: emptyList()

            reminders.mapNotNull { reminderMap ->
                try {
                    Reminder(
                        id = reminderMap["id"] as? String ?: "",
                        dayOfWeek = reminderMap["dayOfWeek"] as? String ?: "",
                        time = reminderMap["time"] as? String ?: "",
                        topic = reminderMap["topic"] as? String ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun deleteReminderForUser(reminderId: String) {
        try {
            val userDoc = firestore
                .collection(USERS_COLLECTION)
                .document(currentUserId)
                .get()
                .await()

            val reminders = userDoc.get("reminders") as? List<Map<String, Any>> ?: emptyList()

            val updatedReminders = reminders.filterNot { it["id"] == reminderId }

            firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .update("reminders", updatedReminders)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        private const val USERS_COLLECTION = "users"
        private const val ACCOUNT_SERVICE_TAG = "AccountService"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercise_packages"
    }
}
