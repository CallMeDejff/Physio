package com.dawidkubica.physio.service.impl

import android.net.Uri
import android.util.Log
import com.dawidkubica.physio.service.services.FileStorageService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FileStorageServiceImpl @Inject constructor(
    private val storage: FirebaseStorage
) : FileStorageService {

    override suspend fun uploadFilesToFirebase(uris: List<Uri>, path: String): List<String> {
        val uploadedUrls = mutableListOf<String>()
        uris.forEach { uri ->
            val uriString = uri.toString()
            if (uriString.startsWith("https://")) {
                uploadedUrls.add(uriString)
            } else {
                val storageRef = storage.reference.child("$path/${uri.lastPathSegment}")
                val uploadTask = storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await()
                uploadedUrls.add(downloadUrl.toString())
            }
        }
        return uploadedUrls
    }

    override suspend fun deleteMedia(mediaUrls: List<String>) {
        mediaUrls.forEach { mediaUrl ->
            try {
                val storagePath = getStoragePathFromUrl(mediaUrl)
                val storageRef = storage.reference.child(storagePath)
                storageRef.delete().await()
            } catch (e: Exception) {
                Log.e(STORAGE_SERVICE_TAG, "Error deleting media file: $mediaUrl", e)
            }
        }
    }

    override suspend fun getStoragePathFromUrl(mediaUrl: String): String {
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

    companion object {
        private const val STORAGE_SERVICE_TAG = "FileStorageService"
    }
}
