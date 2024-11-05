package com.example.physio.screens.exercise.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers

@Composable
fun PreviewScreen(
    mediaUrl: String,
    mediaType: String? = null,
    onDismiss: () -> Unit,
    isUrl: Boolean = false
) {
    val context = LocalContext.current

    val isVideo = if (!isUrl) {
        val mimeType = context.contentResolver.getType(Uri.parse(mediaUrl))
        Log.d("PreviewScreen", "MIME type for $mediaUrl: $mimeType")
        mimeType?.startsWith("video/") == true
    } else {
        mediaType == "video"
    }
    Log.d("PreviewScreen", "isEditor: $isUrl, mediaType: $mediaType, isVideo: $isVideo")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        when {
            isVideo -> {
                Log.d("PreviewScreen", "Displaying video player for URL: $mediaUrl")
                VideoPlayer(mediaUrl = mediaUrl, context = context)
            }

            mediaType == "image" || !isVideo -> {
                Log.d("PreviewScreen", "Displaying image for URL: $mediaUrl")
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            else -> {
                Log.w("PreviewScreen", "Unsupported media type or format for URL: $mediaUrl")
                Text("Nieobsługiwany format multimediów", color = Color.White)
            }
        }

        IconButton(
            onClick = { onDismiss() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Zamknij",
                tint = Color.White
            )
        }
    }
}


fun getThumbnailFromUri(context: Context, mediaUri: Uri): Bitmap? {
    return try {
        context.contentResolver.loadThumbnail(mediaUri, Size(200, 200), null)
    } catch (e: Exception) {
        null
    }
}

suspend fun getThumbnailFromUrl(context: Context, mediaUri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val bitmap: Bitmap = Glide.with(context)
                .asBitmap()
                .load(mediaUri)
                .submit()
                .get()
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}
