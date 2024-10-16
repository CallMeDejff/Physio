package com.example.physio.screens.wizards.exerciseWizard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.physio.screens.wizards.CreatorWizardViewModel
import com.example.physio.ui.components.AutoComplete
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.typography

@Composable
fun ExerciseEditorForm(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: CreatorWizardViewModel
) {
    val selectedExercise by viewModel.selectedExercises.collectAsState()
    val exercisesList by viewModel.exercisesList.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = buildAnnotatedString {
                    append("Edytor istniejącego ")
                    withStyle(style = SpanStyle(color = colorPrimary)) {
                        append("ćwiczenia.")
                    }
                },
                style = typography.bodyLarge,
            )
        }

        item {
            Box(
                modifier = Modifier
                    .heightIn(200.dp, 400.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Podaj tytuł ćwiczenia",
                        style = typography.labelLarge,
                        modifier = Modifier.height(32.dp)
                    )
                    AutoComplete(
                        itemList = exercisesList,
                        selectedItems = selectedExercise,
                        onToggleItem = { exerciseId ->
                            viewModel.toggleExercises(
                                exerciseId,
                                false
                            )
                        }
                    )
                }
            }
        }
    }
}