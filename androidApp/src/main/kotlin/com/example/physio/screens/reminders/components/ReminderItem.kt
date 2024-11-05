package com.example.physio.screens.reminders.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.physio.models.Reminder
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.typography

@Composable
fun ReminderItem(
    reminder: Reminder,
    deletable: Boolean,
    onDelete: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .border(width = 2.dp, color = colorPrimary, shape = RoundedCornerShape(16.dp))
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
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier
                    .weight(4f)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
                    .weight(4f)
            ) {
                Text(reminder.topic, style = typography.labelLarge)
                Text("${reminder.dayOfWeek} - ${reminder.time}", style = typography.labelMedium)
            }

            if(deletable) {
                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete Reminder",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}