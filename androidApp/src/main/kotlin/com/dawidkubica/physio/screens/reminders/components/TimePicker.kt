package com.dawidkubica.physio.screens.reminders.components

import android.annotation.SuppressLint
import android.widget.TimePicker
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.ui.theme.typography
import androidx.compose.material3.TimePicker

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(onTimeSelected: (String) -> Unit) {
    var selectedTime by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    selectedTime = String.format("%02d:%02d", hour, minute)
                    onTimeSelected(selectedTime)
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Anuluj")
                }
            },
            title = {
                Text("Wybierz godzinę")
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    layoutType = TimePickerDefaults.layoutType(),
                    colors = TimePickerDefaults.colors(

                    )
                )
            }
        )
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { showDialog = true }
            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Text(
            text = if (selectedTime.isEmpty()) "Wybierz godzinę" else "Wybrana godzina: $selectedTime",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 8.dp),
            style = typography.labelLarge
        )
    }
}
