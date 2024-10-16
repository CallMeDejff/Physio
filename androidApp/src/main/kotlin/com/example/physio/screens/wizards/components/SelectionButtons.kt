package com.example.physio.screens.wizards.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.colorSecondary
import com.example.physio.ui.theme.typography

@Composable
fun SelectionButton(
    selectedButton: ButtonType?,
    setSelectedButton: (ButtonType) -> Unit,
    buttonType: ButtonType
) {
    TextButton(
        onClick = { setSelectedButton(buttonType) },
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = if (selectedButton == buttonType) colorPrimary else colorSecondary,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (selectedButton == buttonType) colorPrimary else colorSecondary)
    ) {
        Text(
            text = buttonType.displayName,
            color = Color.White,
            style = typography.labelLarge,
            modifier = Modifier.padding(8.dp)
        )
    }
}