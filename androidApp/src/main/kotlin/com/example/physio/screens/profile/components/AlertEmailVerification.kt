package com.example.physio.screens.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.physio.ui.theme.colorSecondary

@Composable
fun AlertEmailVerification(
    showDialog: MutableState<Boolean>,
    statement: String,
    confirmStat: String,
    dismissStat: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val emailState = remember { mutableStateOf(TextFieldValue("")) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Potwierdzenie") },
            text = {
                Column (
                    modifier = Modifier
                        .padding(8.dp)
                ){
                    Text(statement)

                    Spacer(modifier = Modifier.height(16.dp))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    showDialog.value = false
                }) {
                    Text(
                        text = confirmStat,
                        color = colorSecondary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    onDismiss()
                }) {
                    Text(
                        text = dismissStat,
                        color = Color.Red
                    )
                }
            }
        )
    }
}