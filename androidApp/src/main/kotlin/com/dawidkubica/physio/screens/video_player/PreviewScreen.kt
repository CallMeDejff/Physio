package com.dawidkubica.physio.screens.video_player

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(UnstableApi::class)
@Composable
fun PreviewScreen(
    mediaUrl: String,
    mediaType: String? = null,
    onDismiss: () -> Unit,
    isUrl: Boolean = false
) {
    val context = LocalContext.current
    val isVideo =
        mediaType == "video" || (!isUrl && context.contentResolver.getType(Uri.parse(mediaUrl))
            ?.startsWith("video/") == true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .align(Alignment.Center)
        ) {
            if (isVideo) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.Center)
                ) {
                    VideoPlayer(
                        videoUrl = mediaUrl
                    )
                }
            } else {
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
            }

            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Close preview",
                    tint = Color.White
                )
            }
        }
    }
}

fun getThumbnailFromUri(context: Context, mediaUri: Uri): Bitmap? {
    return try {
        context.contentResolver.loadThumbnail(mediaUri, Size(200, 200), null)
    } catch (e: Exception) {
        Log.e("PreviewScreen", "Error loading thumbnail from Uri: ${e.message}")
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
            Log.e("PreviewScreen", "Error loading thumbnail from Url: ${e.message}")
            null
        }
    }
}


