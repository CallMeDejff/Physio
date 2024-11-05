package com.example.physio.screens.exercise.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun VideoThumbnail(mediaUrl: String, onMediaClick: (String) -> Unit) {
    val context = LocalContext.current
    var thumbnailBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(mediaUrl) {
        if (mediaUrl.isNotEmpty()) {
            val uri = Uri.parse(mediaUrl)
            thumbnailBitmap = getThumbnailFromUrl(context, uri)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onMediaClick(mediaUrl) },
        contentAlignment = Alignment.Center
    ) {
        if (thumbnailBitmap != null) {
            Image(
                bitmap = thumbnailBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        Icon(
            imageVector = Icons.Outlined.PlayCircleOutline,
            contentDescription = "Play Video",
            modifier = Modifier.size(64.dp)
                .align(Alignment.Center)
        )
    }
}

