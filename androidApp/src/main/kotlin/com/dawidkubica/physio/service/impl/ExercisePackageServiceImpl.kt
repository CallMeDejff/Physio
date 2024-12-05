package com.dawidkubica.physio.service.impl

import android.net.Uri
import android.util.Log
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.models.UserPackages
import com.dawidkubica.physio.service.AuthError
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.AuthenticationService
import com.dawidkubica.physio.service.services.CacheManager
import com.dawidkubica.physio.service.services.ExercisePackageService
import com.dawidkubica.physio.service.services.FileStorageService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExercisePackageServiceImpl @Inject constructor(
    private val auth: AuthenticationService,
    private val fileStorageService: FileStorageService,
    private val accountService: AccountService,
    private val cacheManager: CacheManager
) : ExercisePackageService {

    var currentUserId: String = ""
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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
            Log.e(
                EXERCISE_PACKAGE_SERVICE_TAG,
                "getExercisePackages:Error getting exercise packages",
                e
            )
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

    override suspend fun createExercisePackage(
        exercisePackage: ExercisePackage,
        mediaUris: List<Uri>
    ) {
        try {
            val mediaUrls =
                fileStorageService.uploadFilesToFirebase(mediaUris, path = auth.currentUserId)
            val packageWithMedia = exercisePackage.copy(mediaUrls = mediaUrls)

            val documentReference = firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .add(packageWithMedia)
                .await()

            val generatedId = documentReference.id
            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "ExercisePackage created with ID: $generatedId")

            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(generatedId)
                .update("id", generatedId, "uid", currentUserId)
                .await()

            val exercisePackageSummaryEntry =
                mapOf("id" to generatedId, "name" to exercisePackage.name, "uid" to currentUserId)

            firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .update("packages", FieldValue.arrayUnion(exercisePackageSummaryEntry))
                .await()

        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error creating exercise package", e)
        }
    }

    override suspend fun deleteExercisePackage(exercisePackage: ExercisePackage) {
        Log.d(
            EXERCISE_PACKAGE_SERVICE_TAG,
            "deleteExercisePackage: Deleting exercise package with ID: ${exercisePackage.id}"
        )

        val documentId = exercisePackage.id
        try {
            deleteExercisePackageMedia(exercisePackage)

            val exercisePackageDocRef =
                firestore.collection(EXERCISE_PACKAGES_COLLECTION).document(documentId)
            val exercisePackageSnapshot = exercisePackageDocRef.get().await()
            val exercisePackageFromDb =
                exercisePackageSnapshot.toObject(ExercisePackage::class.java)

            if (exercisePackageFromDb == null) {
                Log.e(
                    EXERCISE_PACKAGE_SERVICE_TAG,
                    "ExercisePackage not found with ID: $documentId"
                )
                return
            }

            val assignedUsers = exercisePackageFromDb.assignedTo
            for (userId in assignedUsers) {
                accountService.removePackageFromUser(userId, documentId)
            }

            exercisePackageDocRef.delete().await()
            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "ExercisePackage deleted with ID: $documentId")

            val packagesDocument = firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .get()
                .await()

            val existingPackages =
                packagesDocument.get("packages") as? List<Map<String, Any>> ?: emptyList()

            val updatedPackages = existingPackages.filter { it["id"] != documentId }

            firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .set(mapOf("packages" to updatedPackages))
                .await()

            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "Package summary updated after deletion.")

        } catch (e: Exception) {
            Log.e(
                EXERCISE_PACKAGE_SERVICE_TAG,
                "Error deleting exercise package with ID: $documentId",
                e
            )
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

    override suspend fun updateExercisePackage(
        exercisePackage: ExercisePackage,
        mediaUris: List<Uri>
    ) {
        try {
            val updatedMediaUrls = if (!mediaUris.isNullOrEmpty()) {
                fileStorageService.uploadFilesToFirebase(mediaUris, path = exercisePackage.id)
            } else {
                exercisePackage.mediaUrls
            }

            val updatedExercisePackage =
                exercisePackage.copy(mediaUrls = updatedMediaUrls, uid = currentUserId)

            firestore.collection(EXERCISE_PACKAGES_COLLECTION)
                .document(updatedExercisePackage.id)
                .set(updatedExercisePackage)
                .await()

            val exercisePackageSummaryEntry = mapOf(
                "id" to updatedExercisePackage.id,
                "name" to updatedExercisePackage.name,
                "uid" to currentUserId
            )

            val packagesDocument =
                firestore.collection(SUMMARY_COLLECTION).document("packages").get().await()
            val existingPackages =
                packagesDocument.get("packages") as? List<Map<String, Any>> ?: emptyList()
            val updatedPackages =
                existingPackages.filterNot { it["id"] == updatedExercisePackage.id } + exercisePackageSummaryEntry

            firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .set(mapOf("packages" to updatedPackages))
                .await()

            Log.d(
                EXERCISE_PACKAGE_SERVICE_TAG,
                "ExercisePackage updated: ${updatedExercisePackage.id}"
            )

        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error updating exercise package", e)
        }
    }

    private suspend fun deleteExercisePackageMedia(exercisePackage: ExercisePackage) {
        Log.d(
            EXERCISE_PACKAGE_SERVICE_TAG,
            "deleteExercisePackageMedia: Deleting exercise package with ID: ${exercisePackage.id}, mediaUrls: ${exercisePackage.mediaUrls}"
        )
        exercisePackage.mediaUrls.forEach { mediaUrl ->
            try {
                val storagePath = fileStorageService.getStoragePathFromUrl(mediaUrl)
                val storageRef = storage.reference.child(storagePath)
                storageRef.delete().await()
                Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "Deleted media file: $mediaUrl")
            } catch (e: Exception) {
                Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error deleting media file: $mediaUrl", e)
            }
        }
    }

    override suspend fun findMatchingExercisePackages(
        conditionIds: List<String>,
        equipmentIds: List<String>
    ): List<ExercisePackage> {
        return try {
            val allPackages = getExercisePackages()
            val matchingPackages = allPackages.filter { exercisePackage ->
                val conditionsMatch =
                    conditionIds.isEmpty() || exercisePackage.conditionIds.any { it in conditionIds }
                val equipmentMatch =
                    equipmentIds.isEmpty() || exercisePackage.equipmentIds.any { it in equipmentIds }
                conditionsMatch && equipmentMatch
            }

            matchingPackages
        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error finding matching exercise packages", e)
            emptyList()
        }
    }

    override suspend fun getUserExercisePackages(): UserPackages {
        return cacheManager.getCachedUserPackages() ?: try {
            val user = accountService.getUserInfo()

            val favoritePackageIds = user?.favoritePackages ?: emptyList()
            val assignedPackageIds = user?.assignedPackages ?: emptyList()

            val favoritePackages = favoritePackageIds.mapNotNull { packageId ->
                getExercisePackage(packageId)
            }

            val assignedPackages = assignedPackageIds.mapNotNull { packageId ->
                getExercisePackage(packageId)
            }

            val userPackages = UserPackages(
                favoritePackages = favoritePackages,
                assignedPackages = assignedPackages
            )

            cacheManager.setCachedUserPackages(userPackages)
            userPackages
        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error getting user exercise packages", e)
            UserPackages()
        }
    }

    override suspend fun assignPackageToUser(userId: String, packageId: String): Result<Unit> {
        try {
            val exercisePackageDocRef =
                firestore.collection(EXERCISE_PACKAGES_COLLECTION).document(packageId)
            val exercisePackageSnapshot = exercisePackageDocRef.get().await()
            val exercisePackage = exercisePackageSnapshot.toObject(ExercisePackage::class.java)

            if (exercisePackage == null) {
                Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "ExercisePackage not found with ID: $packageId")
                return Result.failure(AuthError("Wystąpił błąd przy przypisywaniu pakietu."))
            }

            if (exercisePackage.assignedTo.contains(userId)) {
                Log.d(
                    EXERCISE_PACKAGE_SERVICE_TAG,
                    "User $userId is already assigned to package $packageId"
                )
                return Result.failure(AuthError("Użytkownik posiada już ten pakiet ćwiczeń."))
            }
            accountService.assignPackageToUser(userId, packageId)
            val updatedAssignedTo = exercisePackage.assignedTo + userId
            exercisePackageDocRef.update("assignedTo", updatedAssignedTo).await()
            Log.d(EXERCISE_PACKAGE_SERVICE_TAG, "Package $packageId assigned to user $userId")
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e(EXERCISE_PACKAGE_SERVICE_TAG, "Error assigning package to user", e)
            return Result.failure(AuthError("Wystąpił błąd przy przypisywaniu pakietu."))
        }
    }

    override suspend fun removePackageFromUser(userId: String, packageId: String) {
        try {
            accountService.removePackageFromUser(userId, packageId)

            val exercisePackageDocRef =
                firestore.collection(EXERCISE_PACKAGES_COLLECTION).document(packageId)
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
    }
}
