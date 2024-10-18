package com.example.physio.service.impl

import android.util.Log
import com.example.physio.service.services.CacheManager
import com.example.physio.service.services.ListService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ListServiceImpl @Inject constructor(
    private val cacheManager: CacheManager
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

    override suspend fun getPackagesList(): List<Pair<String, String>> {
        return try {
            val documentSnapshot = firestore.collection(SUMMARY_COLLECTION)
                .document("packages")
                .get()
                .await()

            val packagesList =
                documentSnapshot.get("packages") as? List<Map<String, String>> ?: emptyList()
            Log.d(LIST_SERVICE_TAG, "Packages list loaded, item count: ${packagesList.size}")

            packagesList.map { exercisePackage ->
                Pair(
                    exercisePackage["id"] ?: "",
                    exercisePackage["name"] ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e(LIST_SERVICE_TAG, "Error getting packages", e)
            emptyList()
        }
    }

    override suspend fun getExercises(): List<Pair<String, String>> {
        return cacheManager.getCachedExercisesList() ?: try {
            val exercisesDocument =
                firestore.collection(SUMMARY_COLLECTION).document("exercises").get().await()

            val exercisesList =
                exercisesDocument.get("exercises") as? List<Map<String, String>> ?: emptyList()

            val result = exercisesList.mapNotNull { entry ->
                val id = entry["id"]
                val title = entry["title"]
                if (id != null && title != null) id to title else null
            }
            cacheManager.setCachedExercisesList(result)
            result
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
