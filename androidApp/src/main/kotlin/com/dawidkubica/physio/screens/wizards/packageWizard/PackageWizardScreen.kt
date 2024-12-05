package com.dawidkubica.physio.screens.wizards.packageWizard

import android.annotation.SuppressLint
import android.util.Log
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
import com.dawidkubica.physio.screens.wizards.viewmodels.PackageCreatorViewModel
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.ghost_white
import com.dawidkubica.physio.ui.theme.typography

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PackageWizardScreen(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PackageCreatorViewModel,
    assignToPerson: Boolean = false,
    isEditor: Boolean = false,
    isEditorNextStep: Boolean = false
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

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
            isEditor -> viewModel.loadPackagesList()
            assignToPerson -> {
                viewModel.loadUsersList()
                viewModel.loadPackagesList()
            }

            isEditorNextStep -> {
                viewModel.getPackageDetails()
                viewModel.loadExercises(false)
                viewModel.loadCondition()
                viewModel.loadUsersList()
                viewModel.loadBodyPartsList()
            }

            else -> {
                viewModel.loadExercises(false)
                viewModel.loadCondition()
                viewModel.loadUsersList()
                viewModel.loadBodyPartsList()
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

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
                when {
                    assignToPerson -> PackageAssigningForm(navigate, popBackStack, viewModel)
                    isEditor -> PackageEditorForm(navigate, popBackStack, viewModel)
                    isEditorNextStep -> PackageDetailsForm(
                        navigate,
                        popBackStack,
                        viewModel,
                        isEditor = true
                    )

                    else -> PackageDetailsForm(navigate, popBackStack, viewModel, isEditor = false)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .constrainAs(navigationButtons) {
                        bottom.linkTo(parent.bottom)
                    },
            ) {
                Button(
                    onClick = {
                        when {
                            assignToPerson -> viewModel.onAssignPackageClick(navigate)
                            isEditor -> viewModel.onEditPackageContinueClick(navigate)
                            isEditorNextStep -> {
                                viewModel.onEditPackageClick(navigate, context)
                            }
                            else -> viewModel.onCreatePackageClick(navigate, context)
                        }
                    },
                    modifier = Modifier
                        .weight(2f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = if (assignToPerson) "Przypisz" else if (isEditor || isEditorNextStep) "Edytuj" else "Utwórz",
                        color = Color.White,
                        style = typography.labelLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Button(
                    onClick = { viewModel.onGoBackClick(popBackStack) },
                    modifier = Modifier
                        .weight(1f),
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
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

