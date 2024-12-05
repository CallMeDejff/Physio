package com.dawidkubica.physio.screens.exercise.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun MenuButtons(
    buttons: List<ButtonItem>,
    selectedTab: ButtonType,
    onTabSelected: (ButtonType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        buttons.forEach { button ->
            Button(
                onClick = { onTabSelected(button.type) },
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = if (button.type == selectedTab) MaterialTheme.colorScheme.primary else button.borderColor,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (button.type == selectedTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(
                    imageVector = button.icon,
                    contentDescription = "${button.text} button",
                    tint = if (selectedTab == button.type) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = button.text,
                    color = if (selectedTab == button.type) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
                    style = typography.labelLarge,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

data class ButtonItem(
    val type: ButtonType,
    val text: String,
    val icon: ImageVector,
    val borderColor: Color
)

enum class ButtonType {
    WARMUP, EXERCISE
}
