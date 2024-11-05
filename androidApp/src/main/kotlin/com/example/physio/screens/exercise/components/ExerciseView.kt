package com.example.physio.screens.exercise.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.physio.screens.exercise.ExerciseViewModel
import com.example.physio.ui.icons.Person_celebrate
import com.example.physio.ui.icons.Self_improvement
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.colorSecondary

@Composable
fun ExercisesView(
    viewModel: ExerciseViewModel,
    modifier: Modifier,
    onMediaClick: (String, String) -> Unit
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
            Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
        ) {
            val buttons = listOf(
                ButtonItem(
                    type = ButtonType.WARMUP,
                    text = "Rozgrzewka",
                    icon = Person_celebrate,
                    borderColor = colorSecondary
                ),
                ButtonItem(
                    type = ButtonType.EXERCISE,
                    text = "Ćwiczenia",
                    icon = Self_improvement,
                    borderColor = colorPrimary
                ),
            )

            MenuButtons(
                buttons = buttons,
                selectedTab = selectedTab,
                onTabSelected = { newTab -> selectedTab = newTab }
            )

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

