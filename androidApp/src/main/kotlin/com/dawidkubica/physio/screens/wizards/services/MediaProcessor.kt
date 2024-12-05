package com.dawidkubica.physio.screens.wizards.services

import android.content.Context
import android.net.Uri
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object MediaProcessor {

    private const val FIREBASE_MEDIA_PREFIX = "https://firebasestorage.googleapis.com/"

    suspend fun processMedia(
        context: Context,
        uri: Uri,
        onError: (String) -> Unit,
        onSuccess: (Uri) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                if (uri.toString().startsWith(FIREBASE_MEDIA_PREFIX)) {
                    android.util.Log.d("MediaProcessor", "Media URL comes from Firestore - processing stopped.")
                    onSuccess(uri)
                    return@withContext
                }

                val filePath = uriToFilePath(context, uri) ?: run {
                    onError("Nie udało się uzyskać ścieżki do pliku.")
                    return@withContext
                }

                val infoCommand = "-i $filePath"
                val session = FFmpegKit.execute(infoCommand)

                if (session.returnCode?.isValueSuccess == true) {
                    val logs = session.allLogsAsString
                    val duration = extractDuration(logs)
                    val resolution = extractResolution(logs)

                    if (duration != null && duration > 300) {
                        onError("Wideo jest zbyt długie (maksymalna długość to 5 minut).")
                        return@withContext
                    }

                    if (resolution != null && (resolution.first > 1920 || resolution.second > 1080)) {
                        val outputFile = File(context.cacheDir, "converted_video.mp4").absolutePath
                        val command =
                            "-i $filePath -vf scale=1920:1080 -c:v libx264 -preset fast -crf 23 -c:a aac $outputFile"

                        val convertSession = FFmpegKit.execute(command)
                        if (convertSession.returnCode?.isValueSuccess == true) {
                            android.util.Log.d("MediaProcessor", "Video could not be converted.")
                            onSuccess(Uri.fromFile(File(outputFile)))
                        } else {
                            onError("Nie udało się przekonwertować wideo.")
                        }
                    } else {
                        onSuccess(uri)
                        android.util.Log.d("MediaProcessor", "Video converted.")
                    }
                } else {
                    onError("Nie udało się uzyskać informacji o wideo.")
                }
            } catch (e: Exception) {
                onError("Wystąpił błąd podczas przetwarzania pliku: ${e.message}")
            }
        }
    }

    private fun uriToFilePath(context: Context, uri: Uri): String? {
        val projection = arrayOf("_data")
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow("_data")
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        }
        return null
    }

    private fun extractDuration(logs: String): Int? {
        val durationRegex = Regex("Duration: (\\d+):(\\d+):(\\d+\\.\\d+)")
        val match = durationRegex.find(logs)
        return match?.let {
            val (hours, minutes, seconds) = it.destructured
            (hours.toInt() * 3600 + minutes.toInt() * 60 + seconds.toDouble().toInt())
        }
    }

    private fun extractResolution(logs: String): Pair<Int, Int>? {
        val resolutionRegex = Regex("(\\d{3,4})x(\\d{3,4})")
        val match = resolutionRegex.find(logs)
        return match?.let {
            val (width, height) = it.destructured
            width.toInt() to height.toInt()
        }
    }
}