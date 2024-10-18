package com.example.physio.screens.exercise.components

import android.text.Html
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.unit.dp
import com.example.physio.models.Exercise
import com.example.physio.ui.theme.typography

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onMediaClick: (String, String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .width(300.dp)
            //.fillMaxHeight()
            .wrapContentSize(Alignment.TopStart),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            MediaView(
                mediaUrls = exercise.mediaUrls,
                mediaType = exercise.mediaType,
                onMediaClick = { mediaUrl ->
                    onMediaClick(mediaUrl, exercise.mediaType)
                })

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exercise.title,
                style = typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            val decodedDescription = Html.fromHtml(exercise.description, Html.FROM_HTML_MODE_LEGACY)

            Text(
                text = if (isExpanded) decodedDescription.toString() else decodedDescription.toString()
                    .take(100) + "...",
                style = typography.labelMedium,
                modifier = Modifier
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 8.dp)
            )
        }
    }
}