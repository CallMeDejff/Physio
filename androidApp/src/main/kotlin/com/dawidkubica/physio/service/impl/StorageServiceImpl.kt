package com.dawidkubica.physio.service.impl

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.dawidkubica.physio.models.Exercise
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.StorageService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val auth: AccountService
) : StorageService {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var cachedEquipmentList: List<Pair<String, String>>? = null
    private var cachedConditionsList: List<Pair<String, String>>? = null
    private var cachedExercisesList: List<Pair<String, String>>? = null
    private var cachedExercisePackages: List<ExercisePackage>? = null
    private var cacheExpiryTime = 7 * 24 * 60 * 60 * 1000

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    override suspend fun getEquipmentList(): List<Pair<String, String>> {
        if (cachedEquipmentList != null && !isCacheExpired(cachedEquipmentListTimestamp)) {
            Log.d(STORAGE_SERVICE_TAG, "Returning cached equipment list")
            return cachedEquipmentList!!
        } else {

            return try {
                val documentSnapshot = firestore.collection(SUMMARY_COLLECTION)
                    .document("equipment")
                    .get()
                    .await()

                val equipmentList =
                    documentSnapshot.get("equipment") as? List<Map<String, String>> ?: emptyList()

                val result = equipmentList.map { item ->
                    Pair(item["id"] ?: "", item["name"] ?: "")
                }

                cachedEquipmentList = result
                cachedEquipmentListTimestamp = System.currentTimeMillis()
                Log.d(STORAGE_SERVICE_TAG, "Equipment list loaded, item count: ${result.size}")

                result
            } catch (e: Exception) {
                Log.e(STORAGE_SERVICE_TAG, "Error getting equipment", e)
                emptyList()
            }
        }
    }

    override suspend fun getPackagesList(): List<Pair<String, String>> {
        return try {
            val documentSnapshot = firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .get()
                .await()

            val packagesList =
                documentSnapshot.get("packages") as? List<Map<String, String>> ?: emptyList()
            Log.d(STORAGE_SERVICE_TAG, "Packages list loaded, item count: ${packagesList.size}")

            packagesList.map { exercisePackage ->
                Pair(
                    exercisePackage["id"] ?: "",
                    exercisePackage["name"] ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting packages", e)
            emptyList()
        }
    }

    override suspend fun updateExercise(exercise: Exercise, mediaUris: List<Uri>?) {
        try {
            val updatedMediaUrls = if (!mediaUris.isNullOrEmpty()) {
                uploadFilesToFirebase(mediaUris, path = exercise.id)
            } else {
                exercise.mediaUrls
            }

            val updatedExercise =
                exercise.copy(mediaUrls = updatedMediaUrls, uid = auth.currentUserId)
            val exerciseRef = firestore.collection(EXERCISES_COLLECTION).document(exercise.id)
            exerciseRef.set(updatedExercise).await()
            Log.d(STORAGE_SERVICE_TAG, "Exercise updated with ID: ${exercise.id}")

            val exerciseSummaryEntry = mapOf("id" to exercise.id, "title" to exercise.title)
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
            Log.e(STORAGE_SERVICE_TAG, "Error updating exercise", e)
        }
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        Log.d(
            STORAGE_SERVICE_TAG,
            "deleteExercise: Deleting exercise with ID: ${exercise.id}, mediaUrls: ${exercise.mediaUrls}"
        )
        val documentId = exercise.id
        try {
            deleteExerciseMedia(exercise)

            firestore.collection(EXERCISES_COLLECTION)
                .document(documentId)
                .delete()
                .await()

            Log.d(STORAGE_SERVICE_TAG, "Exercise deleted with ID: $documentId")
            updateExerciseSummary(documentId)
            removeExerciseReferencesFromPackages(documentId)

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error deleting exercise with ID: $documentId", e)
        }
    }

    private suspend fun deleteExerciseMedia(exercise: Exercise) {
        Log.d(
            STORAGE_SERVICE_TAG,
            "deleteExerciseMedia: Deleting exercise with ID: ${exercise.id}, mediaUrls: ${exercise.mediaUrls}"
        )
        exercise.mediaUrls.forEach { mediaUrl ->
            try {
                val storagePath = getStoragePathFromUrl(mediaUrl)
                val storageRef = storage.reference.child(storagePath)
                storageRef.delete().await()
                Log.d(STORAGE_SERVICE_TAG, "Deleted media file: $mediaUrl")
            } catch (e: Exception) {
                Log.e(STORAGE_SERVICE_TAG, "Error deleting media file: $mediaUrl", e)
            }
        }
    }

    private fun getStoragePathFromUrl(mediaUrl: String): String {
        val apiUrlPrefix = "https://firebasestorage.googleapis.com/v0/b/"
        if (mediaUrl.startsWith(apiUrlPrefix)) {
            val decodedUrl = Uri.decode(mediaUrl)
            return decodedUrl.substringAfter("/o/").substringBefore("?")
        } else {
            val storageUrl = storage.reference.toString()
            val decodedUrl = Uri.decode(mediaUrl)
            return decodedUrl.removePrefix("$storageUrl/")
                .substringBefore("?")
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

        Log.d(STORAGE_SERVICE_TAG, "Exercise summary updated after deletion.")
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
                    STORAGE_SERVICE_TAG,
                    "Removed exercise reference from package ID: ${packageDoc.id}"
                )
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override suspend fun createExerciseWithMedia(exercise: Exercise, mediaUris: List<Uri>) {
        try {
            val mediaUrls = uploadFilesToFirebase(mediaUris, path = auth.currentUserId)
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

            val exerciseSummaryEntry = mapOf("id" to generatedId, "title" to exercise.title)

            firestore.collection(SUMMARY_COLLECTION)
                .document("exercises")
                .update("exercises", FieldValue.arrayUnion(exerciseSummaryEntry))
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
                        Log.w(
                            STORAGE_SERVICE_TAG,
                            "No equipmentId found for exercise with ID: $exerciseId"
                        )
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

    override suspend fun uploadFilesToFirebase(uris: List<Uri>, path: String): List<String> {
        val uploadedUrls = mutableListOf<String>()

        uris.forEach { uri ->
            val uriString = uri.toString()

            if (uriString.startsWith("https://")) {
                uploadedUrls.add(uriString)
            } else {
                val storageRef = storage.reference.child("${path}/${uri.lastPathSegment}")
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

            Log.d(STORAGE_SERVICE_TAG, "ExercisePackage updated: ${updatedExercisePackage.id}")

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error updating exercise package", e)
        }
    }

    override suspend fun deleteExercisePackage(exercisePackage: ExercisePackage) {
        val documentId = exercisePackage.id
        try {
            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(documentId)
                .delete()
                .await()


            Log.d(STORAGE_SERVICE_TAG, "ExercisePackage deleted with ID: $documentId")

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

            Log.d(STORAGE_SERVICE_TAG, "Package summary updated after deletion.")

        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error deleting exercise package with ID: $documentId", e)
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
        if (cachedExercisesList != null && !isCacheExpired(cachedExercisesListTimestamp)) {
            Log.d(STORAGE_SERVICE_TAG, "getExercises: Returning cached exercises list")
            return cachedExercisesList!!
        }
        return try {
            val exercisesDocument =
                firestore.collection(SUMMARY_COLLECTION).document("exercises").get().await()

            val exercisesList = exercisesDocument.get("exercises") as? List<Map<String, String>>
                ?: emptyList()
            Log.d(
                STORAGE_SERVICE_TAG,
                "getExercises: Exercises list loaded, item count: ${exercisesList.size}"
            )

            val result = exercisesList.mapNotNull { entry ->
                val id = entry["id"]
                val title = entry["title"]
                if (id != null && title != null) {
                    id to title
                } else {
                    null
                }
            }
            cachedExercisesList = result
            cachedExercisesListTimestamp = System.currentTimeMillis()
            result
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "getExercises: Error getting exercises", e)
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
        if (cachedExercisePackages != null && !isCacheExpired(cachedExercisePackagesTimestamp)) {
            Log.d(
                STORAGE_SERVICE_TAG,
                "getExercisePackages:Returning cached exercise packages list"
            )
            return cachedExercisePackages!!
        }

        return try {
            val querySnapshot = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .get()
                .await()

            val exercisePackagesList = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(ExercisePackage::class.java)?.let {
                    ExercisePackage(
                        id = it.id,
                        name = it.name,
                        uid = it.uid,
                        description = it.description,
                        conditionIds = it.conditionIds,
                        equipmentIds = it.equipmentIds
                    )
                }
            }

            cachedExercisePackages = exercisePackagesList
            cachedExercisePackagesTimestamp = System.currentTimeMillis()
            exercisePackagesList
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "getExercisePackages:Error getting exercise packages", e)
            emptyList()
        }
    }

    override suspend fun getConditionsList(): List<Pair<String, String>> {
        if (cachedConditionsList != null && !isCacheExpired(cachedConditionsListTimestamp)) {
            Log.d(STORAGE_SERVICE_TAG, "Returning cached conditions list")
            return cachedConditionsList!!
        }

        return try {
            val documentSnapshot = firestore.collection(SUMMARY_COLLECTION)
                .document("conditions")
                .get()
                .await()

            val conditionsList =
                documentSnapshot.get("conditions") as? List<Map<String, String>> ?: emptyList()

            val result = conditionsList.map { item ->
                Pair(item["id"] ?: "", item["name"] ?: "")
            }

            cachedConditionsList = result
            cachedConditionsListTimestamp = System.currentTimeMillis()
            Log.d(STORAGE_SERVICE_TAG, "Conditions list loaded, item count: ${result.size}")
            result
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error getting conditions", e)
            emptyList()
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
                    STORAGE_SERVICE_TAG,
                    "findMatchingExercisePackages: Package: ID = ${pkg.id}, desc: ${pkg.description}, Conditions = ${pkg.conditionIds}, Equipment = ${pkg.equipmentIds}"
                )
            }

            val matchingPackages = allPackages.filter { exercisePackage ->
                exercisePackage.conditionIds.containsAll(conditionIds) &&
                        exercisePackage.equipmentIds.containsAll(equipmentIds)
            }.map { Triple(it.id, it.name, it.description) }

            matchingPackages
        } catch (e: Exception) {
            Log.e(STORAGE_SERVICE_TAG, "Error finding matching exercise packages", e)
            emptyList()
        }
    }

    private fun isCacheExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > cacheExpiryTime
    }

    companion object {
        private const val STORAGE_SERVICE_TAG = "StorageService"
        private const val EXERCISES_COLLECTION = "exercises"
        private const val SUMMARY_COLLECTION = "summaries"
        private const val EXERCISE_PACKAGES_COLLECTION = "exercise_packages"

        private var cachedExercisesListTimestamp: Long = 0
        private var cachedExercisePackagesTimestamp: Long = 0
        private var cachedEquipmentListTimestamp: Long = 0
        private var cachedConditionsListTimestamp: Long = 0
    }
}
