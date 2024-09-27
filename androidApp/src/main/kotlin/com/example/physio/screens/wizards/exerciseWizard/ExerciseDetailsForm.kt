package com.example.physio.screens.wizards.exerciseWizard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.PermMedia
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.physio.screens.wizards.ActionButton
import com.example.physio.screens.wizards.AutoCompleteDetailed
import com.example.physio.screens.wizards.CreatorWizardViewModel
import com.example.physio.ui.colorPrimary
import com.example.physio.ui.gray
import com.example.physio.ui.typography

@Composable
fun ExerciseDetailsForm(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: CreatorWizardViewModel,
    isEditorNextStep: Boolean
) {
    val context = LocalContext.current
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val equipmentList by viewModel.equipmentList.collectAsState()
    val selectedConditions by viewModel.selectedConditions.collectAsState()
    val conditionsList by viewModel.conditionsList.collectAsState()
    val selectedMediaUris by viewModel.selectedMediaUris.collectAsState()
    val description by viewModel.exerciseDescription.collectAsState()
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        viewModel.addSelectedMedia(context, uris)
    }

    var previewMediaUri by remember { mutableStateOf<Uri?>(null) }

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
                val titleText =
                    if (isEditorNextStep) "Edytuj tytuł ćwiczenia" else "Podaj tytuł ćwiczenia"

                Text(
                    text = buildAnnotatedString {
                        append(if (isEditorNextStep) "Edytor istniejącego " else "Kreator nowego ")
                        withStyle(style = SpanStyle(color = colorPrimary)) {
                            append("ćwiczenia")
                        }
                    },
                    style = typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = titleText, style = typography.labelLarge)
            }

            item {
                TextField(
                    value = viewModel.exerciseTitle.collectAsState().value ?: "",
                    onValueChange = { newTitle -> viewModel.updateExerciseTitle(newTitle) },
                    label = { Text("Tytuł ćwiczenia", style = typography.labelMedium) },
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
            }

            item {
                Text(
                    text = "Wybierz potrzebny sprzęt",
                    style = typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                AutoCompleteDetailed(
                    itemList = equipmentList,
                    selectedItems = selectedEquipment,
                    onToggleItem = { equipmentId -> viewModel.toggleEquipment(equipmentId) }
                )
            }

            item {
                Text(
                    text = "Uzupełnij opis i multimedia",
                    style = typography.labelLarge,
                    modifier = Modifier.height(32.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(200.dp, 500.dp)
                        .padding(vertical = 2.dp)
                ) {
                    TextEditorView(
                        initialDescription = description ?: "",
                        viewModel = viewModel
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActionButton(
                        icon = Icons.Outlined.PermMedia,
                        label = "Dodaj multimedia",
                        onClick = { mediaPickerLauncher.launch("image/*, video/*") },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                if (selectedMediaUris.isNotEmpty()) {

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(selectedMediaUris.size) { index ->
                            val uri = selectedMediaUris[index]
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        previewMediaUri = uri
                                    }
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(
                                            Color.Red.copy(alpha = 0.7f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            viewModel.removeMediaUri(uri)
                                        }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove media",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }

                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }


        previewMediaUri?.let { uri ->
            AlertDialog(
                onDismissRequest = { previewMediaUri = null },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        if (uri.toString().contains("image")) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else if (uri.toString().contains("video")) {
                            Text(
                                text = "Podgląd wideo",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { previewMediaUri = null }) {
                        Text("Zamknij")
                    }
                }
            )
        }
    }
}