package com.dawidkubica.physio.screens.wizards.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dawidkubica.physio.screens.wizards.viewmodels.CreatorWizardViewModel
import com.dawidkubica.physio.ui.icons.Clinical_notes

@Composable
fun AdditionalButtons(
    selectedButton: ButtonType,
    navigate: (String) -> Unit,
    viewModel: CreatorWizardViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        when (selectedButton) {
            ButtonType.EXERCISE -> {
                ActionButton(
                    Icons.Default.AddCircleOutline,
                    "Dodaj ćwiczenie"
                ) { viewModel.onNewExerciseClick(navigate) }

                Spacer(modifier = Modifier.height(10.dp))

                ActionButton(
                    Icons.Default.EditNote,
                    "Edytuj ćwiczenie"
                ) { viewModel.onEditExerciseClick(navigate) }
            }

            ButtonType.PACKAGE -> {
                ActionButton(
                    Icons.Default.AddCircleOutline,
                    "Dodaj pakiet ćwiczeń"
                ) { viewModel.onNewPackageClick(navigate) }

                Spacer(modifier = Modifier.height(10.dp))

                ActionButton(
                    Icons.Default.EditNote,
                    "Edytuj pakiet ćwiczeń"
                ) { viewModel.onEditPackageWizardClick(navigate) }

                Spacer(modifier = Modifier.height(10.dp))

                ActionButton(
                    Clinical_notes,
                    "Przypisz pakiet pacjentowi"
                ) { viewModel.onAssignPackageClick(navigate) }
            }
        }
    }
}