package com.example.physio.service.impl

import android.util.Log
import com.example.physio.models.ExercisePackage
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.CacheManager
import com.example.physio.service.services.ExercisePackageService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExercisePackageServiceImpl @Inject constructor(
    private val auth: AccountService,
    private val cacheManager: CacheManager
) : ExercisePackageService {

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
            Log.e(EXERCISE_SERVICE_TAG, "getExercisePackages:Error getting exercise packages", e)
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
            Log.e(EXERCISE_SERVICE_TAG, "Error getting exercise package", e)
            null
        }
    }

    override suspend fun createExercisePackage(exercisePackage: ExercisePackage) {
        try {
            val documentReference = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .add(exercisePackage.copy(id = ""))
                .await()

            val generatedId = documentReference.id
            Log.d(EXERCISE_SERVICE_TAG, "ExercisePackage created with ID: $generatedId")

            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(generatedId)
                .update("id", generatedId, "uid", auth.currentUserId)
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
            Log.e(EXERCISE_SERVICE_TAG, "Error creating exercise package", e)
        }
    }

    override suspend fun deleteExercisePackage(exercisePackage: ExercisePackage) {
        val documentId = exercisePackage.id
        try {
            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(documentId)
                .delete()
                .await()

            Log.d(EXERCISE_SERVICE_TAG, "ExercisePackage deleted with ID: $documentId")

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

            Log.d(EXERCISE_SERVICE_TAG, "Package summary updated after deletion.")

        } catch (e: Exception) {
            Log.e(EXERCISE_SERVICE_TAG, "Error deleting exercise package with ID: $documentId", e)
        }
    }

    override suspend fun getPackage(packageId: String): ExercisePackage? {
        return try {
            val documentSnapshot = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(packageId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                Log.d(EXERCISE_SERVICE_TAG, "Package data: ${documentSnapshot.data}")
            } else {
                Log.d(EXERCISE_SERVICE_TAG, "No package found with ID: $packageId")
            }

            documentSnapshot.toObject(ExercisePackage::class.java)

        } catch (e: Exception) {
            Log.e(EXERCISE_SERVICE_TAG, "Error getting package", e)
            null
        }
    }

    override suspend fun updateExercisePackage(exercisePackage: ExercisePackage) {
        try {
            val updatedExercisePackage = exercisePackage.copy(uid = auth.currentUserId)

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

            Log.d(EXERCISE_SERVICE_TAG, "ExercisePackage updated: ${updatedExercisePackage.id}")

        } catch (e: Exception) {
            Log.e(EXERCISE_SERVICE_TAG, "Error updating exercise package", e)
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
                    EXERCISE_SERVICE_TAG,
                    "findMatchingExercisePackages: Package: ID = ${pkg.id}, desc: ${pkg.description}, Conditions = ${pkg.conditionIds}, Equipment = ${pkg.equipmentIds}"
                )
            }

            val matchingPackages = allPackages.filter { exercisePackage ->
                exercisePackage.conditionIds.containsAll(conditionIds) &&
                        exercisePackage.equipmentIds.containsAll(equipmentIds)
            }.map { Triple(it.id, it.name, it.description) }

            matchingPackages
        } catch (e: Exception) {
            Log.e(EXERCISE_SERVICE_TAG, "Error finding matching exercise packages", e)
            emptyList()
        }
    }

    companion object {
        private const val EXERCISE_SERVICE_TAG = "ExercisePackageService"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercise_packages"
        private const val SUMMARY_COLLECTION = "summaries"
    }
}
