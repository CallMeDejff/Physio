package com.dawidkubica.physio.screens.exercise.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.dawidkubica.physio.screens.exercise.ExerciseViewModel
import com.dawidkubica.physio.ui.icons.Person_celebrate
import com.dawidkubica.physio.ui.icons.Self_improvement

@SuppressLint("UnrememberedMutableInteractionSource", "UnusedBoxWithConstraintsScope")
@Composable
fun ExercisesView(
    viewModel: ExerciseViewModel,
    modifier: Modifier,
    onMediaClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val exercises by viewModel.fetchedExercises.collectAsState()
    val warmups by viewModel.fetchedWarmUps.collectAsState()
    val equipmentList by viewModel.equipmentFullList.collectAsState()

    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    var selectedTab by remember { mutableStateOf(ButtonType.WARMUP) }
    val itemsToShow = if (selectedTab == ButtonType.WARMUP) warmups else exercises
    val pagerState = rememberPagerState(pageCount = { itemsToShow.size })
    val currentPage = remember { mutableStateOf(pagerState.currentPage) }

    LaunchedEffect(pagerState.currentPage) { currentPage.value = pagerState.currentPage }

    LaunchedEffect(Unit) { viewModel.loadEquipmentList() }

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    if (exercises.isEmpty() && warmups.isEmpty()) {
        Text(
            text = "ćwiczenia niedostępne",
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    } else {
        Column(
            Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val buttons = listOf(
                ButtonItem(
                    type = ButtonType.WARMUP,
                    text = "Rozgrzewka",
                    icon = Person_celebrate,
                    borderColor = MaterialTheme.colorScheme.primary
                ),
                ButtonItem(
                    type = ButtonType.EXERCISE,
                    text = "Ćwiczenia",
                    icon = Self_improvement,
                    borderColor = MaterialTheme.colorScheme.primary
                ),
            )

            MenuButtons(
                buttons = buttons,
                selectedTab = selectedTab,
                onTabSelected = { newTab -> selectedTab = newTab }
            )

            HorizontalPagerIndicator(
                pageCount = itemsToShow.size,
                currentPage = pagerState.currentPage,
                targetPage = pagerState.targetPage,
                currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                beyondViewportPageCount = 1,
                verticalAlignment = Alignment.Top,
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    pagerSnapDistance = PagerSnapDistance.atMost(1)
                ),
                contentPadding = PaddingValues(horizontal = 4.dp),
                pageSpacing = 0.dp
            ) { page ->
                val exercise = itemsToShow[page]
                val isVisible = remember { derivedStateOf { pagerState.currentPage == page } }

                if (isVisible.value) {
                    ExerciseCard(
                        exercise = exercise,
                        equipmentList = equipmentList,
                        onMediaClick = onMediaClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
