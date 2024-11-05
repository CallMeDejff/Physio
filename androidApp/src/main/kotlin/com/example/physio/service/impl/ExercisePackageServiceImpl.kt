package com.example.physio.service.impl

import android.util.Log
import com.example.physio.models.ExercisePackage
import com.example.physio.models.User
import com.example.physio.models.UserPackages
import com.example.physio.models.UserSummary
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.CacheManager
import com.example.physio.service.services.ExercisePackageService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExercisePackageServiceImpl @Inject constructor(
    private val cacheManager: CacheManager
) : ExercisePackageService {

    var currentUserId: String = ""
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getExercisePackages(): List<ExercisePackage> {
        return cacheManager.getCachedExercisePackages() ?: try {
            val querySnapshot = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .get()
                .await()

            val exercisePackagesList = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(ExercisePackage::class.java)
            }

            cacheManager.setCachedExercisePackages(exercisePackagesList)
            exercisePackagesList
        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "getExercisePackages:Error getting exercise packages", e)
            emptyList()
        }
    }

    override suspend fun getExercisePackage(exercisePackageId: String): ExercisePackage? {
        return try {
            val documentSnapshot = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(exercisePackageId)
                .get()
                .await()

            documentSnapshot.toObject(ExercisePackage::class.java)
        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error getting exercise package", e)
            null
        }
    }

    override suspend fun createExercisePackage(exercisePackage: ExercisePackage) {
        try {
            val documentReference = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .add(exercisePackage.copy(id = ""))
                .await()

            val generatedId = documentReference.id
            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "ExercisePackage created with ID: $generatedId")

            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(generatedId)
                .update("id", generatedId, "uid", currentUserId)
                .await()

            val exercisePackageSummaryEntry = mapOf(
                "id" to generatedId,
                "name" to exercisePackage.name
            )

            val packagesDocument =
                firestore.collection(SUMMARY_COLLECTION).document("packages").get().await()
            val existingPackages =
                packagesDocument.get("packages") as? List<Map<String, String>> ?: emptyList()
            val updatedPackages = existingPackages + exercisePackageSummaryEntry

            firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .set(mapOf("packages" to updatedPackages))
                .await()

        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error creating exercise package", e)
        }
    }

    override suspend fun deleteExercisePackage(exercisePackage: ExercisePackage) {
        val documentId = exercisePackage.id
        try {
            val exercisePackageDocRef = firestore.collection(EXERCISE_PACKAGES_COLLECTION).document(documentId)
            val exercisePackageSnapshot = exercisePackageDocRef.get().await()
            val exercisePackageFromDb = exercisePackageSnapshot.toObject(ExercisePackage::class.java)

            if (exercisePackageFromDb == null) {
                Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "ExercisePackage not found with ID: $documentId")
                return
            }

            val assignedUsers = exercisePackageFromDb.assignedTo
            for (userId in assignedUsers) {
                removePackageFromUser(userId, documentId)
            }

            exercisePackageDocRef.delete().await()
            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "ExercisePackage deleted with ID: $documentId")

            val packagesDocument = firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .get()
                .await()

            val existingPackages =
                packagesDocument.get("packages") as? List<Map<String, String>> ?: emptyList()

            val updatedPackages = existingPackages.filter { it["id"] != documentId }

            firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .set(mapOf("packages" to updatedPackages))
                .await()

            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "Package summary updated after deletion.")

        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error deleting exercise package with ID: $documentId", e)
        }
    }


    override suspend fun getPackage(packageId: String): ExercisePackage? {
        return try {
            val documentSnapshot = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(packageId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "Package data: ${documentSnapshot.data}")
            } else {
                Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "No package found with ID: $packageId")
            }

            documentSnapshot.toObject(ExercisePackage::class.java)

        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error getting package", e)
            null
        }
    }

    override suspend fun updateExercisePackage(exercisePackage: ExercisePackage) {
        try {
            val updatedExercisePackage = exercisePackage.copy(uid = currentUserId)

            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(updatedExercisePackage.id)
                .set(updatedExercisePackage)
                .await()

            val exercisePackageSummaryEntry = mapOf(
                "id" to updatedExercisePackage.id,
                "name" to updatedExercisePackage.name
            )

            val packagesDocument =
                firestore.collection(SUMMARY_COLLECTION).document("packages").get().await()
            val existingPackages =
                packagesDocument.get("packages") as? List<Map<String, String>> ?: emptyList()
            val updatedPackages =
                existingPackages.filterNot { it["id"] == updatedExercisePackage.id } + exercisePackageSummaryEntry

            firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .set(mapOf("packages" to updatedPackages))
                .await()

            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "ExercisePackage updated: ${updatedExercisePackage.id}")

        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error updating exercise package", e)
        }
    }

    override suspend fun findMatchingExercisePackages(
        conditionIds: List<String>,
        equipmentIds: List<String>
    ): List<Triple<String, String, String>> {
        return try {
            val allPackages = getExercisePackages()

            allPackages.forEach { pkg ->
                Log.d(
                    EXERCISE_PACKAGE_SERVICE_TAG,
                    "findMatchingExercisePackages: Package: ID = ${pkg.id}, desc: ${pkg.description}, Conditions = ${pkg.conditionIds}, Equipment = ${pkg.equipmentIds}"
                )
            }

            val matchingPackages = allPackages.filter { exercisePackage ->
                exercisePackage.conditionIds.containsAll(conditionIds) &&
                        exercisePackage.equipmentIds.containsAll(equipmentIds)
            }.map { Triple(it.id, it.name, it.description) }

            matchingPackages
        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error finding matching exercise packages", e)
            emptyList()
        }
    }

    override suspend fun getUserExercisePackages(): UserPackages {
        return cacheManager.getCachedUserPackages() ?: try {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(currentUserId)
            val documentSnapshot = userDocRef.get().await()
            val user = documentSnapshot.toObject(User::class.java)

            val favoritePackages = user?.favoritePackages?.map { packageId ->
                getExercisePackage(packageId)
            } ?: emptyList()

            val assignedPackages = user?.assignedPackages?.map { packageId ->
                getExercisePackage(packageId)
            } ?: emptyList()

            val userPackages = UserPackages(
                favoritePackages = favoritePackages.filterNotNull(),
                assignedPackages = assignedPackages.filterNotNull()
            )

            cacheManager.setCachedUserPackages(userPackages)
            userPackages
        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error getting exercise packages", e)
            UserPackages()
        }
    }

    override suspend fun assignPackageToUser(userId: String, packageId: String) {
        try {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(userId)
            val userSnapshot = userDocRef.get().await()
            val user = userSnapshot.toObject(User::class.java)

            if (user == null) {
                Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "User not found with ID: $userId")
                return
            }

            val updatedAssignedPackages = user.assignedPackages + packageId
            userDocRef.update("assignedPackages", updatedAssignedPackages).await()

            val exercisePackageDocRef = firestore.collection(EXERCISE_PACKAGES_COLLECTION).document(packageId)
            val exercisePackageSnapshot = exercisePackageDocRef.get().await()
            val exercisePackage = exercisePackageSnapshot.toObject(ExercisePackage::class.java)

            if (exercisePackage == null) {
                Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "ExercisePackage not found with ID: $packageId")
                return
            }
            val updatedAssignedTo = exercisePackage.assignedTo + userId
            exercisePackageDocRef.update("assignedTo", updatedAssignedTo).await()

            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "Package $packageId assigned to user $userId")

        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error assigning package to user", e)
        }
    }

    override suspend fun removePackageFromUser(userId: String, packageId: String) {
        try {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(userId)
            val userSnapshot = userDocRef.get().await()
            val user = userSnapshot.toObject(User::class.java)

            if (user == null) {
                Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "User not found with ID: $userId")
                return
            }

            val updatedAssignedPackages = user.assignedPackages.filter { it != packageId }
            userDocRef.update("assignedPackages", updatedAssignedPackages).await()

            val exercisePackageDocRef = firestore.collection(EXERCISE_PACKAGES_COLLECTION).document(packageId)
            val exercisePackageSnapshot = exercisePackageDocRef.get().await()
            val exercisePackage = exercisePackageSnapshot.toObject(ExercisePackage::class.java)

            if (exercisePackage == null) {
                Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "ExercisePackage not found with ID: $packageId")
                return
            }

            val updatedAssignedTo = exercisePackage.assignedTo.filter { it != userId }
            exercisePackageDocRef.update("assignedTo", updatedAssignedTo).await()

            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "Package $packageId removed from user $userId")

        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error removing package from user", e)
        }
    }


    companion object {
        private const val EXERCISE_PACKAGE_SERVICE_TAG = "ExercisePackageService"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercise_packages"
        private const val SUMMARY_COLLECTION = "summaries"
        private const val USERS_COLLECTION = "users"
    }
}
