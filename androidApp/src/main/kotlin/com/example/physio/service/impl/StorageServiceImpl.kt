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
        try {
        firestore.collection(USERS_COLLECTION)
            .document(auth.currentUserId)
            .set(user)
            .await()

        userPreferences.setUser(user.uid, user.name, user.lastname, user.licenseNumber, user.userType)
        Log.d(STORAGE_SERVICE_TAG, "createUser: $user")
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error creating user:", e)
        }
    }

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
                            email = it.email
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting users: ", e)
        }

        return userList
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

            Log.d(STORAGE_SERVICE_TAG, "Equipment list loaded, item count: ${querySnapshot.documents.size}")

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

    override suspend fun getPackagesList(): List<Pair<String, String>> {
        return try {
            val querySnapshot = firestore.collection(EXERCISE_PACKAGE_SUMMARY)
                .get()
                .await()

            Log.d(STORAGE_SERVICE_TAG, "Packages list loaded, item count: ${querySnapshot.documents.size}")

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

    override suspend fun updateExercise(exercise: Exercise, mediaUris: List<Uri>?) {
        try {
            val updatedMediaUrls = if (!mediaUris.isNullOrEmpty()) {
                uploadFilesToFirebase(mediaUris)
            } else {
                exercise.mediaUrls
            }

            val updatedExercise = exercise.copy(mediaUrls = updatedMediaUrls, uid = auth.currentUserId)

            firestore.collection(EXERCISES_COLLECTION)
                .document(exercise.id)
                .set(updatedExercise)
                .await()

            val exerciseSummary = mapOf(
                "id" to exercise.id,
                "title" to exercise.title
            )

            firestore.collection(EXERCISE_SUMMARY)
                .document(exercise.id)
                .set(exerciseSummary)
                .await()

            Log.d(STORAGE_SERVICE_TAG, "Exercise updated with ID: ${exercise.id}")
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
                .update("id",generatedId,"uid", auth.currentUserId)
                .await()

            val exerciseSummary = mapOf(
                "id" to generatedId,
                "title" to exercise.title
            )

            firestore.collection(EXERCISE_SUMMARY)
                .document(generatedId)
                .set(exerciseSummary)
                .await()

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error creating exercise with media", e)
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
                    val equipmentIds = documentSnapshot.get("equipmentId") as? List<String>
                    if (equipmentIds != null) {
                        val uniqueEquipmentIds = equipmentIds.toSet().toList()
                        exerciseEquipmentMap[exerciseId] = uniqueEquipmentIds
                    } else {
                        Log.w(STORAGE_SERVICE_TAG, "No equipmentId found for exercise with ID: $exerciseId")
                    }
                } else {
                    Log.w(STORAGE_SERVICE_TAG, "No exercise found with ID: $exerciseId")
                }
            }
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error fetching equipmentIds for exercises", e)
        }
        return exerciseEquipmentMap
    }

    override suspend fun uploadFilesToFirebase(uris: List<Uri>): List<String> {

        val uploadedUrls = mutableListOf<String>()

        uris.forEach { uri ->
            val uriString = uri.toString()

            if (uriString.startsWith("https://")) {
                uploadedUrls.add(uriString)
            } else {
                val storageRef = storage.reference.child("uploads/${uri.lastPathSegment}")
                val uploadTask = storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await()
                uploadedUrls.add(downloadUrl.toString())
            }
        }

        return uploadedUrls
    }


    override suspend fun createExercisePackage(exercisePackage: ExercisePackage) {
        try {
            val documentReference = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .add(exercisePackage.copy(id = ""))
                .await()

            val generatedId = documentReference.id
            Log.d(STORAGE_SERVICE_TAG, "ExercisePackage created with ID: $generatedId")

            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(generatedId)
                .update("id", generatedId, "uid", auth.currentUserId)
                .await()

            val exercisePackageSummary = mapOf(
                "id" to generatedId,
                "name" to exercisePackage.name
            )

            firestore.collection(EXERCISE_PACKAGE_SUMMARY)
                .document(generatedId)
                .set(exercisePackageSummary)
                .await()

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error creating exercise package", e)
        }
    }


    override suspend fun updateExercisePackage(exercisePackage: ExercisePackage) {
        try {
            val updatedExercisePackage = exercisePackage.copy(uid = auth.currentUserId)

            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(updatedExercisePackage.id)
                .set(updatedExercisePackage)
                .await()

            val exercisePackageSummary = mapOf(
                "id" to updatedExercisePackage.id,
                "name" to updatedExercisePackage.name
            )

            firestore.collection(EXERCISE_PACKAGE_SUMMARY)
                .document(updatedExercisePackage.id)
                .set(exercisePackageSummary)
                .await()

            Log.d(STORAGE_SERVICE_TAG, "ExercisePackage updated: ${updatedExercisePackage.id}")

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

            if (documentSnapshot.exists()) {
                Log.d(STORAGE_SERVICE_TAG, "Exercise data: ${documentSnapshot.data}")
            } else {
                Log.d(STORAGE_SERVICE_TAG, "No exercise found with ID: $exerciseId")
            }

            documentSnapshot.toObject(Exercise::class.java)

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting exercise", e)
            null
        }
    }

    override suspend fun getPackage(packageId: String): ExercisePackage? {
        return try {
            val documentSnapshot = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(packageId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                Log.d(STORAGE_SERVICE_TAG, "Package data: ${documentSnapshot.data}")
            } else {
                Log.d(STORAGE_SERVICE_TAG, "No package found with ID: $packageId")
            }

            documentSnapshot.toObject(ExercisePackage::class.java)

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting package", e)
            null
        }
    }

    override suspend fun getExercises(): List<Pair<String, String>> {
        return try {
            val querySnapshot = firestore.collection("Exercise_summary")
                .get()
                .await()

            Log.d(STORAGE_SERVICE_TAG, "Exercises list loaded, item count: ${querySnapshot.documents.size}")

            querySnapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id")
                val title = doc.getString("title")
                if (id != null && title != null) {
                    id to title
                } else {
                    null
                }
            }

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

            Log.d(STORAGE_SERVICE_TAG, "Conditions list loaded, item count: ${querySnapshot.documents.size}")

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
        private const val USERS_COLLECTION = "users"
        private const val STORAGE_SERVICE_TAG = "StorageService"
        private const val EXERCISES_COLLECTION = "exercises"
        private const val EQUIPMENT_COLLECTION = "equipment"
        private const val CONDITIONS_COLLECTION = "conditions"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercise_packages"
        private const val EXERCISE_PACKAGE_SUMMARY = "exercise_package_summary"
        private const val EXERCISE_SUMMARY = "Exercise_summary"
    }

}
