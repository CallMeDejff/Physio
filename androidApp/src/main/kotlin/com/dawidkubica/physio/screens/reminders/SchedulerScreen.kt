package com.dawidkubica.physio.screens.reminders

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.screens.reminders.components.ReminderItem
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun SchedulerScreen(
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReminderViewModel
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val reminders by viewModel.reminders.observeAsState(emptyList())

    val daysOfWeek = listOf("PN", "WT", "ŚR", "CZ", "PT", "SB", "ND")
    var selectedDay by remember { mutableIntStateOf(0) }

    val pagerState = rememberPagerState(pageCount = { daysOfWeek.size })

    LaunchedEffect(selectedDay) {
        pagerState.scrollToPage(selectedDay)
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp, top = 10.dp)
            ) {
                Column {
                    Text(
                        text = buildAnnotatedString {
                            append(" Zaplanuj swój ")
                            withStyle(style = SpanStyle(color = colorPrimary)) {
                                append("harmonogram.")
                            }
                        },
                        style = typography.headlineLarge,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {

                        item {
                            DayTile(
                                day = "+",
                                isSelected = true,
                                onClick = {
                                    viewModel.onAddReminderClick(navigate)
                                }
                            )
                        }

                        items(daysOfWeek.size) { index ->
                            DayTile(
                                day = daysOfWeek[index],
                                isSelected = selectedDay == index,
                                onClick = {
                                    selectedDay = index
                                }
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val dayReminders = reminders.filter { it.dayOfWeek == daysOfWeek[page] }

                        Column(modifier = Modifier.padding(16.dp)) {
                            dayReminders.forEach { reminder ->
                                ReminderItem(
                                    reminder = reminder,
                                    deletable = true,
                                    onDelete = {
                                        viewModel.onDeleteReminderClick(reminder.id)
                                    }
                                )
                            }

                            if (dayReminders.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Brak wyników",
                                        style = typography.labelMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DayTile(
    day: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) colorPrimary else Color.Transparent
    val textColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(width = 2.dp, color = colorPrimary, shape = RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(8.dp)
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = textColor,
            style = typography.bodyMedium
        )
    }
}


