package com.example.physio.service.impl

import android.util.Log
import com.example.physio.models.Exercise
import com.example.physio.models.ExercisePackage
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.StorageSampleDataService
import com.example.physio.service.services.StorageService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class StorageSampleImpl @Inject constructor(
    private val auth: AccountService,
    private val storage: StorageService,
) : StorageSampleDataService {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun setSampleData() {

        val equipmentData = mapOf(
            "equipment" to listOf(
                mapOf("version" to 1.0),
                mapOf("id" to "equipment_1", "name" to "Dumbbells"),
                mapOf("id" to "equipment_2", "name" to "Resistance Bands"),
                mapOf("id" to "equipment_3", "name" to "Kettlebells"),
                mapOf("id" to "equipment_4", "name" to "Yoga Mat")
            )
        )

        val conditionsData = mapOf(
            "conditions" to listOf(
                mapOf("version" to 1.0),
                mapOf("id" to "condition_1", "name" to "Hypertension"),
                mapOf("id" to "condition_2", "name" to "Diabetes"),
                mapOf("id" to "condition_3", "name" to "Arthritis"),
                mapOf("id" to "condition_4", "name" to "Asthma")
            )
        )

        val sampleExercise = Exercise(
            id = "",
            title = "Sample Exercise",
            description = "This is a sample exercise description.",
            uid = "sampleUserId",
            equipmentIds = listOf("equipment_1", "equipment_2"),
            mediaUrls = emptyList(),
        )

        val sampleExercisePackage = ExercisePackage(
            id = "",
            name = "Sample Package",
            description = "This is a sample package description.",
            uid = "sampleUserId",
            conditionIds = listOf("condition_1"),
            equipmentIds = listOf("equipment_1", "equipment_2"),
            exerciseIds = listOf()
        )

        try {
            val equipmentDocument =
                firestore.collection(SUMMARY_COLLECTION).document("equipment").get().await()
            if (!equipmentDocument.exists()) {
                firestore.collection(SUMMARY_COLLECTION)
                    .document("equipment")
                    .set(equipmentData)
                    .await()
                Log.d(STORAGE_SERVICE_TAG, "Inserted equipment data.")
            } else {
                Log.d(STORAGE_SERVICE_TAG, "Equipment data already exists.")
            }

            val conditionsDocument =
                firestore.collection(SUMMARY_COLLECTION).document("conditions").get().await()
            if (!conditionsDocument.exists()) {
                firestore.collection(SUMMARY_COLLECTION)
                    .document("conditions")
                    .set(conditionsData)
                    .await()
                Log.d(STORAGE_SERVICE_TAG, "Inserted conditions data.")
            } else {
                Log.d(STORAGE_SERVICE_TAG, "Conditions data already exists.")
            }

            val exercisesDocument =
                firestore.collection(SUMMARY_COLLECTION).document("exercises").get().await()
            val existingExercises =
                exercisesDocument.get("exercises") as? List<Map<String, String>> ?: emptyList()
            if (existingExercises.isEmpty()) {
                val exerciseRef =
                    firestore.collection(EXERCISES_COLLECTION).add(sampleExercise).await()
                val exerciseId = exerciseRef.id
                firestore.collection(EXERCISES_COLLECTION).document(exerciseId)
                    .update("id", exerciseId).await()

                val exerciseSummaryEntry =
                    mapOf("id" to exerciseId, "title" to sampleExercise.title)
                firestore.collection(SUMMARY_COLLECTION)
                    .document("exercises")
                    .set(mapOf("exercises" to listOf(exerciseSummaryEntry)))
                    .await()

                Log.d(STORAGE_SERVICE_TAG, "Inserted sample exercise data.")
            } else {
                Log.d(STORAGE_SERVICE_TAG, "Exercises data already exists.")
            }

            val packagesDocument =
                firestore.collection(SUMMARY_COLLECTION).document("packages").get().await()
            val existingPackages =
                packagesDocument.get("packages") as? List<Map<String, String>> ?: emptyList()
            if (existingPackages.isEmpty()) {
                val updatedSampleExercisePackage =
                    sampleExercisePackage.copy(exerciseIds = listOf(sampleExercise.id))
                val packageRef = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                    .add(updatedSampleExercisePackage).await()
                val packageId = packageRef.id
                firestore.collection(EXERCISE_PACKAGES_COLLECTION).document(packageId)
                    .update("id", packageId).await()

                val packageSummaryEntry =
                    mapOf("id" to packageId, "name" to sampleExercisePackage.name)
                firestore.collection(SUMMARY_COLLECTION)
                    .document("packages")
                    .set(mapOf("packages" to listOf(packageSummaryEntry)))
                    .await()

                Log.d(STORAGE_SERVICE_TAG, "Inserted sample exercise package data.")
            } else {
                Log.d(STORAGE_SERVICE_TAG, "Exercise packages data already exists.")
            }

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error inserting sample data", e)
        }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val STORAGE_SERVICE_TAG = "StorageSampleDataService"
        private const val EXERCISES_COLLECTION = "exercises"
        private const val SUMMARY_COLLECTION = "summaries"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercise_packages"

        private var cachedExercisesListTimestamp: Long = 0
        private var cachedExercisePackagesTimestamp: Long = 0
        private var cachedEquipmentListTimestamp: Long = 0
        private var cachedConditionsListTimestamp: Long = 0
    }
}