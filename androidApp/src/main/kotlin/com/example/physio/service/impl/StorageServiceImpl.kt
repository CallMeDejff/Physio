package com.example.physio.service.impl

import android.net.Uri
import android.util.Log
import com.example.physio.models.Exercise
import com.example.physio.models.ExercisePackage
import com.example.physio.models.User
import com.example.physio.service.UserPreferences
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.StorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val auth: AccountService,
    private var userPreferences: UserPreferences
) : StorageService {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override suspend fun createUser(user: User) {
        firestore.collection(USERS_COLLECTION)
            .document(auth.currentUserId)
            .set(user)
            .await()

        userPreferences.setUser(user.uid, user.name, user.lastname, user.licenseNumber, user.userType)
        Log.d(STORAGE_SERVICE_TAG, "createUser: $user")
    }

    override suspend fun getUserInfo(userId: String): User? {
        val cachedUser = if (userId == auth.currentUserId) {
            User(
                uid = userPreferences.getUserUid(),
                name = userPreferences.getUserName(),
                lastname = userPreferences.getUserLastname(),
                licenseNumber = userPreferences.getUserLicenseNumber(),
                userType = userPreferences.getUserType()
            )
        } else null

        if (cachedUser != null && cachedUser.uid.isNotEmpty()) {
            Log.d(STORAGE_SERVICE_TAG, "returned cached user: $cachedUser")
            return cachedUser
        }

        val documentSnapshot = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()

        return documentSnapshot.toObject(User::class.java)?.also {
            if (userId == auth.currentUserId) {
                userPreferences.setUser(it.uid, it.name, it.lastname, it.licenseNumber, it.userType)
                Log.d(STORAGE_SERVICE_TAG, "user set to shared preferences: $it")
            }
        }
    }

    override suspend fun getEquipmentList(): List<Pair<String, String>> {
        return try {
            val querySnapshot = firestore.collection(EQUIPMENT_COLLECTION)
                .get()
                .await()

            querySnapshot.documents.map { document ->
                Pair(
                    document.id,
                    document.getString("name") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting equipment", e)
            emptyList()
        }
    }

    override suspend fun createExercise(exercise: Exercise) {
        try {
            val documentReference = firestore.collection(EXERCISES_COLLECTION)
                .add(exercise)
                .await()

            val generatedId = documentReference.id
            Log.d(STORAGE_SERVICE_TAG, "Exercise created with ID: $generatedId")

            val exerciseSummary = mapOf(
                "id" to generatedId,
                "title" to exercise.title
            )
            documentReference.collection("details").document(generatedId)
                .set(exerciseSummary)
                .await()

            firestore.collection(EXERCISES_COLLECTION)
                .document(generatedId)
                .update("id", generatedId)
                .await()

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error creating exercise", e)
        }
    }

    override suspend fun updateExercise(exercise: Exercise) {
        try {
            firestore.collection(EXERCISES_COLLECTION)
                .document(exercise.id)
                .set(exercise)
                .await()

            val exerciseSummary = mapOf(
                "id" to exercise.id,
                "title" to exercise.title
            )
            firestore.collection(EXERCISES_COLLECTION)
                .document(exercise.id)
                .collection("details").document(exercise.id)
                .set(exerciseSummary)
                .await()

            Log.d(STORAGE_SERVICE_TAG, "Exercise updated: $exercise")
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error updating exercise", e)
        }
    }

    override suspend fun deleteExercise(exerciseId: String) {
        try {
            firestore.collection(EXERCISES_COLLECTION)
                .document(exerciseId)
                .delete()
                .await()

            firestore.collection(EXERCISES_COLLECTION)
                .document(exerciseId)
                .collection("details").document(exerciseId)
                .delete()
                .await()

            Log.d(STORAGE_SERVICE_TAG, "Exercise deleted with id: $exerciseId")
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error deleting exercise", e)
        }
    }

   override suspend fun createExerciseWithMedia(exercise: Exercise, mediaUris: List<Uri>) {

        try {
            val mediaUrls = uploadFilesToFirebase(mediaUris)
            val exerciseWithMedia = exercise.copy(mediaUrls = mediaUrls)
            val documentReference = firestore.collection(EXERCISES_COLLECTION)
                .add(exerciseWithMedia)
                .await()

            val generatedId = documentReference.id
            Log.d(STORAGE_SERVICE_TAG, "Exercise created with ID: $generatedId")

            firestore.collection(EXERCISES_COLLECTION)
                .document(generatedId)
                .update("id", generatedId, "uid", auth.currentUserId)
                .await()

            val exerciseSummary = mapOf(
                "id" to generatedId,
                "title" to exercise.title
            )
            documentReference.collection("details").document(generatedId)
                .set(exerciseSummary)
                .await()

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error creating exercise with media", e)
        }
    }

    override suspend fun uploadFilesToFirebase(uris: List<Uri>): List<String> {

        val uploadedUrls = mutableListOf<String>()

        uris.forEach { uri ->
            val storageRef = storage.reference.child("uploads/${uri.lastPathSegment}")
            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            uploadedUrls.add(downloadUrl.toString())
        }

        return uploadedUrls
    }

    override suspend fun createExercisePackage(exercisePackage: ExercisePackage) {
        try {
            val documentReference = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .add(exercisePackage)
                .await()

            val generatedId = documentReference.id
            Log.d(STORAGE_SERVICE_TAG, "ExercisePackage created with ID: $generatedId")

            val exercisePackageSummary = mapOf(
                "id" to generatedId,
                "name" to exercisePackage.name
            )
            documentReference.collection("details").document(generatedId)
                .set(exercisePackageSummary)
                .await()

            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(generatedId)
                .update("id", generatedId)
                .await()

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error creating exercise package", e)
        }
    }

    override suspend fun updateExercisePackage(exercisePackage: ExercisePackage) {
        try {
            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(exercisePackage.id)
                .set(exercisePackage)
                .await()

            val exercisePackageSummary = mapOf(
                "id" to exercisePackage.id,
                "name" to exercisePackage.name
            )
            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(exercisePackage.id)
                .collection("details").document(exercisePackage.id)
                .set(exercisePackageSummary)
                .await()

            Log.d(STORAGE_SERVICE_TAG, "ExercisePackage updated: $exercisePackage")
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error updating exercise package", e)
        }
    }

    override suspend fun deleteExercisePackage(exercisePackageId: String) {
        try {
            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(exercisePackageId)
                .delete()
                .await()

            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(exercisePackageId)
                .collection("details").document(exercisePackageId)
                .delete()
                .await()

            Log.d(STORAGE_SERVICE_TAG, "ExercisePackage deleted with id: $exercisePackageId")
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error deleting exercise package", e)
        }
    }

    override suspend fun getExercise(exerciseId: String): Exercise? {
        return try {
            val documentSnapshot = firestore.collection(EXERCISES_COLLECTION)
                .document(exerciseId)
                .get()
                .await()

            documentSnapshot.toObject(Exercise::class.java)
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting exercise", e)
            null
        }
    }

    override suspend fun getExercises(): List<Exercise> {
        return try {
            val querySnapshot = firestore.collection(EXERCISES_COLLECTION)
                .get()
                .await()

            val exercises = mutableListOf<Exercise>()
            for (doc in querySnapshot.documents) {
                val detailsDoc = firestore.collection(EXERCISES_COLLECTION)
                    .document(doc.id)
                    .collection("details")
                    .document(doc.id)
                    .get()
                    .await()

                val id = detailsDoc.getString("id") ?: continue
                val title = detailsDoc.getString("title") ?: continue
                exercises.add(Exercise(id = id, title = title))
            }
            exercises
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting exercises", e)
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
            Log.e(STORAGE_SERVICE_TAG, "Error getting exercise package", e)
            null
        }
    }

    override suspend fun getExercisePackages(): List<ExercisePackage> {
        return try {
            val querySnapshot = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .get()
                .await()

            val exercisePackages = mutableListOf<ExercisePackage>()
            for (doc in querySnapshot.documents) {
                val detailsDoc = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                    .document(doc.id)
                    .collection("details")
                    .document(doc.id)
                    .get()
                    .await()

                val id = detailsDoc.getString("id") ?: continue
                val name = detailsDoc.getString("name") ?: continue
                exercisePackages.add(ExercisePackage(id = id, name = name))
            }
            exercisePackages
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting exercise packages", e)
            emptyList()
        }
    }

    override suspend fun getConditionsList(): List<Pair<String, String>> {
        return try {
            val querySnapshot = firestore.collection(CONDITIONS_COLLECTION)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                val id = document.id
                val name = document.getString("name") ?: ""
                if (name.isNotEmpty()) {
                    Pair(id, name)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting conditions", e)
            emptyList()
        }
    }

    companion object {
        private const val USER_ID_FIELD = "userId"
        private const val NOTES_COLLECTION = "notes"
        private const val USERS_COLLECTION = "users"
        private const val STORAGE_SERVICE_TAG = "StorageService"
        private const val EXERCISES_COLLECTION = "exercises"
        private const val EQUIPMENT_COLLECTION = "equipment"
        private const val CONDITIONS_COLLECTION = "conditions"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercisePackages"
    }

}
