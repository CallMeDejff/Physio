package com.example.physio.screens.wizards.exerciseWizard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PermMedia
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.physio.screens.exercise.components.PreviewScreen
import com.example.physio.screens.wizards.CreatorWizardViewModel
import com.example.physio.screens.wizards.components.ActionButton
import com.example.physio.screens.wizards.components.CustomAlertDialog
import com.example.physio.screens.wizards.components.TextEditorView
import com.example.physio.ui.components.AutoCompleteDetailed
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.gray
import com.example.physio.ui.theme.typography

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
    val selectedMediaUris by viewModel.selectedMediaUris.collectAsState()
    val description by viewModel.exerciseDescription.collectAsState()
    val mediaType by viewModel.mediaType.collectAsState()

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.addSelectedMedia(context, uri)
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var previewMediaUri by remember { mutableStateOf<Uri?>(null) }
    var showPreviewDialog by remember { mutableStateOf(false) }

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
                        onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                if (selectedMediaUris.isNotEmpty()) {
                    val selectedMediaUri = selectedMediaUris.first()

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                previewMediaUri = selectedMediaUri
                                showPreviewDialog = true
                            }
                    ) {
                        AsyncImage(
                            model = selectedMediaUri,
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
                                    viewModel.removeMediaUri(selectedMediaUri)
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
            item {
                if (isEditorNextStep) {
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
                            contentDescription = "Delete exercise",
                            tint = Color.Red
                        )
                        Text(
                            text = "Usuń ćwiczenie",
                            color = Color.Red,
                            style = typography.labelLarge,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    if (showDialog) {
                        CustomAlertDialog(
                            title = "Usuwanie ćwiczenia",
                            icon = Icons.Outlined.DeleteOutline,
                            message = "Czy na pewno chcesz usunąć to ćwiczenie? Pamiętaj, że usunąć możesz jedynie, które zostały utworzone przez Ciebie.",
                            onConfirm = { viewModel.deleteExercise(navigate) },
                            onDismiss = { showDialog = false }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        if (isEditorNextStep) {
            if (showPreviewDialog) {
                PreviewScreen(
                    mediaUrl = previewMediaUri.toString(),
                    mediaType = mediaType.toString(),
                    onDismiss = {
                        showPreviewDialog = false
                        previewMediaUri = null
                    })
            }
        }
    }
}
