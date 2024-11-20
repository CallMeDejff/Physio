package com.example.physio.screens.exercise.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MediaView(
    mediaUrls: List<String>,
    mediaType: String,
    onMediaClick: (String) -> Unit
) {
    if (mediaUrls.isNotEmpty()) {
        val mediaUrl = mediaUrls.first()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .height(200.dp),
        ) {
            when (mediaType) {
                "image" -> {
                    AsyncImage(
                        model = mediaUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                "video" -> {
                    VideoPlayer(
                        videoUrl = mediaUrl,
                        onClick = { },
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                else -> {
                    Text(
                        "Nieobsługiwany format multimediów",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.Fullscreen,
                contentDescription = "Play or Expand",
                tint = Color.Gray,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(48.dp)
                    .clickable { onMediaClick(mediaUrl) }
                    .padding(4.dp)
            )
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
