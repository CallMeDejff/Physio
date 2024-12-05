package com.dawidkubica.physio.screens.reminders.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.models.Reminder
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography
@Composable
fun ReminderItem(
    reminder: Reminder,
    deletable: Boolean,
    onDelete: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(6.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = "Timer Icon",
                modifier = Modifier.weight(1f),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Column(
                modifier = Modifier
                    .weight(4f)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = reminder.topic,
                    style = typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val fullDay = mapShortDayToFull(reminder.dayOfWeek)
                Text(
                    text = "$fullDay - ${reminder.time}",
                    style = typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (deletable) {
                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Reminder",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun mapShortDayToFull(shortDay: String): String {
    return when (shortDay) {
        "PN" -> "Poniedziałek"
        "WT" -> "Wtorek"
        "ŚR" -> "Środa"
        "CZ" -> "Czwartek"
        "PT" -> "Piątek"
        "SB" -> "Sobota"
        "ND" -> "Niedziela"
        else -> "Poniedziałek"
    }
}
