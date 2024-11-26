package com.dawidkubica.physio.service.impl

import android.util.Log
import com.dawidkubica.physio.service.services.AuthenticationService
import com.dawidkubica.physio.service.services.CacheManager
import com.dawidkubica.physio.service.services.ListService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ListServiceImpl @Inject constructor(
    private val cacheManager: CacheManager,
    private val auth: AuthenticationService
) : ListService {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getEquipments(): List<Pair<String, String>> {
        return cacheManager.getCachedEquipmentsList() ?: try {
            val documentSnapshot = firestore.collection(SUMMARY_COLLECTION)
                .document("equipment")
                .get()
                .await()

            val equipmentList =
                documentSnapshot.get("equipment") as? List<Map<String, String>> ?: emptyList()

            val result = equipmentList.map { item ->
                Pair(item["id"] ?: "", item["name"] ?: "")
            }
            cacheManager.setCachedEquipmentsList(result)
            result
        } catch (e: Exception) {
            Log.e(LIST_SERVICE_TAG, "Error getting equipments", e)
            emptyList()
        }
    }

    override suspend fun getBodyParts(): List<Pair<String, String>> {
        return cacheManager.getCachedBodyPartsList() ?: try {
            val documentSnapshot = firestore.collection(SUMMARY_COLLECTION)
                .document("body_parts")
                .get()
                .await()

            val equipmentList =
                documentSnapshot.get("body_parts") as? List<Map<String, String>> ?: emptyList()

            val result = equipmentList.map { item ->
                Pair(item["id"] ?: "", item["name"] ?: "")
            }
            cacheManager.setCachedBodyPartsList(result)
            result
        } catch (e: Exception) {
            Log.e(LIST_SERVICE_TAG, "Error getting body parts data", e)
            emptyList()
        }
    }

    override suspend fun getPackagesList(allPackages: Boolean): List<Pair<String, String>> {
        return try {
            val currentUserId = auth.currentUserId
            val documentSnapshot =
                firestore.collection(SUMMARY_COLLECTION).document("packages").get().await()

            val packagesList =
                documentSnapshot.get("packages") as? List<Map<String, Any>> ?: emptyList()
            Log.d(LIST_SERVICE_TAG, "Packages list loaded, item count: ${packagesList.size}")

            val filteredPackages = packagesList.mapNotNull { entry ->
                val id = entry["id"] as? String
                val name = entry["name"] as? String
                val uid = entry["uid"] as? String

                if (id != null && name != null) {
                    if (allPackages || (uid == currentUserId)) {
                        id to name
                    } else {
                        null
                    }
                } else {
                    null
                }
            }

            filteredPackages
        } catch (e: Exception) {
            Log.e(LIST_SERVICE_TAG, "Error getting packages", e)
            emptyList()
        }
    }

    override suspend fun getExercises(): List<Pair<String, String>> {
        val currentUserId = auth.currentUserId
        return cacheManager.getCachedExercisesList() ?: try {
            val exercisesDocument =
                firestore.collection(SUMMARY_COLLECTION).document("exercises").get().await()

            val exercisesList =
                exercisesDocument.get("exercises") as? List<Map<String, Any>> ?: emptyList()

            val filteredExercises = exercisesList.mapNotNull { entry ->
                val id = entry["id"] as? String
                val title = entry["title"] as? String
                val nonPublic = entry["nonPublic"] as? Boolean ?: false
                val uid = entry["uid"] as? String

                if (!nonPublic || (nonPublic && uid == currentUserId)) {
                    if (id != null && title != null) id to title else null
                } else {
                    null
                }
            }

            cacheManager.setCachedExercisesList(filteredExercises)

            filteredExercises
        } catch (e: Exception) {
            Log.e(LIST_SERVICE_TAG, "getExercises: Error getting exercises", e)
            emptyList()
        }
    }

    override suspend fun getConditions(): List<Pair<String, String>> {
        return cacheManager.getCachedConditionsList() ?: try {
            val conditionsDocument =
                firestore.collection(SUMMARY_COLLECTION).document("conditions").get().await()

            val conditionsList =
                conditionsDocument.get("conditions") as? List<Map<String, String>> ?: emptyList()

            val result = conditionsList.mapNotNull { entry ->
                val id = entry["id"]
                val name = entry["name"]
                if (id != null && name != null) id to name else null
            }
            cacheManager.setCachedConditionsList(result)
            result
        } catch (e: Exception) {
            Log.e(LIST_SERVICE_TAG, "Error getting conditions", e)
            emptyList()
        }
    }

    companion object {
        private const val LIST_SERVICE_TAG = "ListService"
        private const val SUMMARY_COLLECTION = "summaries"
    }
}
