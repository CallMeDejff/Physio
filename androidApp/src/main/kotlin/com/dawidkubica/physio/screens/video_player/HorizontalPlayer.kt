package com.dawidkubica.physio.screens.video_player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowLeft
import androidx.compose.material.icons.automirrored.outlined.ArrowRight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.util.Log
import coil.compose.AsyncImage
import com.dawidkubica.physio.models.Exercise
import com.dawidkubica.physio.models.ExerciseMediaItem
import com.dawidkubica.physio.screens.video_player.components.MediaItemStats

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun HorizontalPlayerScreen(
    exercises: List<Exercise>,
) {
    val mediaItems = exercises.flatMap { exercise ->
        exercise.mediaUrls.map { url ->
            ExerciseMediaItem(
                title = exercise.title,
                mediaUrl = url,
                mediaType = exercise.mediaType,
                time = exercise.time,
                attempts = exercise.attempts
            )
        }
    }

    Log.d("HorizontalPlayerScreen", "Media items: $mediaItems")

    var currentIndex by remember { mutableStateOf(0) }
    val currentMedia = mediaItems[currentIndex]
    val isVideo = currentMedia.mediaType == "video"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
                .wrapContentSize()
                .zIndex(1f)
        ) {
            MediaItemStats(
                mediaItem = currentMedia,
            )
        }

        if (isVideo) {
            VideoPlayer(
                videoUrl = currentMedia.mediaUrl
            )
        } else {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .wrapContentSize(),
            ) {
                AsyncImage(
                    model = currentMedia.mediaUrl,
                    contentDescription = "Media Image",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                modifier = Modifier.size(42.dp),
                onClick = {
                    if (currentIndex > 0) currentIndex--
                },
                enabled = currentIndex > 0
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowLeft,
                    contentDescription = "Previous Media",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(42.dp)
                )
            }

            IconButton(
                modifier = Modifier.size(42.dp),
                onClick = {
                    if (currentIndex < mediaItems.size - 1) currentIndex++
                },
                enabled = currentIndex < mediaItems.size - 1
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowRight,
                    contentDescription = "Next Media",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(42.dp)
                )
            }
        }
    }
}
