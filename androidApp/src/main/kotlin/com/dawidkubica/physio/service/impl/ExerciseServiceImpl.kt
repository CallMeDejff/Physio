package com.dawidkubica.physio.service.impl

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.dawidkubica.physio.models.Exercise
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.service.services.AuthenticationService
import com.dawidkubica.physio.service.services.ExerciseService
import com.dawidkubica.physio.service.services.FileStorageService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExerciseServiceImpl @Inject constructor(
    private val auth: AuthenticationService,
    private val fileStorageService: FileStorageService,
) : ExerciseService {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override suspend fun getExercise(exerciseId: String): Exercise? {
        return try {
            val documentSnapshot = firestore.collection(EXERCISES_COLLECTION)
                .document(exerciseId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                Log.d(EXERCISE_SERVICE_TAG, "Exercise data: ${documentSnapshot.data}")
            } else {
                Log.d(EXERCISE_SERVICE_TAG, "No exercise found with ID: $exerciseId")
            }

            documentSnapshot.toObject(Exercise::class.java)

        } catch (e: Exception) {
            Log.e(EXERCISE_SERVICE_TAG, "Error getting exercise", e)
            null
        }
    }

    override suspend fun getEquipmentIdsForExercises(exerciseIds: List<String>): Map<String, List<String>> {
        val exerciseEquipmentMap = mutableMapOf<String, List<String>>()

        try {
            for (exerciseId in exerciseIds) {
                val documentSnapshot = firestore.collection(EXERCISES_COLLECTION)
                    .document(exerciseId)
                    .get()
                    .await()

                if (documentSnapshot.exists()) {
                    val equipmentIds = documentSnapshot.get("equipmentIds") as? List<String>
                    if (equipmentIds != null) {
                        val uniqueEquipmentIds = equipmentIds.toSet().toList()
                        exerciseEquipmentMap[exerciseId] = uniqueEquipmentIds
                    } else {
                        Log.w(
                            EXERCISE_SERVICE_TAG,
                            "No equipmentId found for exercise with ID: $exerciseId"
                        )
                    }
                } else {
                    Log.w(EXERCISE_SERVICE_TAG, "No exercise found with ID: $exerciseId")
                }
            }
        } catch (e: Exception) {
            Log.e(EXERCISE_SERVICE_TAG, "Error fetching equipmentIds for exercises", e)
        }
        return exerciseEquipmentMap
    }

    @SuppressLint("SuspiciousIndentation")
    override suspend fun createExerciseWithMedia(exercise: Exercise, mediaUris: List<Uri>) {
        try {
            val mediaUrls =
                fileStorageService.uploadFilesToFirebase(mediaUris, path = auth.currentUserId)
            val exerciseWithMedia = exercise.copy(mediaUrls = mediaUrls)

            val documentReference = firestore.collection(EXERCISES_COLLECTION)
                .add(exerciseWithMedia)
                .await()

            val generatedId = documentReference.id
            Log.d(EXERCISE_SERVICE_TAG, "Exercise created with ID: $generatedId")

            firestore.collection(EXERCISES_COLLECTION)
                .document(generatedId)
                .update("id", generatedId, "uid", auth.currentUserId)
                .await()

            val exerciseSummaryEntry = mapOf(
                "id" to generatedId,
                "title" to exercise.title,
                "nonPublic" to exercise.nonPublic,
                "uid" to auth.currentUserId
            )

            firestore.collection(SUMMARY_COLLECTION)
                .document("exercises")
                .update("exercises", FieldValue.arrayUnion(exerciseSummaryEntry))
                .await()

        } catch (e: Exception) {
            Log.e(EXERCISE_SERVICE_TAG, "Error creating exercise with media", e)
        }
    }

    override suspend fun updateExercise(exercise: Exercise, mediaUris: List<Uri>?) {
        try {
            val updatedMediaUrls = if (!mediaUris.isNullOrEmpty()) {
                fileStorageService.uploadFilesToFirebase(mediaUris, path = exercise.id)
            } else {
                exercise.mediaUrls
            }

            val updatedExercise =
                exercise.copy(mediaUrls = updatedMediaUrls, uid = auth.currentUserId)

            val exerciseRef = firestore.collection(EXERCISES_COLLECTION)
                .document(exercise.id)
            exerciseRef.set(updatedExercise).await()
            Log.d(EXERCISE_SERVICE_TAG, "Exercise updated with ID: ${exercise.id}")

            val exerciseSummaryEntry = mapOf(
                "id" to exercise.id,
                "title" to exercise.title,
                "nonPublic" to exercise.nonPublic,
                "uid" to auth.currentUserId
            )

            val exercisesDocument =
                firestore.collection(SUMMARY_COLLECTION).document("exercises").get().await()
            val existingExercises =
                exercisesDocument.get("exercises") as? List<Map<String, String>> ?: emptyList()
            val updatedExercises =
                existingExercises.filterNot { it["id"] == exercise.id } + exerciseSummaryEntry

            firestore.collection(SUMMARY_COLLECTION)
                .document("exercises")
                .set(mapOf("exercises" to updatedExercises))
                .await()

        } catch (e: Exception) {
            Log.e(EXERCISE_SERVICE_TAG, "Error updating exercise", e)
        }
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        Log.d(
            EXERCISE_SERVICE_TAG,
            "deleteExercise: Deleting exercise with ID: ${exercise.id}, mediaUrls: ${exercise.mediaUrls}"
        )

        val documentId = exercise.id
        try {
            deleteExerciseMedia(exercise)

            firestore.collection(EXERCISES_COLLECTION)
                .document(documentId)
                .delete()
                .await()

            Log.d(EXERCISE_SERVICE_TAG, "Exercise deleted with ID: $documentId")
            updateExerciseSummary(documentId)
            removeExerciseReferencesFromPackages(documentId)

        } catch (e: Exception) {
            Log.e(EXERCISE_SERVICE_TAG, "Error deleting exercise with ID: $documentId", e)
        }
    }

    private suspend fun deleteExerciseMedia(exercise: Exercise) {
        Log.d(
            EXERCISE_SERVICE_TAG,
            "deleteExerciseMedia: Deleting exercise with ID: ${exercise.id}, mediaUrls: ${exercise.mediaUrls}"
        )
        exercise.mediaUrls.forEach { mediaUrl ->
            try {
                val storagePath = fileStorageService.getStoragePathFromUrl(mediaUrl)
                val storageRef = storage.reference.child(storagePath)
                storageRef.delete().await()
                Log.d(EXERCISE_SERVICE_TAG, "Deleted media file: $mediaUrl")
            } catch (e: Exception) {
                Log.e(EXERCISE_SERVICE_TAG, "Error deleting media file: $mediaUrl", e)
            }
        }
    }

    private suspend fun updateExerciseSummary(exerciseId: String) {
        val exercisesDocument = firestore.collection(SUMMARY_COLLECTION)
            .document("exercises")
            .get()
            .await()

        val existingExercises =
            exercisesDocument.get("exercises") as? List<Map<String, String>> ?: emptyList()
        val updatedExercises = existingExercises.filterNot { it["id"] == exerciseId }

        firestore.collection(SUMMARY_COLLECTION)
            .document("exercises")
            .set(mapOf("exercises" to updatedExercises))
            .await()

        Log.d(EXERCISE_SERVICE_TAG, "Exercise summary updated after deletion.")
    }

    private suspend fun removeExerciseReferencesFromPackages(exerciseId: String) {
        val packages = firestore.collection(EXERCISE_PACKAGES_COLLECTION).get().await()
        for (packageDoc in packages) {
            val exercisePackage = packageDoc.toObject(ExercisePackage::class.java)
            val updatedExerciseIds = exercisePackage.exerciseIds.filterNot { it == exerciseId }

            if (updatedExerciseIds.size != exercisePackage.exerciseIds.size) {
                firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                    .document(packageDoc.id)
                    .update("exerciseIds", updatedExerciseIds)
                    .await()

                Log.d(
                    EXERCISE_SERVICE_TAG,
                    "Removed exercise reference from package ID: ${packageDoc.id}"
                )
            }
        }
    }

    companion object {
        private const val EXERCISE_SERVICE_TAG = "ExerciseService"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercise_packages"
        private const val EXERCISES_COLLECTION = "exercises"
        private const val SUMMARY_COLLECTION = "summaries"
    }
}
