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
        setProcessing: (Boolean) -> Unit,
        onError: (String) -> Unit,
        onSuccess: (Uri) -> Unit
    ) {
        if (uri.toString().startsWith(FIREBASE_MEDIA_PREFIX)) {
            onSuccess(uri)
            return
        }

        setProcessing(true)
        try {
            withContext(Dispatchers.IO) {
                val filePath = copyToCacheDir(context, uri) ?: run {
                    throw IllegalStateException("Nie udało się uzyskać ścieżki do pliku.")
                }

                val infoCommand = "-i $filePath"
                val session = FFmpegKit.execute(infoCommand)

                val logs = session.allLogsAsString
                if (logs.contains("Error") || session.failStackTrace != null) {
                    throw IllegalStateException("Nie udało się uzyskać informacji o wideo. Logi: $logs")
                }

                val duration = extractDuration(logs)
                val resolution = extractResolution(logs)

                if (duration != null && duration > 300) {
                    throw IllegalArgumentException("Wideo jest zbyt długie (maksymalna długość to 5 minut).")
                }

                if (resolution != null && (resolution.first > 1920 || resolution.second > 1080)) {
                    val outputFile = File(context.cacheDir, "converted_video.mp4").absolutePath
                    val command =
                        "-i $filePath -vf scale=1920:1080 -c:v libx264 -preset fast -crf 23 -c:a aac $outputFile"

                    val convertSession = FFmpegKit.execute(command)

                    val convertLogs = convertSession.allLogsAsString
                    if (convertLogs.contains("Error") || convertSession.failStackTrace != null) {
                        throw IllegalStateException("Nie udało się przekonwertować wideo. Logi: $convertLogs")
                    }

                    onSuccess(Uri.fromFile(File(outputFile)))
                } else {
                    onSuccess(uri)
                }
            }
        } catch (e: Exception) {
            onError(e.message ?: "Nieznany błąd.")
        } finally {
            setProcessing(false)
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

    private fun copyToCacheDir(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, uri.lastPathSegment ?: "temp_video.mp4")
            file.outputStream().use { inputStream.copyTo(it) }
            file.absolutePath
        } catch (e: Exception) {
            android.util.Log.e("MediaProcessor", "Error copying file to cache dir", e)
            null
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