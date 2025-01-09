package com.dawidkubica.physio.screens.video_player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.models.ExerciseMediaItem
import com.dawidkubica.physio.ui.icons.Timelapse
import com.dawidkubica.physio.ui.icons.Timer
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun MediaItemStats(
    mediaItem: ExerciseMediaItem
) {
    Box(
        Modifier
            .wrapContentSize()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Row(
            Modifier
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = mediaItem.title,
                style = typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "|",
                style = typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Timer,
                contentDescription = "Timer Icon",
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = ": " + if (mediaItem.time.toString() == "0") "-" else mediaItem.time.toString() + " min",
                style = typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Timelapse,
                contentDescription = "Timelapse Icon",
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = ": " + if (mediaItem.time.toString() == "0") "-" else mediaItem.time.toString() + "x",
                style = typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}