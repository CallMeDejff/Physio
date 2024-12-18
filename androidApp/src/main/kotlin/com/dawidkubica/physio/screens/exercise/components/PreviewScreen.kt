package com.dawidkubica.physio.screens.exercise.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
@Composable
fun PreviewScreen(
    mediaUrl: String,
    mediaType: String? = null,
    onDismiss: () -> Unit,
    isUrl: Boolean = false
) {
    val context = LocalContext.current
    val isVideo = mediaType == "video" || (!isUrl && context.contentResolver.getType(Uri.parse(mediaUrl))?.startsWith("video/") == true)

    var videoAspectRatio by remember { mutableStateOf(16 / 9f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .wrapContentSize()
        ) {
            if (isVideo) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(videoAspectRatio)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    VideoPlayer(
                        videoUrl = mediaUrl,
                        onVideoSizeChanged = { width, height ->
                            videoAspectRatio = if (width > 0 && height > 0) width.toFloat() / height else 16 / 9f
                        }
                    )
                }
            } else {
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .clip(RoundedCornerShape(16.dp)),
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
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
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


