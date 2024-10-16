package com.example.physio.screens.exercise.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MediaView(mediaUrls: List<String>, onMediaClick: (String) -> Unit) {
    if (mediaUrls.isNotEmpty()) {
        val mediaUrl = mediaUrls.first()

        when {
            mediaUrl.contains("image") -> {
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { onMediaClick(mediaUrl) },
                    contentScale = ContentScale.Crop
                )
            }

            mediaUrl.contains("video") -> {
                VideoThumbnail(mediaUrl = mediaUrl, onMediaClick = onMediaClick)
            }

            else -> {
                Text("Nieobsługiwany format multimediów", modifier = Modifier.padding(16.dp))
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Multimedia niedostepne")
        }
    }
}