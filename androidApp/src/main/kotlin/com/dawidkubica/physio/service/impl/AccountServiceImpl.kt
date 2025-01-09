package com.dawidkubica.physio.service.impl

import android.util.Log
import com.dawidkubica.physio.models.Reminder
import com.dawidkubica.physio.models.StorageResult
import com.dawidkubica.physio.models.User
import com.dawidkubica.physio.service.UserPreferences
import com.dawidkubica.physio.service.services.AccountService
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
    private var userPreferences: UserPreferences,
) : AccountService {

    private val firestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<User?>(null)
    override var currentUser: Flow<User?> = _currentUser.asStateFlow()

    override var currentUserId: String = ""
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override suspend fun getUsersList(): List<User>? {
        return safeFirestoreCall {
            val querySnapshot = firestore.collection(USERS_COLLECTION).get().await()
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(User::class.java)?.let {
                    User(
                        uid = it.uid,
                        name = it.name,
                        lastname = it.lastname,
                        email = it.email,
                        emailVerified = it.emailVerified
                    )
                }
            }
        }
    }

    override suspend fun searchUser(userId: String): User? {
        return try {
            val querySnapshot = firestore.collection(USERS_COLLECTION)
                .whereEqualTo("uid", userId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error searching user: ${e.message}")
            null
        }
    }

    override suspend fun getUserInfo(): User? {
        return safeFirestoreCall {
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
        }
    }

    override suspend fun updateUser(user: User): Result<Unit>? {
        return safeFirestoreCall {
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
        }
    }

    override suspend fun toggleFavoritePackage(packageId: String): StorageResult? {
        return safeFirestoreCall {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(currentUserId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userDocRef)
                val user = snapshot.toObject(User::class.java)
                    ?: throw Exception("User document not found")
                val updatedFavorites = user.favoritePackages.toMutableSet()

                val result = if (updatedFavorites.contains(packageId)) {
                    updatedFavorites.remove(packageId)
                    Log.d(
                        ACCOUNT_SERVICE_TAG,
                        "toggleFavoritePackage: Package removed from favorites: $packageId"
                    )
                    StorageResult.Removed(packageId)
                } else {
                    updatedFavorites.add(packageId)
                    Log.d(
                        ACCOUNT_SERVICE_TAG,
                        "toggleFavoritePackage: Package added to favorites: $packageId"
                    )
                    StorageResult.Added(packageId)
                }

                transaction.update(userDocRef, "favoritePackages", updatedFavorites.toList())
                result
            }.await()
        }
    }

    override suspend fun addReminderForUser(reminder: Reminder): String? {
        val reminderId = UUID.randomUUID().toString()
        val reminderWithId = reminder.copy(id = reminderId)

        return safeFirestoreCall {
            firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .update("reminders", FieldValue.arrayUnion(reminderWithId))
                .await()
            reminderId
        }
    }

    override suspend fun deleteReminderForUser(reminderId: String) {
        safeFirestoreCall {
            val userDoc =
                firestore.collection(USERS_COLLECTION).document(currentUserId).get().await()
            val reminders = userDoc.get("reminders") as? List<Map<String, Any>> ?: emptyList()
            val updatedReminders = reminders.filterNot { it["id"] == reminderId }

            firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .update("reminders", updatedReminders)
                .await()
        }
    }

    override suspend fun assignPackageToUser(userId: String, packageId: String) {
        safeFirestoreCall {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(userId)
            val userSnapshot = userDocRef.get().await()
            val user = userSnapshot.toObject(User::class.java) ?: return@safeFirestoreCall

            val updatedAssignedPackages = user.assignedPackages + packageId
            userDocRef.update("assignedPackages", updatedAssignedPackages).await()
            Log.d(ACCOUNT_SERVICE_TAG, "Package $packageId assigned to user $userId")
        }
    }

    override suspend fun removePackageFromUser(userId: String, packageId: String) {
        safeFirestoreCall {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(userId)
            val userSnapshot = userDocRef.get().await()
            val user = userSnapshot.toObject(User::class.java)

            if (user == null) {
                Log.e(ACCOUNT_SERVICE_TAG, "User not found with ID: $userId")
                return@safeFirestoreCall
            }

            if (user.assignedPackages.contains(packageId)) {
                val updatedAssignedPackages =
                    user.assignedPackages.toList().filter { it != packageId }
                userDocRef.update("assignedPackages", updatedAssignedPackages).await()
                Log.d(ACCOUNT_SERVICE_TAG, "Package $packageId removed from user $userId")
            } else {
                Log.d(
                    ACCOUNT_SERVICE_TAG,
                    "Package $packageId not found in user's assigned packages: ${user.assignedPackages.toList()}"
                )
            }
        }
    }

    override suspend fun getRemindersForUser(): List<Reminder> {
        return safeFirestoreCall {
            val userDoc =
                firestore.collection(USERS_COLLECTION).document(currentUserId).get().await()
            val reminders = userDoc.get("reminders") as? List<Map<String, Any>> ?: emptyList()
            reminders.mapNotNull { reminderMap ->
                runCatching {
                    Reminder(
                        id = reminderMap["id"] as? String ?: "",
                        dayOfWeek = reminderMap["dayOfWeek"] as? String ?: "",
                        time = reminderMap["time"] as? String ?: "",
                        topic = reminderMap["topic"] as? String ?: ""
                    )
                }.getOrNull()
            }
        } ?: emptyList()
    }


    companion object {
        private const val USERS_COLLECTION = "users"
        private const val ACCOUNT_SERVICE_TAG = "AccountService"
    }

    private suspend fun <T> safeFirestoreCall(operation: suspend () -> T): T? {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e(ACCOUNT_SERVICE_TAG, "Firestore operation failed", e)
            null
        }
    }
}
