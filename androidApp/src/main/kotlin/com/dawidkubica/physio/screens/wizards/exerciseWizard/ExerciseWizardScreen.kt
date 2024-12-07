package com.dawidkubica.physio.screens.wizards.exerciseWizard

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dawidkubica.physio.screens.sign_in.components.HeaderView
import com.dawidkubica.physio.screens.wizards.viewmodels.ExerciseCreatorViewModel
import com.dawidkubica.physio.ui.components.FullScreenLoader
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.ghost_white
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ExerciseWizardScreen(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExerciseCreatorViewModel,
    isEditor: Boolean = false,
    isEditorNextStep: Boolean = false
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    LaunchedEffect(Unit) {
        when {
            isEditorNextStep -> {
                viewModel.getExerciseDetails()
                viewModel.loadEquipmentList()
                viewModel.loadCondition()
            }

            isEditor -> viewModel.loadExercises(false)
            else -> {
                viewModel.loadEquipmentList()
                viewModel.loadCondition()
            }
        }
    }

    if (isLoading) {
        FullScreenLoader()
    } else {
        WizardContent(
            navigate = navigate,
            popBackStack = popBackStack,
            viewModel = viewModel,
            context = context,
            isEditor = isEditor,
            isUploading = isUploading,
            isEditorNextStep = isEditorNextStep
        )
    }
}

@Composable
fun WizardContent(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: ExerciseCreatorViewModel,
    context: Context,
    isUploading: Boolean,
    isEditor: Boolean,
    isEditorNextStep: Boolean
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (header, wizardForm, navigationButtons) = createRefs()

        HeaderView(
            modifier = Modifier
                .height(320.dp)
                .fillMaxWidth()
                .constrainAs(header) {
                    top.linkTo(parent.top)
                },
            200, 0.7f
        )

        Card(
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(wizardForm) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(navigationButtons.top)
                }
        ) {
            ExerciseFormSelector(
                isEditor = isEditor,
                isEditorNextStep = isEditorNextStep,
                navigate = navigate,
                popBackStack = popBackStack,
                viewModel = viewModel
            )
        }

        NavigationButtons(
            isUploading = isUploading,
            onCreateOrEditClick = {
                val title = viewModel.exerciseTitle.value.toString()
                val description = viewModel.exerciseDescription.value.toString()
                val selectedEquipment = viewModel.selectedEquipment.value.toList()

                when {
                    isEditor -> {
                        if (!viewModel.hasSelectedExercise()) {
                            viewModel.showMessage("Nie wybrano ćwiczenia do edycji")
                        } else {
                            viewModel.onEditExerciseContinueClick(navigate)
                        }
                    }

                    isEditorNextStep -> {
                        viewModel.validateFields(
                            title = title,
                            description = description,
                            selectedEquipment = selectedEquipment
                        ) { isValid ->
                            if (isValid) {
                                viewModel.onUpdateExerciseClick(context, navigate)
                            }
                        }
                    }

                    else -> {
                        viewModel.validateFields(
                            title = title,
                            description = description,
                            selectedEquipment = selectedEquipment
                        ) { isValid ->
                            if (isValid) {
                                viewModel.onCreateExerciseClick(context, navigate)
                            }
                        }
                    }
                }
            },
            onBackClick = { viewModel.onGoBackClick(popBackStack) },
            buttonText = if (isEditorNextStep || isEditor) "Edytuj" else "Utwórz",
            modifier = Modifier.constrainAs(navigationButtons) {
                bottom.linkTo(parent.bottom)
            }
        )
    }
}

@Composable
fun ExerciseFormSelector(
    isEditor: Boolean,
    isEditorNextStep: Boolean,
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: ExerciseCreatorViewModel
) {
    when {
        isEditor -> ExerciseEditorForm(navigate, popBackStack, viewModel = viewModel)
        else -> ExerciseDetailsForm(
            navigate,
            popBackStack,
            isEditorNextStep = isEditorNextStep,
            viewModel = viewModel
        )
    }
}

@Composable
fun NavigationButtons(
    modifier: Modifier = Modifier,
    onCreateOrEditClick: () -> Unit,
    onBackClick: () -> Unit,
    isUploading: Boolean = false,
    buttonText: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Button(
            onClick = onCreateOrEditClick,
            modifier = Modifier.weight(2f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isUploading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text(
                    text = buttonText,
                    color = Color.White,
                    style = typography.labelLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Cofnij",
                color = Color.White,
                style = typography.labelLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

