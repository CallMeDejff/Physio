package com.example.physio.screens.wizards.exerciseWizard

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PermMedia
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physio.screens.wizards.ActionButton
import com.example.physio.screens.wizards.CreatorWizardViewModel
import com.example.physio.ui.colorPrimary
import com.example.physio.ui.typography

@Composable
fun ExerciseWizardEditorScreen(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatorWizardViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedMediaUris by viewModel.selectedMediaUris.collectAsState()
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        viewModel.addSelectedMedia(context, uris)
    }

    LaunchedEffect(viewModel.wizardMessage) {
        viewModel.wizardMessage.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearLoginMessage()
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Uzupełnij ")

                    withStyle(style = SpanStyle(color = colorPrimary)) {
                        append("opis")
                    }

                    append(" i ")

                    withStyle(style = SpanStyle(color = colorPrimary)) {
                        append("multimedia")
                    }
                },
                style = typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(200.dp, 500.dp)
                    .padding(vertical = 2.dp)
            ) {
                ExerciseEditorView(initialDescription = "")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ActionButton(
                    icon = Icons.Outlined.PermMedia,
                    label = "Dodaj multimedia",
                    onClick = { mediaPickerLauncher.launch("image/*, video/*") },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedMediaUris.isNotEmpty()) {
                selectedMediaUris.forEach { uri ->
                    Text(text = "Wybrano: $uri", style = typography.labelSmall)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.onCreateExerciseClick(navigate, viewModel) },
                    modifier = Modifier
                        .weight(2f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
                ) {
                    Text(
                        text = "Utwórz",
                        color = Color.White,
                        style = typography.labelLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Button(
                    onClick = { viewModel.onExitWizardClick(popBackStack) },
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),

                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
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
    }
}