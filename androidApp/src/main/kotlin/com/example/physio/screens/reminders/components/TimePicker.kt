package com.example.physio.screens.reminders.components

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.physio.ui.theme.typography
import java.util.Calendar

@SuppressLint("DefaultLocale")
@Composable
fun TimePicker(onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    var selectedTime by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(
                    context,
                    { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                        selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                        onTimeSelected(selectedTime)
                    },
                    hour,
                    minute,
                    true
                )
                timePickerDialog.show()
            }
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
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