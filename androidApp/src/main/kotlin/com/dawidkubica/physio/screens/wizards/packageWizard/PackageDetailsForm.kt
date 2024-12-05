package com.dawidkubica.physio.screens.wizards.packageWizard

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PermMedia
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.dawidkubica.physio.screens.wizards.components.ActionButton
import com.dawidkubica.physio.screens.wizards.components.CustomAlertDialog
import com.dawidkubica.physio.screens.wizards.components.TextEditorView
import com.dawidkubica.physio.screens.wizards.viewmodels.PackageCreatorViewModel
import com.dawidkubica.physio.ui.components.AutoCompleteDetailed
import com.dawidkubica.physio.ui.components.FilterableItemSelector
import com.dawidkubica.physio.ui.theme.RedConfirmed
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.gray
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun PackageDetailsForm(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: PackageCreatorViewModel,
    isEditor: Boolean = false
) {
    val exercisesList by viewModel.exercisesList.collectAsState()
    val filteredConditionsList by viewModel.filteredConditionsList.collectAsState()
    val filteredBodyPartsList by viewModel.filteredBodyPartsList.collectAsState()
    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val selectedWarmUp by viewModel.selectedWarmUp.collectAsState()
    val selectedConditions by viewModel.selectedConditions.collectAsState()
    val selectedBodyParts by viewModel.selectedBodyParts.collectAsState()
    val selectedMediaUris by viewModel.selectedMediaUris.collectAsState()
    val description by viewModel.packageDescription.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    val cropImageResultLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { uri -> viewModel.addSelectedMedia(uri) }
        } else {
            Log.e("PackageDetailsForm:", result.error.toString())
        }
    }

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                val cropImageOptions = CropImageOptions().apply {
                    guidelines = CropImageView.Guidelines.ON
                    cropShape = CropImageView.CropShape.RECTANGLE_HORIZONTAL_ONLY
                    aspectRatioX = 16
                    aspectRatioY = 10
                    fixAspectRatio = true
                }
                cropImageResultLauncher.launch(
                    CropImageContractOptions(
                        uri = it,
                        cropImageOptions = cropImageOptions
                    )
                )
            }
        }

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
                PackageHeader(isEditor = isEditor)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextFieldSection(
                    value = viewModel.packageName.collectAsState().value ?: "",
                    onValueChange = { newName -> viewModel.updatePackageName(newName) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                SelectFromListSection(
                    exercisesList = exercisesList,
                    selectedItems = selectedWarmUp,
                    onToggleItem = { equipmentId -> viewModel.toggleWarmUp(equipmentId) },
                    description = "Wybierz ćwiczenia na rozgrzewkę"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                SelectFromListSection(
                    exercisesList = exercisesList,
                    selectedItems = selectedExercises,
                    onToggleItem = { exerciseId -> viewModel.toggleExercises(exerciseId, true) },
                    description = "Wybierz ćwiczenia"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                ItemSelectorSection(
                    conditionsList = filteredConditionsList,
                    bodyPartsList = filteredBodyPartsList,
                    selectedConditions = selectedConditions ,
                    selectedBodyParts = selectedBodyParts,
                    onToggleConditions = { conditionId -> viewModel.toggleCondition(conditionId, true)},
                    onToggleBodyParts = { bodyPartId -> viewModel.toggleBodyPart(bodyPartId, true)},
                    onSearch = { query ->
                        viewModel.apply {
                            filterBodyPartsList(query)
                            filterConditionsList(query)
                        }
                    },
                    description = "Wybierz schorzenie i części ciała"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                DescriptionSection(description = description ?: "", viewModel = viewModel)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                MediaButton(
                    pickMedia = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SelectedMediaUris(
                    selectedMediaUris = selectedMediaUris,
                    onRemoveMedia = { uri -> viewModel.removeMediaUri(uri) }
                )
            }

            item {
                if (isEditor) {
                    DeletePackageButton(showDialog = showDialog, onClick = { showDialog = true })
                    if (showDialog) {
                        CustomAlertDialog(
                            title = "Usuwanie pakietu ćwiczeń",
                            icon = Icons.Outlined.DeleteOutline,
                            message = "Czy na pewno chcesz usunąć ten pakiet ćwiczeń?",
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

@Composable
fun PackageHeader(isEditor: Boolean) {
    Text(
        text = if (isEditor) {
            "Edytor istniejącego pakietu ćwiczeń"
        } else {
            "Kreator nowego pakietu ćwiczeń"
        },
        style = typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun TextFieldSection(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                "Nazwa pakietu ćwiczeń",
                style = typography.labelLarge,
                textAlign = TextAlign.Center
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
    )
}

@Composable
fun SelectFromListSection(
    exercisesList: List<Pair<String, String>>,
    selectedItems: Set<String>,
    onToggleItem: (String) -> Unit,
    description: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = description,
            style = typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Box(
            modifier = Modifier
                .heightIn(200.dp, 300.dp)
        ) {
            AutoCompleteDetailed(
                itemList = exercisesList,
                selectedItems = selectedItems,
                onToggleItem = onToggleItem
            )
        }
    }
}

@Composable
fun ItemSelectorSection(
    conditionsList: List<Pair<String, String>>,
    bodyPartsList: List<Pair<String, String>>,
    selectedConditions: Set<String>,
    selectedBodyParts: Set<String>,
    onToggleConditions: (String) -> Unit,
    onToggleBodyParts: (String) -> Unit,
    onSearch: (String) -> Unit,
    description: String
) {
    Box(
        modifier = Modifier
        .wrapContentHeight()
        .animateContentSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = description,
                style = typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterableItemSelector(
                itemList = bodyPartsList,
                selectedItems = selectedBodyParts,
                onToggleItem = onToggleBodyParts,
                showSearchIcon = false
            )

            FilterableItemSelector(
                itemList = conditionsList,
                selectedItems = selectedConditions,
                onToggleItem = onToggleConditions,
                onSearch = onSearch
            )
        }
    }
}

@Composable
fun DescriptionSection(description: String, viewModel: PackageCreatorViewModel) {
    Text("Uzupełnij opis", textAlign = TextAlign.Center, style = typography.labelLarge)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(200.dp, 600.dp)
            .padding(vertical = 2.dp)
    ) {
        TextEditorView(initialDescription = description, viewModel = viewModel)
    }
}

@Composable
fun MediaButton(pickMedia: () -> Unit) {
    ActionButton(
        icon = Icons.Outlined.PermMedia,
        label = "Dodaj multimedia",
        onClick = pickMedia
    )
}

@Composable
fun SelectedMediaUris(
    selectedMediaUris: List<Uri>,
    onRemoveMedia: (Uri) -> Unit
) {
    if (selectedMediaUris.isNotEmpty()) {
        val selectedMediaUri = selectedMediaUris.first()
        Box(modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { }) {
            AsyncImage(
                model = selectedMediaUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(RedConfirmed, shape = CircleShape)
                    .clickable { onRemoveMedia(selectedMediaUri) }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove media",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

@Composable
fun DeletePackageButton(showDialog: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Icon(
            imageVector = Icons.Outlined.DeleteOutline,
            contentDescription = "Delete package",
            tint = Color.Red
        )
        Text(text = "Usuń pakiet", color = RedConfirmed, style = typography.labelLarge)
    }
}
