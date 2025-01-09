package com.dawidkubica.physio.screens.exercise.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.models.Exercise
import com.dawidkubica.physio.ui.icons.Timelapse
import com.dawidkubica.physio.ui.icons.Timer
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ExerciseStats(
    exercise: Exercise,
    equipmentList: List<Pair<String, String>> = emptyList(),
) {
    Box(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            Modifier
                .padding(vertical = 6.dp)
                .align(Alignment.TopStart),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = exercise.title,
                color = MaterialTheme.colorScheme.onBackground,
                style = typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                Modifier
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Timer,
                    contentDescription = "Timer Icon",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = ": " + if (exercise.time.toString() == "0") "-" else exercise.time.toString() + " min",
                    style = typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Timelapse,
                    contentDescription = "Timelapse Icon",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = ": " + if (exercise.time.toString() == "0") "-" else exercise.time.toString() + "x",
                    style = typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                )
            }
            EquipmentStats(exercise = exercise, equipmentList = equipmentList)
        }
    }
}