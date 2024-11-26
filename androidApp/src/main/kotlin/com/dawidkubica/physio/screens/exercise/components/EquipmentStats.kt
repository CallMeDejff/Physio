package com.dawidkubica.physio.screens.exercise.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.models.Exercise
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun EquipmentStats(exercise: Exercise, equipmentList: List<Pair<String, String>>) {
    val equipmentNames = exercise.equipmentIds.mapNotNull { equipmentId ->
        equipmentList.find { it.first == equipmentId }?.second
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(equipmentNames) { item ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = colorPrimary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = item,
                    color = colorPrimary,
                    style = typography.labelMedium
                )
            }
        }
    }
}