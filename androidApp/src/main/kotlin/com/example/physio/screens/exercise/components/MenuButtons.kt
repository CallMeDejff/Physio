package com.example.physio.screens.exercise.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.physio.ui.icons.Person_celebrate
import com.example.physio.ui.icons.Self_improvement
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.colorSecondary
import com.example.physio.ui.theme.typography

@Composable
fun MenuButtons(selectedTab: ButtonType, onTabSelected: (ButtonType) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onTabSelected(ButtonType.WARMUP) },
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = colorSecondary,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == ButtonType.WARMUP) colorPrimary else Color.White
            )
        ) {
            Icon(
                imageVector = Person_celebrate,
                contentDescription = "warmup button",
                tint = if (selectedTab == ButtonType.WARMUP) Color.White else colorPrimary
            )
            Text(
                text = "Rozgrzewka",
                color = if (selectedTab == ButtonType.WARMUP) Color.White else colorPrimary,
                style = typography.labelLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
        Button(
            onClick = { onTabSelected(ButtonType.EXERCISE) },
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = colorPrimary,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == ButtonType.EXERCISE) colorPrimary else Color.White,
            )
        ) {
            Icon(
                imageVector = Self_improvement,
                contentDescription = "exercise button",
                tint = if (selectedTab == ButtonType.EXERCISE) Color.White else colorPrimary
            )
            Text(
                text = "Ćwiczenia",
                color = if (selectedTab == ButtonType.EXERCISE) Color.White else colorPrimary,
                style = typography.labelLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

enum class ButtonType {
    WARMUP, EXERCISE
}