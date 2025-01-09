package com.dawidkubica.physio.screens.exercise.components

import android.text.Html
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.models.Exercise
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ExerciseCard(
    modifier: Modifier,
    isPaused: Boolean,
    exercise: Exercise,
    equipmentList: List<Pair<String, String>>,
    onMediaClick: (String, String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        modifier = Modifier
            .padding(8.dp)
            .animateContentSize()
            .heightIn(max = 650.dp),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(bottom = 32.dp)
                .wrapContentHeight()
        ) {
            item {
                MediaView(
                    isPaused = isPaused,
                    mediaUrls = exercise.mediaUrls,
                    mediaType = exercise.mediaType,
                    onMediaClick = { mediaUrl ->
                        onMediaClick(mediaUrl, exercise.mediaType)
                    }
                )
            }

            item {
                ExerciseStats(exercise, equipmentList)
            }

            item {
                val decodedDescription =
                    Html.fromHtml(exercise.description, Html.FROM_HTML_MODE_LEGACY)
                val truncatedDescription = if (!isExpanded && decodedDescription.length > 250) {
                    "${decodedDescription.substring(0, 250)}..."
                } else {
                    decodedDescription.toString()
                }

                val formattedDescription = spannedToAnnotatedString(
                    Html.fromHtml(
                        truncatedDescription,
                        Html.FROM_HTML_MODE_LEGACY
                    )
                )

                Text(
                    text = formattedDescription,
                    style = typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 8.dp)
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}