package com.example.physio.screens.wizards.packageWizard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.physio.screens.wizards.viewmodels.CreatorWizardViewModel
import com.example.physio.screens.wizards.components.CustomAlertDialog
import com.example.physio.screens.wizards.components.TextEditorView
import com.example.physio.screens.wizards.viewmodels.PackageCreatorViewModel
import com.example.physio.ui.components.AutoCompleteDetailed
import com.example.physio.ui.icons.Clinical_notes
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.gray
import com.example.physio.ui.theme.typography

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun PackageDetailsForm(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: PackageCreatorViewModel,
    isEditor: Boolean = false
) {
    val exercisesList by viewModel.exercisesList.collectAsState()
    val conditionsList by viewModel.conditionsList.collectAsState()
    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val selectedWarmUp by viewModel.selectedWarmUp.collectAsState()
    val selectedConditions by viewModel.selectedConditions.collectAsState()
    val description by viewModel.packageDescription.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 120.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (isEditor) {
                    Text(
                        text = buildAnnotatedString {
                            append("Edytor istniejącego ")

                            withStyle(style = SpanStyle(color = colorPrimary)) {
                                append("pakietu ćwiczeń")
                            }
                        },
                        style = typography.bodyLarge,
                    )
                } else {
                    Text(
                        text = buildAnnotatedString {
                            append("Kreator nowego ")

                            withStyle(style = SpanStyle(color = colorPrimary)) {
                                append("pakietu ćwiczeń")
                            }
                        },
                        style = typography.bodyLarge,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = viewModel.packageName.collectAsState().value ?: "",
                    onValueChange = { newName -> viewModel.updatePackageName(newName) },
                    label = {
                        Text(
                            "Nazwa pakietu ćwiczeń",
                            style = typography.labelLarge,
                            color = Color.Black,
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = colorPrimary,
                        unfocusedIndicatorColor = gray,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions.Default
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(
                    text = "Wybierz ćwiczenia na rozgrzewkę",
                    style = typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .heightIn(200.dp, 300.dp)
                ) {
                    AutoCompleteDetailed(
                        itemList = exercisesList,
                        selectedItems = selectedWarmUp,
                        onToggleItem = { equipmentId ->
                            viewModel.toggleWarmUp(equipmentId)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(
                    text = "Wybierz ćwiczenia w ramach pakietu ćwiczeń",
                    style = typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .heightIn(200.dp, 300.dp)
                ) {
                    AutoCompleteDetailed(
                        itemList = exercisesList,
                        selectedItems = selectedExercises,
                        onToggleItem = { exerciseId ->
                            viewModel.toggleExercises(exerciseId, true)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(
                    text = "Wybierz schorzenia przypisane do tego pakietu ćwiczeń",
                    style = typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .heightIn(200.dp, 300.dp)
                ) {
                    AutoCompleteDetailed(
                        itemList = conditionsList,
                        selectedItems = selectedConditions,
                        onToggleItem = { conditionId ->
                            viewModel.toggleCondition(conditionId, true)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(
                    text = "Uzupełnij opis",
                    style = typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(200.dp, 600.dp)
                        .padding(vertical = 2.dp)
                ) {
                    TextEditorView(
                        initialDescription = description ?: "",
                        viewModel = viewModel
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                }
            }

            item {
                if (isEditor) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete package",
                            tint = Color.Red
                        )
                        Text(
                            text = "Usuń pakiet",
                            color = Color.Red,
                            style = typography.labelLarge,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    if (showDialog) {
                        CustomAlertDialog(
                            title = "Usuwanie pakietu ćwiczeń",
                            icon = Icons.Outlined.DeleteOutline,
                            message = "Czy na pewno chcesz usunąć ten pakiet ćwiczeń? Pamiętaj, że usunąć możesz jedynie pakiety, które nie są przypisane do żadnych pacjentów i zostały stworzone przez Ciebie.",
                            onConfirm = { viewModel.deletePackage(navigate) },
                            onDismiss = { showDialog = false }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

    }
}
