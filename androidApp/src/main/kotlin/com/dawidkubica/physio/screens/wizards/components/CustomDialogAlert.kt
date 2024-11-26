package com.dawidkubica.physio.screens.wizards.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.ui.theme.colorSecondary

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String = "Potwierdź",
    dismissButtonText: String = "Anuluj",
    icon: ImageVector? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Row {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title)
            }
        },
        text = { Text(text = message) },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                modifier = Modifier,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = confirmButtonText,
                    color = colorSecondary
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                modifier = Modifier,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = dismissButtonText,
                    color = Color.Red
                )
            }
        }
    )
}
