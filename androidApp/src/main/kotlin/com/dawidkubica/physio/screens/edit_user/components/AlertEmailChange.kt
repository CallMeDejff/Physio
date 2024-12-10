package com.dawidkubica.physio.screens.edit_user.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.R
import com.dawidkubica.physio.ui.theme.RedConfirmed

@Composable
fun AlertEmailChange(
    showDialog: MutableState<Boolean>,
    statement: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val emailState = remember { mutableStateOf(TextFieldValue("")) }
    val confirmText = stringResource(id = R.string.change_data_confirm)
    val dismissText = stringResource(id = R.string.no)
    val emailLabel = stringResource(id = R.string.email_label)
    val titleText = stringResource(id = R.string.confirmation)

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(titleText) },
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
                        label = { Text(emailLabel) },
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
                        text = confirmText,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    onDismiss()
                }) {
                    Text(
                        text = dismissText,
                        color = RedConfirmed
                    )
                }
            }
        )
    }
}

