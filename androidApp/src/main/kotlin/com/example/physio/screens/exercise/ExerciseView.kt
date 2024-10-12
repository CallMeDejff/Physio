package com.example.physio.screens.exercise

import android.content.Context
import android.text.Html
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.physio.models.Exercise
import com.example.physio.ui.Person_celebrate
import com.example.physio.ui.Self_improvement
import com.example.physio.ui.colorPrimary
import com.example.physio.ui.colorSecondary
import com.example.physio.ui.typography

@Composable
fun ExercisesView(
    viewModel: ExerciseViewModel,
    modifier: Modifier,
    onMediaClick: (String) -> Unit
) {
    val exercises by viewModel.fetchedExercises.collectAsState()
    val warmups by viewModel.fetchedWarmUps.collectAsState()

    var selectedTab by remember { mutableStateOf(ButtonType.WARMUP) }

    Log.d("ExercisesView", "Rendering exercises: $exercises")
    Log.d("ExercisesView", "Rendering warmups: $warmups")

    if (exercises.isEmpty() && warmups.isEmpty()) {
        Text(text = "ćwiczenia niedostępne", modifier = Modifier.padding(16.dp))
    } else {
        Column(
            Modifier.fillMaxSize()
        ) {
            MenuButtons(selectedTab) { newSelection ->
                selectedTab = newSelection
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp, top = 16.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val itemsToShow = if (selectedTab == ButtonType.WARMUP) warmups else exercises
                items(itemsToShow) { exercise ->
                    Log.d("ExercisesView", "Rendering: ${exercise.title}")
                    ExerciseCard(exercise = exercise, onMediaClick = onMediaClick)
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onMediaClick: (String) -> Unit
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
            MediaView(mediaUrls = exercise.mediaUrls, onMediaClick = onMediaClick)

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

@Composable
fun VideoThumbnail(mediaUrl: String, onMediaClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onMediaClick(mediaUrl) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.PlayCircleOutline,
            contentDescription = "Play Video",
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun PreviewScreen(mediaUrl: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        when {
            mediaUrl.contains("image") -> {
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            mediaUrl.contains("video") -> {
                VideoPlayer(mediaUrl = mediaUrl, context = LocalContext.current)
            }

            else -> {
                Text("Nieobsługiwany format multimediów", color = Color.White)
            }
        }
        IconButton(
            onClick = { onDismiss() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Zamknij",
                tint = Color.White
            )
        }
    }
}

@Composable
fun VideoPlayer(mediaUrl: String, context: Context) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(mediaUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { PlayerView(context).apply { player = exoPlayer } }
        )
    ) {
        onDispose { exoPlayer.release() }
    }
}

@Composable
fun MenuButtons(selectedTab: ButtonType, onTabSelected: (ButtonType) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onTabSelected(ButtonType.WARMUP) },
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = colorSecondary,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == ButtonType.WARMUP) colorPrimary else Color.White
            )
        ) {
            Icon(
                imageVector = Person_celebrate,
                contentDescription = "warmup button",
                tint = if (selectedTab == ButtonType.WARMUP) Color.White else colorPrimary
            )
            Text(
                text = "Rozgrzewka",
                color = if (selectedTab == ButtonType.WARMUP) Color.White else colorPrimary,
                style = typography.labelLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
        Button(
            onClick = { onTabSelected(ButtonType.EXERCISE) },
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = colorPrimary,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == ButtonType.EXERCISE) colorPrimary else Color.White,
            )
        ) {
            Icon(
                imageVector = Self_improvement,
                contentDescription = "exercise button",
                tint = if (selectedTab == ButtonType.EXERCISE) Color.White else colorPrimary
            )
            Text(
                text = "Ćwiczenia",
                color = if (selectedTab == ButtonType.EXERCISE) Color.White else colorPrimary,
                style = typography.labelLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

enum class ButtonType {
    WARMUP, EXERCISE
}

