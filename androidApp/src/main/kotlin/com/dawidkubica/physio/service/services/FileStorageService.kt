package com.dawidkubica.physio.service.services

import android.net.Uri

interface FileStorageService {
    suspend fun uploadFilesToFirebase(uris: List<Uri>, path: String): List<String>
    suspend fun deleteMedia(mediaUrls: List<String>)
    suspend fun getStoragePathFromUrl(mediaUrl: String): String
}