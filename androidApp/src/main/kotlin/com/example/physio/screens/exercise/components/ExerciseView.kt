package com.example.physio.screens.exercise.components

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.physio.screens.exercise.ExerciseViewModel
import com.example.physio.ui.icons.Person_celebrate
import com.example.physio.ui.icons.Self_improvement
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.colorSecondary

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

    var selectedTab by remember { mutableStateOf(ButtonType.WARMUP) }
    val itemsToShow = if (selectedTab == ButtonType.WARMUP) warmups else exercises
    val pagerState = rememberPagerState(pageCount = { itemsToShow.size })
    val currentPage = remember { mutableStateOf(pagerState.currentPage) }

    LaunchedEffect(pagerState.currentPage) {
        Log.d("ExercisesView", "Current page: ${pagerState.currentPage}")
        currentPage.value = pagerState.currentPage
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadEquipmentList()
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
                verticalAlignment = Alignment.Top,
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    pagerSnapDistance = PagerSnapDistance.atMost(0)
                ),
                contentPadding = PaddingValues(horizontal = 4.dp),
                pageSpacing = 0.dp
            ) { page ->
                val exercise = itemsToShow[page]

                ExerciseCard(
                    exercise = exercise,
                    equipmentList = equipmentList,
                    onMediaClick = onMediaClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}
