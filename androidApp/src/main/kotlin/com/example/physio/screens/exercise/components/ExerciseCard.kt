package com.example.physio.screens.exercise.components

import android.text.Html
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.physio.models.Exercise
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.colorTertiary
import com.example.physio.ui.theme.typography

@Composable
fun ExerciseCard(
    exercise: Exercise,
    equipmentList: List<Pair<String, String>>,
    onMediaClick: (String, String) -> Unit,
    modifier: Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorTertiary),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .padding(8.dp)
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
                val truncatedDescription = if (!isExpanded && decodedDescription.length > 100) {
                    "${decodedDescription.substring(0, 100)}..."
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
                    modifier = Modifier
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 8.dp)
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}