package com.example.physio.screens.editUser.components

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
fun AlertEmailChange(
    showDialog: MutableState<Boolean>,
    statement: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val emailState = remember { mutableStateOf(TextFieldValue("")) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Potwierdzenie") },
            text = {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(statement)

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = emailState.value,
                        onValueChange = { emailState.value = it },
                        label = { Text("Adres Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(emailState.value.text)
                    showDialog.value = false
                }) {
                    Text(
                        text = "Zmieniam dane",
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
                        text = "Nie",
                        color = Color.Red
                    )
                }
            }
        )
    }
}

